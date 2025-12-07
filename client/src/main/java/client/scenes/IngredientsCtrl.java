package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Nutrition;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

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

    @FXML
    private Label nameLabel;

    @FXML
    private TextField fatField;
    @FXML
    private TextField proteinField;
    @FXML
    private TextField carbsField;
    @FXML
    private Label kCalLabel;

    @FXML
    private Label usageLabel;

    @FXML
    private VBox detailsBox;



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
     * Adds an ingredient to the Ingredient scene
     */
    @FXML
    public void addIngredient() {
        Optional<RecipeIngredient> result = IngredientDialog.show(data, server);
        if (result.isPresent()) {
            refresh();
        }
    }

    /**
     * Edits the values of an ingredient (NOT FULLY FUNCTIONAL YET)
     */
    @FXML
    public void editIngredient() {
        // We'll need to have some additions to the IngredientDialog class.
        // Currently, the show method in IngredientDialog is used to only *add* an ingredient.
        Ingredient selected = ingredientsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setContentText("Please select an ingredient to edit.");
            alert.showAndWait();
            return;
        }
    }

    /**
     * Removes a certain ingredient from the database
     */
    public void removeIngredient() {
        Ingredient selected = ingredientsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Select an item to delete!");
            alert.showAndWait();
            return;
        }

        int usage = countUsage(selected);
        Alert alert;

        if (usage > 0) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ingredient in Use");
            alert.setHeaderText("Warning: Used Ingredient");
            alert.setContentText("This ingredient is used in " + usage + " recipes.\n" +
                    "Deleting it will remove it from all those recipes too!\n\n" +
                    "Are you sure?");
        } else {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Ingredient");
            alert.setHeaderText("Delete " + selected.getName() + "?");
            alert.setContentText("Are you sure you want to delete this ingredient?");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            server.deleteIngredient(selected);
            refresh();
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
            if(nameLabel != null) {
                nameLabel.setText(ingredient.getName());
            }
            Nutrition nutrition = ingredient.getNutrition();
            //Ai generated this 4 lines (mostly the format)
            if(nutrition != null) {
                fatField.setText(String.format("%.2f", nutrition.getFatPer100g()));
                proteinField.setText(String.format("%.2f", nutrition.getProteinPer100g()));
                carbsField.setText(String.format("%.2f", nutrition.getCarbsPer100g()));
                kCalLabel.setText(String.format("%.2f", nutrition.getKcalPer100g()));
            } else {
                fatField.setText("0");
                proteinField.setText("0");
                carbsField.setText("0");
                kCalLabel.setText("0");
            }

            if(usageLabel != null) {
                int count = countUsage(ingredient);
                usageLabel.setText("Used in " + count + " recipes");
            }
        }
    }
}
