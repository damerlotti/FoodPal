package client.scenes;

import client.i18n.I18n;
import client.utils.IngredientDialog;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddRecipeCtrl implements Initializable {

    private final PrimaryCtrl primaryCtrl;
    private final ServerUtils server;
    private ObservableList<Integer> choices = FXCollections.observableArrayList();
    @FXML
    private TextField titleField;
    @FXML
    private ChoiceBox<Integer> servingsField;
    @FXML
    private ListView<RecipeIngredient> ingredientsList;
    //list of steps
    @FXML
    private ListView<String> stepsList;

    @FXML
    private Label titleLabel;
    @FXML
    private Label ingredientsLabel;
    @FXML
    private Label stepsLabel;
    @FXML
    private Button addRecipeButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addStepButton;
    @FXML
    private Button deleteStepButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button deleteIngredientButton;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param primaryCtrl primaryCtrl
     * @param server server
     */
    @Inject
    public AddRecipeCtrl(PrimaryCtrl primaryCtrl, ServerUtils server) {
        this.primaryCtrl = primaryCtrl;
        this.server = server; // Inject server
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind labels and buttons to i18n
        titleLabel.textProperty().bind(I18n.createStringBinding("addRecipe.title"));
        ingredientsLabel.textProperty().bind(
                I18n.createStringBinding("addRecipe.ingredientsLabel"));
        stepsLabel.textProperty().bind(I18n.createStringBinding("addRecipe.stepsLabel"));
        addRecipeButton.textProperty().bind(I18n.createStringBinding("addRecipe.addRecipe"));
        cancelButton.textProperty().bind(I18n.createStringBinding("addRecipe.cancel"));
        addStepButton.textProperty().bind(I18n.createStringBinding("addRecipe.add"));
        deleteStepButton.textProperty().bind(I18n.createStringBinding("addRecipe.delete"));
        addIngredientButton.textProperty().bind(I18n.createStringBinding("addRecipe.add"));
        deleteIngredientButton.textProperty().bind(I18n.createStringBinding("addRecipe.delete"));
        displayServingChoices();
    }

    /**
     * Populates the choice box with choices
     */
    @FXML
    public void displayServingChoices() {
        for (int i = 1; i<21; i++) {
            choices.add(i);
        }
        servingsField.setValue(choices.get(0));
        servingsField.setItems(choices);
    }
    /**
     * The user adds an ingredient while creating the recipe
     */
    @FXML
    public void onAddIngredient() {
        var allIngredients = server.getIngredients();
        Optional<RecipeIngredient> res = IngredientDialog.show(allIngredients, server);
        res.ifPresent(x -> ingredientsList.getItems().add(x));
    }
    /**
     * When user clicks 'Add Step' textInput pops up where the user writes/confirms step description
     */
    @FXML
    public void onAddStepClicked() {
        // popup dialog for step description
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(I18n.get("dialog.addStep.header"));
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
            alert.setTitle(I18n.get("alert.noSelection"));
            alert.setContentText(I18n.get("alert.selectStepToDeleteAddRecipe"));
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
            showAlert(I18n.get("alert.validationError"), I18n.get("alert.titleEmpty"));
            return;
        }

        Recipe r = new Recipe();
        r.setName(title);
        r.setServings(servingsField.getValue());
        List<RecipeStep> steps = new ArrayList<>();
        int index = 0;
        for (String text: stepsList.getItems()) {
            RecipeStep step = new RecipeStep();
            step.setText(text);
            step.setOrderIndex(index++);
            step.setRecipe(r); // link child back to parent
            steps.add(step);
        }
        r.setSteps(steps);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        index = 0;
        for (RecipeIngredient x: ingredientsList.getItems()) {
            x.setRecipe(r);
            x.setOrderIndex(index++);
            ingredients.add(x);
        }
        r.setIngredients(ingredients);

        // Calculate kcal per 100g here
        BigDecimal estimatedKcal = r.estimatedRecipeKcal();
        System.out.println("Estimated kcal per 100g: " + estimatedKcal);

        try {
            server.addRecipe(r);
        } catch (Exception e) {
            showAlert(I18n.get("alert.serverError"), I18n.get("alert.couldNotSaveRecipe"));
            return;
        }

        clearFields();
        stepsList.getScene().getWindow().hide();
    }

    private void clearFields() {
        titleField.clear();
        stepsList.getItems().clear();
        ingredientsList.getItems().clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * Closes the AddRecipe scene, we don't need to ask PrimaryCtrl to show the recipes
     * because we don't have another scene, and recipes will still be the last thing in MainLayout
     * since we pressed on Add Recipe
     */
    @FXML
    public void cancel() {
        clearFields();
        stepsList.getScene().getWindow().hide();
    }
}
