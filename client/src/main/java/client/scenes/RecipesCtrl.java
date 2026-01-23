package client.scenes;

import client.MyWebSocketClient;
import client.i18n.I18n;
import client.config.Config;
import client.i18n.I18nConfig;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import client.utils.ServerUtils;
import client.utils.search.RecipeSearchMatcher;
import client.utils.search.SearchQueryParser;
import client.utils.*;
import com.google.inject.Inject;
import commons.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.springframework.messaging.simp.stomp.StompSession;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RecipesCtrl implements Initializable {

    private final ServerUtils server;
    private final PrimaryCtrl primaryCtrl;
    private final RecipeListCellFactory cellFactory;
    private final ShoppingListService shoppingListService;

    // fxml link, keep in mind that it must match with the id in scene builder
    // that's the left-hand side
    @FXML
    private ListView<Recipe> recipeList;

    // and this is the right-hand side
    @FXML
    private Label recipeTitle;
    @FXML
    private Spinner<Double> scaler;
    private Map<Long, Double> scalingMap = new HashMap<>();
    @FXML
    private Label recipeKcal;
    @FXML
    private ListView<RecipeIngredient> ingredientsList;
    @FXML
    private ListView<RecipeStep> stepsList;
    @FXML
    private ToggleButton favoritesToggle;
    @FXML
    private Label servingsLabel;

    // i18n buttons and labels
    @FXML
    private Label allRecipesLabel;
    @FXML
    private Button refreshButton;
    @FXML
    private Button addRecipeButton;
    @FXML
    private Button deleteRecipeButton;
    @FXML
    private Button cloneButton;
    @FXML
    private Button printButton;
    @FXML
    private Button editTitleButton;
    @FXML
    private Label ingredientsLabel;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button editIngredientButton;
    @FXML
    private Button deleteIngredientButton;
    @FXML
    private Label preparationLabel;
    @FXML
    private Button addStepButton;
    @FXML
    private Button editStepButton;
    @FXML
    private Button deleteStepButton;
    @FXML
    private Button shopButton;
    @FXML
    private Label scaleTitleLabel;
    @FXML
    private Label servingsTitleLabel;
    @FXML
    private Button editServingsButton;
    @FXML
    private Label servingSizeLabel;
    @FXML
    private TextField searchField;
    @FXML
    private Label languageLabel;
    @FXML
    private ComboBox<Locale> languageCombo;

    private ObservableList<Recipe> allRecipes;
    private FilteredList<Recipe> filteredRecipes;

    private SearchQueryParser searchParser;
    private RecipeSearchMatcher searchMatcher;
    private List<String> filteredLanguages = new ArrayList<>();

    private MyWebSocketClient webSocket = new MyWebSocketClient();
    private StompSession.Subscription currentRecipeSubscription;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     *
     * @param server              the server utils
     * @param primaryCtrl         primaryCtrl
     * @param cellFactory         the cell factory to keep favourites local
     * @param shoppingListService the shopping list manager
     */
    @Inject
    public RecipesCtrl(ServerUtils server, PrimaryCtrl primaryCtrl,
                       RecipeListCellFactory cellFactory, ShoppingListService shoppingListService) {
        this.server = server;
        this.primaryCtrl = primaryCtrl;
        this.cellFactory = cellFactory;
        this.shoppingListService = shoppingListService;
    }

    // runs immediately when the screen opens
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeScaler();
        initializeRecipeList();
        initializeWebSocket();
        initializeLanguage();
    }

    private void initializeLanguage() {
        // Bind labels and buttons to i18n
        allRecipesLabel.textProperty().bind(I18n.createStringBinding("recipes.title"));
        refreshButton.textProperty().bind(I18n.createStringBinding("recipes.refresh"));
        addRecipeButton.textProperty().bind(I18n.createStringBinding("recipes.add"));
        deleteRecipeButton.textProperty().bind(I18n.createStringBinding("recipes.delete"));
        cloneButton.textProperty().bind(I18n.createStringBinding("recipes.clone"));
        printButton.textProperty().bind(I18n.createStringBinding("recipes.print"));
        editTitleButton.textProperty().bind(I18n.createStringBinding("recipes.edit"));
        ingredientsLabel.textProperty().bind(I18n.createStringBinding("recipes.ingredients"));
        addIngredientButton.textProperty().bind(I18n.createStringBinding("recipes.add"));
        editIngredientButton.textProperty().bind(I18n.createStringBinding("recipes.edit"));
        deleteIngredientButton.textProperty().bind(I18n.createStringBinding("recipes.delete"));
        preparationLabel.textProperty().bind(I18n.createStringBinding("recipes.preparation"));
        addStepButton.textProperty().bind(I18n.createStringBinding("recipes.add"));
        editStepButton.textProperty().bind(I18n.createStringBinding("recipes.edit"));
        deleteStepButton.textProperty().bind(I18n.createStringBinding("recipes.delete"));
        languageLabel.textProperty().bind(I18n.createStringBinding("recipes.languageLabel"));
        shopButton.textProperty().bind(I18n.createStringBinding("recipes.shop"));
        editServingsButton.textProperty().bind(I18n.createStringBinding("recipes.edit"));
        scaleTitleLabel.textProperty().bind(I18n.createStringBinding("recipes.scale"));
        servingsTitleLabel.textProperty().bind(I18n.createStringBinding("recipes.servings"));
        
        // Bind recipe title default to i18n
        recipeTitle.textProperty().bind(I18n.createStringBinding("recipes.selectRecipe"));

        // Bind favorites toggle to i18n
        favoritesToggle.textProperty().bind(
                javafx.beans.binding.Bindings.when(favoritesToggle.selectedProperty())
                        .then(I18n.createStringBinding("recipes.showAll"))
                        .otherwise(I18n.createStringBinding("recipes.showFavorite"))
        );

        languageCombo.getItems().setAll(I18nConfig.SUPPORTED);
        languageCombo.setCellFactory(_ -> LanguageComboUtils.createFlagCell());
        languageCombo.setButtonCell(LanguageComboUtils.createFlagCell());
        languageCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                return "";
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });
        // listener to update the languageTag of the recipe when the user edits it
        languageCombo.valueProperty().addListener((obs,
                                                   oldLang, newLang) -> {
            Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
            if (currentRecipe != null && newLang != null &&
                    !newLang.toLanguageTag().equals(currentRecipe.getLanguageTag())) {
                currentRecipe.setLanguageTag(newLang.toLanguageTag());
                try {
                    server.updateRecipe(currentRecipe);
                } catch (Exception e) {
                    showAlert(I18n.get("alert.serverError"),
                            I18n.get("alert.couldNotUpdateRecipe"));
                }
            }
        });
        I18n.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            Recipe current = recipeList.getSelectionModel().getSelectedItem();
            if (current != null) {
                showRecipeDetails(current);
            }
        });
    }

    /**
     * initializes the recipe list
     */
    private void initializeRecipeList() {
        allRecipes = FXCollections.observableArrayList();
        filteredRecipes = new FilteredList<>(allRecipes);
        recipeList.setItems(filteredRecipes);
        recipeList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showRecipeDetails(newValue)
        );
        recipeList.setCellFactory(cellFactory);

        // Initialize search components
        searchParser = new SearchQueryParser();
        updateSearchMatcher();

        // Add real-time search filtering
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            applySearchFilter();
        });

        // Add Escape key handler to clear search and unfocus
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
                searchField.getParent().requestFocus();
                event.consume();
            }
        });

        // Load saved language filter from config
        List<String> savedLanguages = cellFactory.getConfigManager().getConfig()
                .getSelectedRecipeLanguages();
        if (savedLanguages != null && !savedLanguages.isEmpty()) {
            filteredLanguages = new ArrayList<>(savedLanguages);
        } else {
            // Default to all supported languages
            filteredLanguages = new ArrayList<>(
                    I18nConfig.SUPPORTED.stream()
                            .map(Locale::toLanguageTag)
                            .toList()
            );
        }

        // the ui list contains our data, currently empty
        refresh();
    }

    /**
     * Parse the text inside the spinner
     * If it's valid, we use that, if not, don't change the value
     */
    private void commitScalerValue() {
        String text = scaler.getEditor().getText();
        try {
            Double val = Double.parseDouble(text);
            scaler.getValueFactory().setValue(val);
        } catch (NumberFormatException e) {
            scaler.getEditor().setText(scaler.getValue().toString());
        }
    }

    /**
     * initializes the scaler
     */
    private void initializeScaler() {
        scaler.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 10.0, 1.0, 0.5)
        );
        scaler.setEditable(true);
        scaler.getEditor().setTextFormatter(createNumberTextFormatter());
        scaler.getEditor().setOnAction(e -> commitScalerValue());
        scaler.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitScalerValue();
            }
        });
        scaler.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Recipe current = recipeList.getSelectionModel().getSelectedItem();
                if (current != null) {
                    scalingMap.put(current.getId(), newVal);
                    int baseServings = current.getServings() > 0 ? current.getServings() : 1;
                    int scaledServings = (int) Math.round(baseServings * newVal);
                    servingsLabel.setText(String.valueOf(scaledServings));
                }
                updateVisuals(newVal);
            }
        });
    }


    /**
     * Creates a text formatter that only allows optional decimal point
     * and optional minus sign
     *
     * @return a TextFormatter for numeric validation
     */
    private TextFormatter<String> createNumberTextFormatter() {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("-?[0-9]*(\\.[0-9]*)?") ? change : null;
        });
    }

    /**
     * initializes the websocket
     */
    private void initializeWebSocket() {
        webSocket.connect("ws://localhost:8080/websocket");

        webSocket.subscribe("/topic/recipes", Object.class, obj -> {
            System.out.println("Something changed in the list!");
            refresh();
        });

        recipeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (currentRecipeSubscription != null) {
                currentRecipeSubscription.unsubscribe();
                currentRecipeSubscription = null;
            }
            if (newVal != null) {
                currentRecipeSubscription = webSocket.subscribe
                        ("/topic/recipes/" + newVal.getId(), Recipe.class, r -> {
                            showRecipeDetails(r);
                        });
            } else {
                showRecipeDetails(null);
            }
        });
    }

    /**
     * shows an alert
     *
     * @param title   the title of the alert
     * @param content the content of the alert
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Refreshes the list of recipes
     */
    public void refresh() {
        Recipe selected = recipeList.getSelectionModel().getSelectedItem();
        Long selectedId = (selected != null) ? selected.getId() : null;
        var recipes = server.getRecipes();
        allRecipes.setAll(recipes);
        // Same filter in toggleFavoritesFilter
        if (favoritesToggle.isSelected()) {
            filteredRecipes.setPredicate(recipe ->
                    cellFactory.isFavorite(recipe.getId())
            );
        } else {
            filteredRecipes.setPredicate(null); // show all if toggle off
        }
        if (selectedId != null) {
            Recipe newRecipe = recipeList.getItems().stream()
                    .filter(r -> r.getId() == selectedId)
                    .findFirst()
                    .orElse(null);
            if (newRecipe != null) {
                recipeList.getSelectionModel().select(newRecipe);
                showRecipeDetails(newRecipe);
            }
        }
        // Apply current search/favorites filter
        applySearchFilter();
        Config favConfig = cellFactory.getConfigManager().getConfig();
        Map<Long, String> favRecipes = favConfig.getFavoriteRecipes();
        List<Long> favRecipeIds = new ArrayList<>(favRecipes.keySet());
        boolean favRecipeExists;
        for (Long recipeID : favRecipeIds) {
            favRecipeExists = false;
            for (Recipe recipe : allRecipes) {
                if (recipe.getId() == recipeID) {
                    favRecipeExists = true;
                    String currentName = recipe.getName();
                    String storedName = favRecipes.get(recipeID);
                    if (!currentName.equals(storedName)) {
                        favConfig.addFavorite(recipeID, currentName);
                    }
                    break;
                }
            }
            if (!favRecipeExists) {
                String recipeName = favRecipes.get(recipeID);
                if (recipeName == null) {
                    recipeName = I18n.get("recipes.defaultRecipeName");
                }

                favConfig.removeFavorite(recipeID);
                showAlert(I18n.get("alert.favouriteDeleted"),
                        I18n.get("alert.mournOverDeletedFavRecipePart1") +
                                " " + recipeName + " " +
                                I18n.get("alert.mournOverDeletedFavRecipePart2"));
            }
        }
        cellFactory.getConfigManager().save();
    }

    /**
     * Updates the search matcher with current locale and favorites.
     */
    private void updateSearchMatcher() {
        Config config = cellFactory.getConfigManager().getConfig();
        String locale = config.getPrefLocale();
        if (locale == null || locale.isEmpty()) {
            locale = Locale.getDefault().toLanguageTag();
        }
        Set<Long> favorites = config.getFavoriteRecipeIds();
        searchMatcher = new RecipeSearchMatcher(locale, favorites);
    }

    /**
     * Applies the current search filter to the recipe list.
     */
    private void applySearchFilter() {
        String query = searchField.getText();
        updateSearchMatcher();

        if (query == null || query.trim().isEmpty()) {
            // No search query - apply language and favorites filter
            filteredRecipes.setPredicate(recipe -> {
                // Language filter
                if (!matchesLanguageFilter(recipe)) {
                    return false;
                }
                // Favorites filter
                if (favoritesToggle.isSelected() && !cellFactory.isFavorite(recipe.getId())) {
                    return false;
                }
                return true;
            });
        } else {
            // Apply search filter (combined with language filter)
            var parsedQuery = searchParser.parse(query);
            filteredRecipes.setPredicate(recipe -> {
                if (!matchesLanguageFilter(recipe)) {
                    return false;
                }
                return searchMatcher.matches(recipe, parsedQuery);
            });
        }
    }

    /**
     * Checks if a recipe matches the current language filter.
     */
    private boolean matchesLanguageFilter(Recipe recipe) {
        if (filteredLanguages.isEmpty()) {
            return false;
        }
        String recipeLanguage = recipe.getLanguageTag();
        if (recipeLanguage == null || recipeLanguage.isEmpty()) {
            return true; // Recipes without language match all
        }
        for (String lang : filteredLanguages) {
            if (recipeLanguage.toLowerCase().startsWith(lang.toLowerCase().split("-")[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the filtered languages from the language menu.
     *
     * @param languages the list of language tags to filter by
     */
    public void setFilteredLanguages(List<String> languages) {
        this.filteredLanguages = new ArrayList<>(languages);
        applySearchFilter();
    }

    /**
     * Toggle between showing all recipes and favorites only
     */
    @FXML
    public void toggleFavoritesFilter() {
        applySearchFilter();
    }

    /**
     * Shows the details of a recipe (ingredients, preparation steps)
     *
     * @param recipe
     */
    private void showRecipeDetails(Recipe recipe) {
        if (recipe == null) {
            // When no recipe is selected, keep the i18n binding active
            // This ensures the default text updates with language changes
            recipeTitle.textProperty().unbind();
            recipeTitle.textProperty().bind(I18n.createStringBinding("recipes.selectRecipe"));
            recipeKcal.setText("");
            ingredientsList.getItems().clear();
            stepsList.getItems().clear();

            recipeKcal.setVisible(false);

            recipeKcal.setText("");

            return;
        }
        String tag = recipe.getLanguageTag();
        if (tag != null && !tag.isBlank()) {
            Locale recipeLocale = Locale.forLanguageTag(tag);
            languageCombo.getItems().stream().filter(l -> l.toLanguageTag()
                    .equalsIgnoreCase(recipeLocale.toLanguageTag())).findFirst().ifPresentOrElse(
                            l -> languageCombo.getSelectionModel().select(l),
                            () -> languageCombo.getSelectionModel().clearSelection()
            );
        } else {
            languageCombo.getSelectionModel().clearSelection();
        }

        // we check to see the stored scale value (if not just put 1)
        Double savedFactor = scalingMap.getOrDefault(recipe.getId(), 1.0);
        scaler.getValueFactory().setValue(savedFactor);  // this will call updateVisuals

        // Unbind the default i18n binding and set the recipe name directly
        recipeTitle.textProperty().unbind();
        recipeTitle.setText(recipe.getName());

        int currentServings = recipe.getServings() > 0 ? recipe.getServings() : 1;
        int displayServings = (int) Math.round(currentServings * savedFactor);
        servingsLabel.setText(String.valueOf(displayServings));

        java.math.BigDecimal totalKcal = recipe.estimatedRecipeKcal();
        recipeKcal.setText(I18n.get("recipes.kcal").replace("{0}",
                        totalKcal.stripTrailingZeros().toPlainString())
        );
        recipeKcal.setVisible(true);

        int baseServings = recipe.getServings() > 0 ? recipe.getServings() : 1;
        java.math.BigDecimal kcalPerServing;
        try {
            kcalPerServing = totalKcal.divide(
                    java.math.BigDecimal.valueOf(baseServings),
                    0,
                    java.math.RoundingMode.HALF_UP
            );
        } catch (Exception e) {
            kcalPerServing = java.math.BigDecimal.ZERO;
        }
        servingSizeLabel.setText(
                I18n.get("recipes.servingSize").replace("{0}", kcalPerServing.toPlainString())
        );
        servingSizeLabel.setVisible(true);

        ingredientsList.getItems().clear();
        for (var ing : recipe.getIngredients()) {
            ingredientsList.getItems().add(ing);  // add RecipeIngredient objects, not strings
        }
        stepsList.getItems().clear();
        for (RecipeStep step : recipe.getSteps()) {
            stepsList.getItems().add(step);  // add RecipeStep objects, not strings
        }
    }

    private void updateVisuals(double factor) {
        Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
        if (currentRecipe == null) {
            return;
        }
        ingredientsList.setCellFactory(param -> new ListCell<RecipeIngredient>() {
            @Override
            protected void updateItem(RecipeIngredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Measurement displayMeasurement = item.getAmount().scale(factor)
                            .normalizeMeasurement();

                    RecipeIngredient aux = new RecipeIngredient();
                    aux.setIngredient(item.getIngredient());
                    aux.setId(item.getId());
                    aux.setAmount(displayMeasurement);
                    setText(aux.toString());
                }
            }
        });
    }

    /**
     * This will add the selected recipe to the shopping list
     */
    @FXML
    public void addToShoppingList() {
        Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
        if (currentRecipe == null) {
            showAlert(I18n.get("dialog.addToShoppingList.noRecipeSelected"),
                    I18n.get("dialog.addToShoppingList.selectRecipe"));
            return;
        }
        Double factor = scaler.getValue();
        if (factor == null) {
            factor = 1.0;
        }

        ObservableList<ShoppingItem> temp = FXCollections.observableArrayList();
        for (RecipeIngredient ri : currentRecipe.getIngredients()) {
            Measurement m = ri.getAmount().scale(factor).normalizeMeasurement();
            ShoppingItem item = new ShoppingItem(
                    ri.getIngredient(),
                    m,
                    currentRecipe.getName()
            );
            item.setChecked(false);
            temp.add(item);
        }
        FXCollections.sort(temp, (i1, i2) ->
                i1.getIngredient().getName().compareToIgnoreCase(i2.getIngredient().getName())
        );

        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(10));

        ListView<ShoppingItem> overviewList = new ListView<>();
        overviewList.setItems(temp);
        overviewList.setPrefHeight(300);
        overviewList.setPrefWidth(400);
        overviewList.setCellFactory(new ShoppingListCellFactory());
        Button addExtraButton = new Button();
        addExtraButton.textProperty().bind(I18n.createStringBinding("shoppingList.addExtra"));
        addExtraButton.setMaxWidth(Double.MAX_VALUE);

        addExtraButton.setOnAction(e -> {
            var allIngredients = server.getIngredients();
            Optional<RecipeIngredient> result = IngredientDialog.show(allIngredients, server);
            result.ifPresent(ri -> {
                ShoppingItem newItem = new ShoppingItem(
                        ri.getIngredient(),
                        ri.getAmount(),
                        currentRecipe.getName()
                );
                temp.add(newItem);
                FXCollections.sort(temp, (i1, i2) -> i1.getIngredient().getName()
                                .compareToIgnoreCase(i2.getIngredient().getName()));
            });
        });
        dialogContent.getChildren().addAll(overviewList, addExtraButton);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(I18n.get("dialog.addToShoppingList.title"));
        dialog.setHeaderText(I18n.get("dialog.addToShoppingList.header")
                .replace("{0}", currentRecipe.getName()));

        ButtonType addButtonType = new ButtonType(I18n.get("dialog.addToShoppingList.addItems"),
                ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(dialogContent);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType && !temp.isEmpty()) {
            shoppingListService.addItems(temp);
        }
    }

    /**
     * Edit the number of servings of the recipe
     */
    @FXML
    public void onEditServingsClicked() {
        Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
        if (currentRecipe == null) {
            showAlert(I18n.get("alert.noRecipeSelected"), I18n.get("alert.selectRecipeToEdit"));
            return;
        }
        TextInputDialog dialog = new TextInputDialog(String.valueOf(currentRecipe.getServings()));
        dialog.setTitle(I18n.get("dialog.editServings.title"));
        dialog.setHeaderText(I18n.get("dialog.editServings.header"));
        dialog.setContentText(null);
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                int newServings = Integer.parseInt(input);
                if (newServings < 1) {
                    showAlert(I18n.get("alert.invalidInput"),
                            I18n.get("alert.servingsAtLeast1"));
                    return;
                }
                currentRecipe.setServings(newServings);
                scalingMap.put(currentRecipe.getId(), 1.0);
                scaler.getValueFactory().setValue(1.0);
                try {
                    server.updateRecipe(currentRecipe);
                } catch (Exception e) {
                    showAlert(I18n.get("alert.serverError"),
                            I18n.get("alert.couldNotUpdateRecipe"));
                }
            } catch (NumberFormatException e) {
                showAlert(I18n.get("alert.invalidInput"),
                        I18n.get("alert.enterValidNumber"));
            }
        });
    }

    /**
     * When the add button for an ingredient is pressed, the user can add a new ingredient
     * in an identical fashion as when creating a recipe
     */
    @FXML
    public void addIngredient() {
        Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
        if (currentRecipe == null) {
            showAlert(I18n.get("alert.noSelection"), I18n.get("alert.selectRecipeFirst"));
            return;
        }
        var allIngredients = server.getIngredients();
        Optional<RecipeIngredient> result = IngredientDialog.show(allIngredients, server);
        result.ifPresent(recIng -> {
            try {
                recIng.setRecipe(currentRecipe);
                currentRecipe.getIngredients().add(recIng);
                server.updateRecipe(currentRecipe);
            } catch (Exception e) {
                showAlert(I18n.get("alert.error"), I18n.get("alert.couldNotAddIngredient"));
            }
        });
    }

    /**
     * Action for when the user presses on delete for a certain ingredient withing a recipe
     */
    @FXML
    public void deleteIngredient() {
        Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
        RecipeIngredient selectedIngredient = ingredientsList.getSelectionModel().getSelectedItem();
        if (currentRecipe == null || selectedIngredient == null) {
            showAlert(I18n.get("alert.error"), I18n.get("alert.selectRecipeAndIngredient"));
            return;
        }
        currentRecipe.getIngredients().remove(selectedIngredient);

        try {
            server.updateRecipe(currentRecipe);
        } catch (Exception e) {
            showAlert(I18n.get("alert.error"), I18n.get("alert.couldNotDeleteIngredient"));
        }
    }

    /**
     * When the edit button for an ingredient is pressed, the user can edit the amount
     */
    @FXML
    public void editIngredient() {
        Recipe currentRecipe = recipeList.getSelectionModel().getSelectedItem();
        RecipeIngredient selectedItem = ingredientsList.getSelectionModel().getSelectedItem();

        if (currentRecipe == null || selectedItem == null) {
            showAlert(I18n.get("alert.error"), I18n.get("alert.selectRecipeAndIngredient"));
            return;
        }

        Optional<Measurement> result = IngredientDialog.showEditAmount(selectedItem.getAmount());
        result.ifPresent(newMeasurement -> {
            RecipeIngredient actualItem = currentRecipe.getIngredients().stream()
                    .filter(i -> i.getId() == selectedItem.getId()) // Match IDs
                    .findFirst()
                    .orElse(null);
            actualItem.setAmount(newMeasurement);
            try {
                server.updateRecipe(currentRecipe);
            } catch (Exception e) {
                showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotEditIngredient"));
            }
        });
    }

    /**
     * When the add button is pressed, the user can add a new step to the recipe.
     */
    @FXML
    public void onAddStepClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            showAlert(I18n.get("alert.noRecipeSelected"), I18n.get("alert.selectRecipeToAddStep"));
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(I18n.get("dialog.addStep.header"));
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String stepText = result.get();
            if (stepText.isEmpty()) {
                showAlert(I18n.get("alert.error"), I18n.get("alert.stepDescriptionEmpty"));
                return;
            }
            RecipeStep step = new RecipeStep();
            step.setText(stepText);
            step.setOrderIndex(recipe.getSteps().size());
            step.setRecipe(recipe);
            recipe.getSteps().add(step);

            try {
                server.updateRecipe(recipe);
            } catch (Exception e) {
                showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotSaveStep"));
                return;
            }
        }
    }

    /**
     * When delete button is clicked and the step is deleted
     */
    @FXML
    public void onDeleteStepClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            showAlert(I18n.get("alert.noRecipeSelected"), I18n.get("alert.selectRecipeToEdit"));
            return;
        }

        RecipeStep selectedStep = stepsList.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            showAlert(I18n.get("alert.noStepSelected"), I18n.get("alert.selectStepToDelete"));
            return;
        }

        recipe.getSteps().remove(selectedStep);
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            recipe.getSteps().get(i).setOrderIndex(i);
        }

        try {
            server.updateRecipe(recipe);
        } catch (Exception e) {
            showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotDeleteStep"));
            return;
        }
    }

    /**
     * Edit button is clicked, a box appears where uses edits step.
     */
    @FXML
    public void onEditStepClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            showAlert(I18n.get("alert.noRecipeSelected"), I18n.get("alert.selectRecipeToEdit"));
            return;
        }
        RecipeStep selectedStep = stepsList.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            showAlert(I18n.get("alert.noStepSelected"), I18n.get("alert.selectStepToEdit"));
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedStep.getText());
        dialog.setHeaderText(I18n.get("dialog.editStep.header"));
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newText = result.get().trim();
            if (newText.isEmpty()) {
                return;
            }
            // the problem was that the first time works, but we send 2 messages on 2 channels,
            // and it happens that the right part shows old objects, and then refresh happens,
            // and we get different objects on the left
            RecipeStep actualStep = recipe.getSteps().stream()
                    .filter(s -> s.getId() == selectedStep.getId())
                    .findFirst()
                    .orElse(null);
            actualStep.setText(newText);

            try {
                server.updateRecipe(recipe);
            } catch (Exception e) {
                showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotEditStep"));
                return;
            }
        }
    }

    /**
     * When the edit button is pressed the user can edit the title of the existing recipe
     */
    @FXML
    public void onEditTitleClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            showAlert(I18n.get("alert.noRecipeSelected"), I18n.get("alert.selectRecipeToEdit"));
            return;
        }
        TextInputDialog dialog = new TextInputDialog(recipe.getName());
        dialog.setHeaderText(I18n.get("dialog.editTitle.header"));
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 150);

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newTitle = result.get().trim();
            if (newTitle.isEmpty()) {
                showAlert(I18n.get("alert.validationError"), I18n.get("alert.titleEmpty"));
                return;
            }
            recipe.setName(newTitle);
            try {
                server.updateRecipe(recipe);
            } catch (Exception e) {
                showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotSaveRecipe"));
                return;
            }
        }

    }

    /**
     * Switches to add recipe
     */
    @FXML
    public void switchToAddRecipe() {
        primaryCtrl.showAddRecipe();
    }

    /**
     * Clones the recipe, making a new recipe with same data and selecting it in the view.
     */
    @FXML
    public void onClonePressed() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            showAlert(I18n.get("alert.noRecipeSelected"), I18n.get("alert.selectRecipeToClone"));
            return;
        }

        // TODO(calco): When adding a recipe, do we clone the ingredients and steps as well?
        // "[...] so I can give it my own twist without changing the original [...]"

        Recipe clonedRecipe = recipe.cloneRecipe();


        // TODO(calco): This shouldn't be as hard coded
        String cloneName = clonedRecipe.getName() + " " + I18n.get("dialog.clone.suffix");
        TextInputDialog dialog = new TextInputDialog(cloneName);
        dialog.setHeaderText(I18n.get("dialog.clone.header"));
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get();
            if (newName.isEmpty()) {
                showAlert(I18n.get("alert.error"), I18n.get("alert.noNameProvided"));
                return;
            }

            clonedRecipe.setName(newName);

            try {
                Recipe rServer = server.addRecipe(clonedRecipe);

                // TODO(calco): Stupid workaround for stuff
                refresh();
                Recipe toSelect = null;
                for (Recipe r : allRecipes) {
                    if (r.getId() == rServer.getId()) {
                        toSelect = r;
                        break;
                    }
                }
                recipeList.getSelectionModel().select(toSelect);
                recipeList.scrollTo(toSelect);
                recipeList.refresh();
                recipeList.requestFocus();
                showRecipeDetails(toSelect);
            } catch (Exception e) {
                showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotClone"));
            }
        }
    }

    /**
     * Opens a new dialogue to save a file with all recipe information
     * in a user specified ocation and format.
     */
    @FXML
    public void onPrintPressed() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("fileChooser.saveRecipe"));
        fileChooser.setInitialFileName(recipe.getName());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md")
