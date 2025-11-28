package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeStep;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

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
        primaryCtrl.showOverview();
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
     * Calls the switchToOverview method in primaryCtrl to switch to the overview scene
     */
    @FXML
    public void switchToOverview() {
        clearFields();
        primaryCtrl.showOverview();
    }

}
