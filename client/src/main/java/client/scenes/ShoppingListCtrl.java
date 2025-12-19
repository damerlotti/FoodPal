package client.scenes;

import client.utils.ShoppingItem;
import client.utils.ShoppingListService;
import com.google.inject.Inject;
import commons.Unit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Shopping List screen.
 * Handles user interactions like adding items manually and resetting the list.
 */
public class ShoppingListCtrl implements Initializable {

    private final ShoppingListService shoppingListService;
    private final PrimaryCtrl primaryCtrl;

    @FXML
    private ListView<ShoppingItem> shoppingListView;

    /**
     * Constructs the controller via dependency injection.
     * @param shoppingListService the service holding the shared list data
     * @param primaryCtrl the main controller for navigation
     */
    @Inject
    public ShoppingListCtrl(ShoppingListService shoppingListService, PrimaryCtrl primaryCtrl) {
        this.shoppingListService = shoppingListService;
        this.primaryCtrl = primaryCtrl;
    }

    /**
     * Called automatically when the FXML is loaded.
     * Binds the ListView to the observable list in the service nd sets up the custom cell factory.
     * @param location the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shoppingListView.setItems(shoppingListService.getItems());
        shoppingListView.setCellFactory(new ShoppingListCellFactory());
    }

    /**
     * Opens a dialog to manually add a new item to the shopping list.
     * the "Add Item" button activates it.
     */
    @FXML
    public void addManualItem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Item");
        dialog.setHeaderText("Add to Shopping List");
        dialog.setContentText("Item Name:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                // for now, the program assumes 1 piece for manual items.
                ShoppingItem newItem = new ShoppingItem(name, BigDecimal.ONE, Unit.PIECE, "Manual");
                shoppingListService.getItems().add(newItem);
            }
        });
    }

    /**
     * Clears alllll items from the shopping list.
     * the "Reset List" button activates it.
     */
    @FXML public void resetList(){
        shoppingListService.clear();
    }

    /**
     * Downloads a printable version of the list.
     * I haven't done anything with it yet, but the button exists.
     */
    @FXML
    public void downloadList(){
        //TODO: Will be added later
    }
}