//              TODO(calco): Add support for PDF export (use markdown to pdf storage)
//               new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"),
        );

        Window owner = recipeList.getScene().getWindow();
        File file = fileChooser.showSaveDialog(owner);

        if (file != null) {
            try (PrintWriter out = new PrintWriter(file, StandardCharsets.UTF_8)) {
                if (file.getName().endsWith(".md")) {
                    out.print(recipe.toExportableMarkdown());
                } else if (file.getName().endsWith(".txt")) {
                    out.print(recipe.toExportablePlaintext());
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(I18n.get("alert.saveFailed"));
                alert.setHeaderText(I18n.get("alert.couldNotSaveFile"));
                alert.setContentText(I18n.get("alert.errorPrefix")
                        .replace("{0}", e.getMessage()));
                alert.showAndWait();
            }
        }
    }

    /**
     * when a recipe is selected and the button is pressed, it gets deleted.
     */
    @FXML
    public void onDeleteRecipePressed() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(I18n.get("alert.noRecipeSelected"));
            alert.setContentText(I18n.get("alert.selectRecipeToDelete"));
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(I18n.get("dialog.deleteRecipe.title"));
        confirm.setHeaderText(I18n.get("dialog.deleteRecipe.header"));
        confirm.setContentText(I18n.get("dialog.deleteRecipe.content"));
        confirm.setGraphic(null);

        ButtonType deleteButton = new ButtonType(
                I18n.get("dialog.deleteRecipe.delete"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(
                I18n.get("dialog.deleteRecipe.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(deleteButton, cancelButton);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != deleteButton) {
            return;
        }

        try {
            server.deleteRecipe(recipe);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(I18n.get("alert.serverError"));
            alert.setHeaderText(I18n.get("alert.couldNotDeleteRecipe"));
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    /**
     * given a recipe id, it returns the name of the recipe
     *
     * @param recipeId the id of the recipe
     * @return the name of the recipe if it exists, else null
     */
    public String findRecipeNameById(long recipeId) {
        for (Recipe recipe : allRecipes) {
            if (recipe.getId() == recipeId) {
                return recipe.getName();
            }
        }
        return null; // or throw exception if not found
    }

}