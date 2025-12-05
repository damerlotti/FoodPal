package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeStep;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecipesCtrl implements Initializable {

    private final ServerUtils server;
    private ObservableList<Recipe> data;
    private final PrimaryCtrl primaryCtrl;

    // fxml link, keep in mind that it must match with the id in scene builder
    // that's the left-hand side
    @FXML
    private ListView<Recipe> recipeList;

    // and this is the right-hand side
    @FXML
    private Label recipeTitle;
    @FXML
    private ListView<String> ingredientsList;
    @FXML
    private ListView<RecipeStep> stepsList;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     *
     * @param server      the server utils
     * @param primaryCtrl primaryCtrl
     */
    @Inject
    public RecipesCtrl(ServerUtils server, PrimaryCtrl primaryCtrl) {
        this.server = server;
        this.primaryCtrl = primaryCtrl;
    }

    // runs immediately when the screen opens
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // special javafx list that updates the UI automatically
        data = FXCollections.observableArrayList();
        recipeList.setItems(data);
        recipeList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showRecipeDetails(newValue)
        );
        // the ui list contains our data, currently empty
        refresh();
    }

    /**
     * The logic linked to the refresh button
     */
    public void refresh() {
        //System.out.println("Refreshing list...");
        var recipes = server.getRecipes();
        data.clear();
        data.addAll(recipes);
    }

    /**
     * Shows the details of a recipe (ingredients, preparation steps)
     *
     * @param recipe
     */
    private void showRecipeDetails(Recipe recipe) {
        if (recipe == null) {
            recipeTitle.setText("Select a recipe");
            ingredientsList.getItems().clear();
            stepsList.getItems().clear();
            return;
        }
        recipeTitle.setText(recipe.getName());
        ingredientsList.getItems().clear();
        for (var ing : recipe.getIngredients()) {
            ingredientsList.getItems().add(ing.toString());
        }
        stepsList.getItems().clear();
        for (RecipeStep step : recipe.getSteps()) {
            stepsList.getItems().add(step);  // add RecipeStep objects, not strings
        }
    }

    /**
     * When the add button is pressed, the user can add a new step to the recipe.
     */
    @FXML
    public void onAddStepClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Recipe Selected");
            alert.setContentText("Please select a recipe to add a step.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter the step description:");
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String stepText = result.get();
            if (stepText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("Step description cannot be empty.");
                alert.showAndWait();
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server Error");
                alert.setContentText("Could not save the new step to the database.");
                alert.showAndWait();
                return;
            }

            showRecipeDetails(recipe);
        }
    }

    /**
     * When delete button is clicked and the step is deleted
     */
    @FXML
    public void onDeleteStepClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Recipe Selected");
            alert.setContentText("Please select a recipe to edit.");
            alert.showAndWait();
            return;
        }

        RecipeStep selectedStep = stepsList.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Step Selected");
            alert.setContentText("Please select a step to delete.");
            alert.showAndWait();
            return;
        }

        recipe.getSteps().remove(selectedStep);
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            recipe.getSteps().get(i).setOrderIndex(i);
        }

        try {
            server.updateRecipe(recipe);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Error");
            alert.setContentText("Could not delete the step from the database.");
            alert.showAndWait();
            return;
        }
        showRecipeDetails(recipe);
    }

    /**
     * Edit button is clicked, a box appears where uses edits step.
     */
    @FXML
    public void onEditStepClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Recipe Selected");
            alert.setContentText("Please select a recipe to edit.");
            alert.showAndWait();
            return;
        }
        RecipeStep selectedStep = stepsList.getSelectionModel().getSelectedItem();
        if (selectedStep == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Step Selected");
            alert.setContentText("Please select a step to modify.");
            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedStep.getText());
        dialog.setHeaderText("Edit the step description:");
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 250);

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newText = result.get().trim();
            if (newText.isEmpty()) {
                return;
            }

            selectedStep.setText(newText);

            try {
                server.updateRecipe(recipe);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server Error");
                alert.setContentText("Could not update the step in the database.");
                alert.showAndWait();
                return;
            }
            showRecipeDetails(recipe);
        }
    }

    /**
     * When the edit button is pressed the user can edit the title of the existing recipe
     */
    @FXML
    public void onEditTitleClicked() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();
        if (recipe == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Recipe Selected");
            alert.setContentText("Please select a recipe to edit its title.");
            alert.showAndWait();
        }
        TextInputDialog dialog = new TextInputDialog(recipe.getName());
        dialog.setHeaderText("Edit the recipe title:");
        dialog.setGraphic(null);
        dialog.getDialogPane().setPrefSize(400, 150);

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newTitle = result.get().trim();
            if (newTitle.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Title");
                alert.setContentText("Title cannot be empty.");
                alert.showAndWait();
                return;
            }
            recipe.setName(newTitle);
            try {
                server.updateRecipe(recipe);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Server Error");
                alert.setContentText("Could not update the recipe title in the database.");
                alert.showAndWait();
                return;
            }
            showRecipeDetails(recipe);
            refresh();
            recipeList.getSelectionModel().select(recipe);
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Recipe Selected");
            alert.setContentText("Please select a recipe to clone.");
            alert.showAndWait();
            return;
        }

        // TODO(calco): When adding a recipe, do we clone the ingredients and steps as well?
        // "[...] so I can give it my own twist without changing the original [...]"

        Recipe clonedRecipe = recipe.cloneRecipe();
        
//        try {
//            server.addRecipe(r);
//        } catch (Exception e) {
//            showAlert("Server Error", "Could not save recipe.");
//            return;
//        }
    }

    /**
     * Opens a new dialogue to save a file with all recipe information in a user specified ocation and format.
     */
    @FXML
    public void onPrintPressed() {
        Recipe recipe = recipeList.getSelectionModel().getSelectedItem();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Recipe to File");
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
                }
                else if (file.getName().endsWith(".txt")) {
                    out.print(recipe.toExportablePlaintext());
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Failed");
                alert.setHeaderText("Could not save the recipe to the file.\nUnable to write to the file.");
                // TODO(calco): Should probably show a more human readable message
                alert.setContentText("Error: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}