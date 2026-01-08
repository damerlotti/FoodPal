package client.scenes;

import client.i18n.I18n;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Nutrition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ResourceBundle;

public class IngredientsCtrl implements Initializable {

    private final ServerUtils server;
    private ObservableList<Ingredient> data;
    private final PrimaryCtrl primaryCtrl;

    @FXML
    private ListView<Ingredient> ingredientsList;

    @FXML
    private TextArea fatDisplay;
    @FXML
    private TextArea proteinDisplay;
    @FXML
    private TextArea carbsDisplay;
    @FXML
    private TextArea kCalDisplay;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteSelectedButton;


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

        // special javafx list that updates the UI automatically
        data = FXCollections.observableArrayList();
        ingredientsList.setItems(data);
        ingredientsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldIngredient, newIngredient)
                        -> showNutritionOfIngredient(newIngredient)
        );

        clearNutritionDisplay();
        refresh();
    }

    /**
     * Loads all the ingredients in the ListView of Ingredients
     */
    public void refresh() {
        var list = server.getIngredients();
        data.clear();
        data.addAll(list);
        clearNutritionDisplay();
    }

    /**
     * Removes a certain ingredient from the database
     */
    public void removeIngredient() {
        var selected = ingredientsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(I18n.get("alert.selectItemToDelete"));
            alert.showAndWait();
            return;
        }
        server.deleteIngredient(selected);
        refresh();
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
        fatDisplay.setText("");
        proteinDisplay.setText("");
        carbsDisplay.setText("");
        kCalDisplay.setText("");
    }

    /**
     * sets the nutrition values of the ingredient to the textAreas
     * @param ingredient ingredient which we want to show its nutrition
     */
    public void showNutritionOfIngredient(Ingredient ingredient) {
        clearNutritionDisplay();
        if (ingredient == null) {
            clearNutritionDisplay();
        } else {
            Nutrition nutrition = ingredient.getNutrition();
            if (nutrition == null) {
                return;
            }
            //Ai generated this 4 lines (mostly the format)
            fatDisplay.setText(String.format("%.2f", nutrition.getFatPer100g()));
            proteinDisplay.setText(String.format("%.2f", nutrition.getProteinPer100g()));
            carbsDisplay.setText(String.format("%.2f", nutrition.getCarbsPer100g()));
            kCalDisplay.setText(String.format("%.2f", nutrition.getKcalPer100g()));
        }
    }
}
