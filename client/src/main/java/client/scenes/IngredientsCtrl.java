package client.scenes;

import client.MyWebSocketClient;
import client.i18n.I18n;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Nutrition;
import commons.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class IngredientsCtrl implements Initializable {

    private final ServerUtils server;
    private ObservableList<Ingredient> data;
    private final PrimaryCtrl primaryCtrl;

    @FXML
    private ListView<Ingredient> ingredientsList;

    private MyWebSocketClient webSocket = new MyWebSocketClient();

    @FXML
    private TextField fatField;
    @FXML
    private TextField proteinField;
    @FXML
    private TextField carbsField;
    @FXML
    private TextField sugarField;
    @FXML
    private TextField sodiumField;
    @FXML
    private Label fatLabel;
    @FXML
    private Label proteinLabel;
    @FXML
    private Label carbsLabel;
    @FXML
    private Label sugarLabel;
    @FXML
    private Label sodiumLabel;
    @FXML
    private Label kCalLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label usageLabel;
    @FXML
    private Button addButton;

    @FXML
    private Button deleteSelectedButton;
    @FXML
    private Button editSelectedButton;


    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param server the server utils
     * @param primaryCtrl primaryCtrl
     */
    @Inject
    public IngredientsCtrl(ServerUtils server, PrimaryCtrl primaryCtrl) {
        this.server = server;
        this.primaryCtrl = primaryCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind buttons to i18n
        addButton.textProperty().bind(I18n.createStringBinding("ingredients.add"));
        deleteSelectedButton.textProperty().bind(
                I18n.createStringBinding("ingredients.deleteSelected"));
        editSelectedButton.textProperty().bind(
                I18n.createStringBinding("ingredients.editSelected"));
        fatLabel.textProperty().bind(I18n.createStringBinding("ingredients.fat"));
        proteinLabel.textProperty().bind(I18n.createStringBinding("ingredients.protein"));
        carbsLabel.textProperty().bind(I18n.createStringBinding("ingredients.carbs"));
        sodiumLabel.textProperty().bind(I18n.createStringBinding("ingredients.sodium"));
        sugarLabel.textProperty().bind(I18n.createStringBinding("ingredients.sugar"));

        // special javafx list that updates the UI automatically
        data = FXCollections.observableArrayList();
        ingredientsList.setItems(data);
        ingredientsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldIngredient, newIngredient)
                        -> showNutritionOfIngredient(newIngredient)
        );

        clearNutritionDisplay();
        initializeWebSocket();
        refresh();
    }

    private void initializeWebSocket() {
        webSocket.connect("ws://localhost:8080/websocket");
        webSocket.subscribe("/topic/ingredients", Object.class, obj -> {
            System.out.println("Ingredient updated");
            javafx.application.Platform.runLater(() -> {
                refresh();
            });
        });
    }

    /**
     * Loads all the ingredients in the ListView of Ingredients
     */
    public void refresh() {
        var list = server.getIngredients();
        list.sort((i1, i2) ->
                i1.getName().compareToIgnoreCase(i2.getName())
        );
        data.clear();
        data.addAll(list);
    }

    /**
     * Counts how many recipes an ingredient is used in.
     * @param ingredient any ingredient from the Ingredient repo
     * @return the amount of recipes the given ingredient is used in
     */
    private int countUsage(Ingredient ingredient) {
        List<Recipe> allRecipes = server.getRecipes();
        int count = 0;
        for (Recipe recipe : allRecipes) {
            for (var ri : recipe.getIngredients()) {
                if (ri.getIngredient().getId() == ingredient.getId()) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * Removes a certain ingredient from the database
     */
    public void removeIngredient() {
        Ingredient selected = ingredientsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(I18n.get("alert.selectItemToDelete"));
            alert.showAndWait();
            return;
        }

        int usage = countUsage(selected);
        Alert alert;

        ButtonType yesButton = new ButtonType(I18n.get("dialog.deleteIngredient.yesButton"));
        ButtonType noButton = new ButtonType(I18n.get("dialog.deleteIngredient.noButton"));

        if(usage > 0) {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(I18n.get("alert.warning"));
            alert.setHeaderText(I18n.get("dialog.deleteIngredientInUse.header"));
            alert.setContentText(String.format(I18n.get(
                    "dialog.deleteIngredientInUse.content"), usage));
        } else {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(I18n.get("dialog.deleteIngredient.title"));
            alert.setHeaderText(I18n.get("dialog.deleteIngredient.header"));
            alert.setContentText(I18n.get("dialog.deleteIngredient.content"));
        }
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == yesButton) {
            server.deleteIngredient(selected);
            refresh();
        }
    }

    /**
     * Switches to edit ingredient
     */
    @FXML
    public void switchToEditIngredient() {
        Ingredient selected = ingredientsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(I18n.get("alert.selectItemToEdit"));
            alert.showAndWait();
            return;
        } else {
            primaryCtrl.showEditIngredient(selected);
        }
    }

    /**
     * Switches to add ingredient
     */
    @FXML
    public void switchToAddIngredient() {
        primaryCtrl.showAddIngredient();
    }

    /**
     * clears up the text displaying the nutrition of the ingredient
     */
    public void clearNutritionDisplay() {
        fatField.setText("");
        proteinField.setText("");
        carbsField.setText("");
        sugarField.setText("");
        sodiumField.setText("");
        kCalLabel.setText("");
    }

    /**
     * sets the nutrition values of the ingredient to the textFields
     * @param ingredient ingredient which we want to show its nutrition
     */
    public void showNutritionOfIngredient(Ingredient ingredient) {
        clearNutritionDisplay();
        if (ingredient == null) {
            clearNutritionDisplay();
        } else {
            if (nameLabel != null) {
                nameLabel.setText(ingredient.getName());
            }
            Nutrition nutrition = ingredient.getNutrition();

            if (nutrition != null) {
                fatField.setText(formatDecimal(nutrition.getFatPer100g()));
                proteinField.setText(formatDecimal(nutrition.getProteinPer100g()));
                carbsField.setText(formatDecimal(nutrition.getCarbsPer100g()));
                sugarField.setText(formatDecimal(nutrition.getSugarPer100g()));
                sodiumField.setText(formatDecimal(nutrition.getSodiumPer100g()));
                kCalLabel.setText(formatDecimal(nutrition.getKcalPer100g()));
            } else {
                fatField.setText("0");
                proteinField.setText("0");
                carbsField.setText("0");
                sugarField.setText("0");
                sodiumField.setText("0");
                kCalLabel.setText("0");
            }
            if(usageLabel != null) {
                int count = countUsage(ingredient);
                usageLabel.setText("Used in " + count + " recipes");
            }
        }
    }

    /**
     * Helper method to format BigDecimal values nicely.
     * (I've created this after noticing some issues with the sodium values)
     * @param value the value of the nutrient
     * @return a correct value that doesnt get rounded up or down,
     * yet also doesnt contain too many 0's
     */
    private String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
