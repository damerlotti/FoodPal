package client.scenes;

import client.config.ConfigManager;
import client.i18n.I18n;
import client.i18n.I18nConfig;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainLayoutCtrl implements Initializable {

    private final PrimaryCtrl primaryCtrl;
    private final ConfigManager configManager;

    @FXML
    private StackPane contentArea;
    
    @FXML
    private ComboBox<Locale> languageCombo;

    @FXML
    private MenuButton recipeLanguageMenu;

    @FXML
    private Button recipesButton;

    @FXML
    private Button ingredientsButton;

    @FXML
    private Button shoppingListButton;

    private final List<Locale> selectedRecipeLanguages = new ArrayList<>();

    /**
     * Constructor which injects the PrimaryCtrl and ConfigManager.     
     * @param primaryCtrl the primary controller
     * @param configManager the config manager for persisting settings
     */
    @Inject
    public MainLayoutCtrl(PrimaryCtrl primaryCtrl, ConfigManager configManager) {
        this.primaryCtrl = primaryCtrl;
        this.configManager = configManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind button text to i18n
        recipesButton.textProperty().bind(I18n.createStringBinding("nav.recipes"));
        ingredientsButton.textProperty().bind(I18n.createStringBinding("nav.ingredients"));
        shoppingListButton.textProperty().bind(I18n.createStringBinding("nav.shoppingList"));

        // Initialize recipe language multi-select menu
        initializeRecipeLanguageMenu();

        // Initialise combo box
        languageCombo.getItems().setAll(I18nConfig.SUPPORTED);
        languageCombo.setCellFactory(_lv -> createFlagCell());
        
        languageCombo.getSelectionModel().select(I18n.getLocale());

        languageCombo.valueProperty().addListener((obs, oldL, newL) -> {
            if (newL != null) {
                I18n.setLocale(newL);
            }
        });

        // show language name in its own language
        languageCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Locale locale) {
                if (locale == null) {
                    return "";
                }
                return capitalize(locale.getDisplayName(locale));
            }
            @Override public Locale fromString(String s) {
                return null;
            }
        });
//        
//        languageCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
//            @Override protected void updateItem(Locale item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? null : capitalize(item.getDisplayName(item)));
//            }
//        });
//        
        languageCombo.setButtonCell(languageCombo.getCellFactory().call(null));
    }

    /**
     * Helper method to replace the center content
     * @param content the actual visuals
     */
    public void setCenter(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    /**
     * Initializes the recipe language multi-select menu.
     */
    private void initializeRecipeLanguageMenu() {
        // Load saved languages from config, or default to all
        List<String> savedLanguages = configManager.getConfig().getSelectedRecipeLanguages();
        boolean useDefaults = savedLanguages == null || savedLanguages.isEmpty();

        if (useDefaults) {
            selectedRecipeLanguages.addAll(I18nConfig.SUPPORTED);
        } else {
            for (String tag : savedLanguages) {
                Locale locale = Locale.forLanguageTag(tag);
                if (I18nConfig.SUPPORTED.contains(locale)) {
                    selectedRecipeLanguages.add(locale);
                }
            }
            // If no valid languages found, default to all
            if (selectedRecipeLanguages.isEmpty()) {
                selectedRecipeLanguages.addAll(I18nConfig.SUPPORTED);
            }
        }

        for (Locale locale : I18nConfig.SUPPORTED) {
            CheckMenuItem item = new CheckMenuItem();
            item.setText(getLanguageDisplayName(locale));
            item.setSelected(selectedRecipeLanguages.contains(locale));
            item.setUserData(locale);

            item.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    if (!selectedRecipeLanguages.contains(locale)) {
                        selectedRecipeLanguages.add(locale);
                    }
                } else {
                    selectedRecipeLanguages.remove(locale);
                }
                updateRecipeLanguageMenuText();
                saveSelectedLanguages();
                primaryCtrl.onRecipeLanguagesChanged(getSelectedLanguageTags());
            });

            recipeLanguageMenu.getItems().add(item);
        }

        updateRecipeLanguageMenuText();
    }

    /**
     * Saves the selected languages to the config.
     */
    private void saveSelectedLanguages() {
        configManager.getConfig().setSelectedRecipeLanguages(getSelectedLanguageTags());
        configManager.save();
    }

    /**
     * Updates the menu button text to show selected language codes.
     */
    private void updateRecipeLanguageMenuText() {
        if (selectedRecipeLanguages.isEmpty()) {
            recipeLanguageMenu.setText("—");
        } else if (selectedRecipeLanguages.size() == I18nConfig.SUPPORTED.size()) {
            recipeLanguageMenu.setText("All");
        } else if (selectedRecipeLanguages.size() == 1) {
            recipeLanguageMenu.setText(getLanguageDisplayName(selectedRecipeLanguages.get(0)));
        } else {
            String codes = selectedRecipeLanguages.stream()
                    .map(this::getLanguageCode)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("—");
            recipeLanguageMenu.setText(codes);
        }
    }

    /**
     * Returns the language name in its own language (e.g., "Română" for Romanian).
     */
    private String getLanguageDisplayName(Locale locale) {
        String name = locale.getDisplayLanguage(locale);
        return capitalize(name);
    }

    /**
     * Returns the 2-letter language code for display (capitalized).
     */
    private String getLanguageCode(Locale locale) {
        String lang = locale.getLanguage().toUpperCase();
        if (lang.length() >= 2) {
            return lang.substring(0, 1).toUpperCase() + lang.substring(1, 2).toLowerCase();
        }
        return lang;
    }

    /**
     * Returns the list of selected language tags.
     * @return the list of selected language tags
     */
    public List<String> getSelectedLanguageTags() {
        return selectedRecipeLanguages.stream()
                .map(Locale::toLanguageTag)
                .toList();
    }

    /**
     * Targetting the View -> Recipes menuitem, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showRecipes() {
        primaryCtrl.showRecipes();
    }

    /**
     * Targetting the View -> Ingredients menu item, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showIngredients() {
        primaryCtrl.showIngredients();
    }

    /**
     * Targetting the View -> ShoppingList menu item, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showShoppingList() { 
        primaryCtrl.showShoppingList(); 
    }

    /** 
     * Returns a string capitalized.
     * @param s The language string
     * @return The properly capitalised string
     */
    private static String capitalize(String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * @return The 
     */
    private ListCell<Locale> createFlagCell() {
        return new ListCell<>() {
            private final ImageView iv = new ImageView();

            {
                iv.setFitWidth(20);
                iv.setFitHeight(14);
                iv.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Locale locale, boolean empty) {
                super.updateItem(locale, empty);

                if (empty || locale == null) {
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                    return;
                }

                String path = client.i18n.Flags.flagPathFor(locale);
                if (path != null) {
                    Image img = new Image(Objects.requireNonNull(
                            getClass().getClassLoader().getResourceAsStream(path),
                            "Missing flag resource: " + path
                    ));
                    iv.setImage(img);
                    setGraphic(iv);
                } else {
                    setGraphic(null); // fallback if missing image
                }

                setText(null); // flags only
                setTooltip(new javafx.scene.control.Tooltip(locale.getDisplayName(locale)));
            }
        };
    }
}