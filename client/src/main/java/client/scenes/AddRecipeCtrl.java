package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeCtrl {

    private final PrimaryCtrl primaryCtrl;
    private final ServerUtils server;
    @FXML
    private TextField titleField;

    //list of steps
    @FXML
    private ListView<String> stepsList;

    @FXML
    private Button addIngredient;

    @FXML
    private ListView<String> ingredientList;

    private ArrayList<Ingredient> ingredients;         // actual objects of the ingredients

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param primaryCtrl primaryCtrl
     * @param server server
     */
    @Inject
    public AddRecipeCtrl(PrimaryCtrl primaryCtrl, ServerUtils server) {
        this.primaryCtrl = primaryCtrl;
        this.server = server; // Inject server
        this.ingredients = new ArrayList<>();
    }

    /**
     * When user clicks 'Add Step' textInput pops up where the user writes/confirms step description
     */
    @FXML
    public void onAddStepClicked() {
        // popup dialog for step description
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter the step description:");
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);

        // The description
        var result = dialog.showAndWait();

        // If user pressed ok and input isn't null
        result.ifPresent(step -> stepsList.getItems().add(step));
    }

    /**
     * The user selects a step and presses on delete button to delete the step
     */
    @FXML
    private void onDeleteStepClicked() {
        var selected = stepsList.getSelectionModel().getSelectedItem();
        //if no step is being selected
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setContentText("Please select a step to delete.");
            alert.showAndWait();
            return;
        }
        // is step is selected then it is deleted from list
        stepsList.getItems().remove(selected);
    }

    /**
     * The action for the 'Add Recipe' button
     */
    @FXML
    public void createRecipe() {
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) {
            showAlert("Validation Error", "Title cannot be empty.");
            return;
        }

        Recipe r = new Recipe();
        r.setName(title);

        List<RecipeStep> dbSteps = new ArrayList<>();
        int index = 0;

        for (String text : stepsList.getItems()) {
            RecipeStep step = new RecipeStep();
            step.setText(text);
            step.setOrderIndex(index++);
            step.setRecipe(r); // link child back to parent
            dbSteps.add(step);
        }

        r.setSteps(dbSteps);

        try {
            server.addRecipe(r);
        } catch (Exception e) {
            showAlert("Server Error", "Could not save recipe.");
            return;
        }

        clearFields();
        stepsList.getScene().getWindow().hide();
    }

    private void clearFields() {
        titleField.clear();
        stepsList.getItems().clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * Closes the AddRecipe scene, we don't need to ask PrimaryCtrl to show the overview
     * because we don't have another scene, and overview will still be the last thing in MainLayout
     * since we pressed on Add Recipe
     */
    @FXML
    public void cancel() {
        clearFields();
        stepsList.getScene().getWindow().hide();
    }

    /**
     * When user clicks 'Add' next to the 'Add the recipe ingredients',
     * textInput pops up where the user writes/confirms the ingredient
     */
    @FXML
    public void onAddIngredientClicked() {
        // popup dialog for ingredient description
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter the ingredient name:");
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);
        // The description
        var ingredientDiaName = dialog.showAndWait();

        Unit unitAll = Unit.G;
        Measurement fat = new Measurement(new BigDecimal(-1), unitAll);
        Measurement protein = new Measurement(new BigDecimal(-1), unitAll);
        Measurement carb = new Measurement(new BigDecimal(-1), unitAll);
        BigDecimal kcal = new BigDecimal(-1);
        dialog.getEditor().clear();
        String ingredientName = "Standard";
        if (ingredientDiaName.isPresent()) {
            ingredientName = ingredientDiaName.get();
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Enter the ingredient fat per 100 g:");
        var ingredientFat = dialog.showAndWait();
        if (ingredientFat.isPresent()) {
            fat = new Measurement(stringToBigDecimal(ingredientFat.get()), unitAll);
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Enter the ingredient protein per 100 g:");
        var ingredientProtein = dialog.showAndWait();
        if (ingredientProtein.isPresent()) {
            protein = new Measurement(stringToBigDecimal(ingredientProtein.get()), unitAll);
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Enter the ingredient carbs per 100 g:");
        var ingredientCarb = dialog.showAndWait();
        if (ingredientCarb.isPresent()) {
            carb = new Measurement(stringToBigDecimal(ingredientCarb.get()), unitAll);
        }

        dialog.getEditor().clear();
        dialog.setHeaderText("Enter the ingredient kcal per 100 g:");
        var ingredientKCal = dialog.showAndWait();
        if (ingredientKCal.isPresent()) {
            kcal = stringToBigDecimal(ingredientKCal.get());
        }
        Nutrition nutrition = new Nutrition(fat, protein, carb, kcal);
        Ingredient ingredient = new Ingredient(10000, ingredientName, nutrition);
        // If user pressed ok and input isn't null
        ingredients.add(ingredient);
        ingredientList.getItems().add(ingredient.toString());
    }

    /**
     * parses a string into a big decimal
     * in case of a not valid big decimal, return -1.0
     * @param string string containing a big decimal
     * @return the big decimal in the string
     */
    public static BigDecimal stringToBigDecimal(String string){
        try {
            return new BigDecimal(string);
        } catch (Exception e) {
            return new BigDecimal(-1);
        }
    }
}
