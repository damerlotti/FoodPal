package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

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
    private final ServerUtils server;

    @FXML
    private ListView<ShoppingItem> shoppingListView;

    /**
     * Constructs the controller via dependency injection.
     * @param shoppingListService the service holding the shared list data
     * @param primaryCtrl the main controller for navigation
     * @param server the server utils injected
     */
    @Inject
    public ShoppingListCtrl(ShoppingListService shoppingListService, PrimaryCtrl primaryCtrl,
                            ServerUtils server) {
        this.shoppingListService = shoppingListService;
        this.primaryCtrl = primaryCtrl;
        this.server = server;
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
        var allIngredients = server.getIngredients();
        Optional<RecipeIngredient> result = IngredientDialog.show(allIngredients, server);
        result.ifPresent(recipeIngredient -> {
            ShoppingItem newItem = new ShoppingItem(
                    recipeIngredient.getIngredient(),
                    recipeIngredient.getAmount(),
                    "Manual"
            );
            shoppingListService.addItems(java.util.List.of(newItem));
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
