package client.utils;

import client.scenes.RecipesCtrl;
import commons.Recipe;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import client.config.ConfigManager;
import client.i18n.I18n;

public class RecipeListCellWithContextMenu extends
        RecipeListCellFactory.RecipeListCell {

    private final RecipesCtrl recipesCtrl;
    private ContextMenu contextMenu;

    /**
     * e
     * @param recipesCtrl e
     * @param configManager e
     */
    public RecipeListCellWithContextMenu(
            RecipesCtrl recipesCtrl, ConfigManager configManager) {
        this.recipesCtrl = recipesCtrl;
        this.setConfigManager(configManager);
        createContextMenu();
        setupEventHandlers();
    }

    /**
     * creates the context menu
     */
    private void createContextMenu() {
        contextMenu = new ContextMenu();

        // Edit recipe menu item
        MenuItem editMenuItem = new MenuItem();
        editMenuItem.textProperty().
                bind(I18n.createStringBinding("recipes.edit"));
        editMenuItem.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                recipesCtrl.onEditTitleClicked();
            }
        });

        // Clone recipe menu item
        MenuItem cloneMenuItem = new MenuItem();
        cloneMenuItem.textProperty().
                bind(I18n.createStringBinding("recipes.clone"));
        cloneMenuItem.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                recipesCtrl.onClonePressed();
            }
        });

        // Print recipe menu item
        MenuItem printMenuItem = new MenuItem();
        printMenuItem.textProperty().
                bind(I18n.createStringBinding("recipes.print"));
        printMenuItem.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                recipesCtrl.onPrintPressed();
            }
        });

        // Add to shopping list menu item
        MenuItem shoppingListMenuItem = new MenuItem();
        shoppingListMenuItem.textProperty().
                bind(I18n.createStringBinding("recipes.shop"));
        shoppingListMenuItem.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                recipesCtrl.addToShoppingList();
            }
        });

        // Delete recipe menu item
        MenuItem deleteMenuItem = new MenuItem();
        deleteMenuItem.textProperty().
                bind(I18n.createStringBinding("recipes.delete"));
        deleteMenuItem.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                recipesCtrl.onDeleteRecipePressed();
            }
        });

        // Add all items to the context menu
        contextMenu.getItems().addAll(
                editMenuItem,
                cloneMenuItem,
                printMenuItem,
                shoppingListMenuItem,
                new SeparatorMenuItem(),
                deleteMenuItem
        );
    }

    /**
     * sets up the even handlers
     */
    private void setupEventHandlers() {
        // Show context menu on right click
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY && !isEmpty()) {
                // Ensure this recipe is selected in the list view
                getListView().getSelectionModel().select(getItem());
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
            } else {
                // Hide context menu on left click elsewhere
                contextMenu.hide();
            }
        });
    }

    @Override
    protected void updateItem(Recipe recipe, boolean empty) {
        super.updateItem(recipe, empty);

        // Enable/disable context menu based on whether there's an item
        if (empty || recipe == null) {
            setContextMenu(null);
        } else {
            setContextMenu(contextMenu);
        }
    }
}
