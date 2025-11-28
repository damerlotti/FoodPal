package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

public class AddRecipeCtrl {

    private final PrimaryCtrl primaryCtrl;

    //list of steps
    @FXML
    private ListView<String> stepsList;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param primaryCtrl primaryCtrl
     */
    @Inject
    public AddRecipeCtrl(PrimaryCtrl primaryCtrl) {
        this.primaryCtrl = primaryCtrl;
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
     * Calls the switchToOverview method in primaryCtrl to switch to the overview scene
     */
    @FXML
    public void switchToOverview() {
        primaryCtrl.showOverview();
    }
}
