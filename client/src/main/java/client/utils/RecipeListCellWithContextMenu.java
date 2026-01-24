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
import javafx.beans.binding.StringBinding;
// this class has been made with the help of AI
public class RecipeListCellWithContextMenu extends RecipeListCellFactory.RecipeListCell {

    private final RecipesCtrl recipesCtrl;
    private ContextMenu contextMenu;

    /**
     * rawr
     * @param recipesCtrl rawr
     * @param configManager rawr
     */
    public RecipeListCellWithContextMenu(RecipesCtrl recipesCtrl, ConfigManager configManager) {
        this.recipesCtrl = recipesCtrl;
        // Set the configManager so the parent class can use it
        this.setConfigManager(configManager);
        createContextMenu();
        setupEventHandlers();
    }

    private void createContextMenu() {
        contextMenu = new ContextMenu();

        // Edit recipe menu item
        MenuItem editMenuItem = new MenuItem();
        StringBinding editTextBinding = I18n.createStringBinding("recipes.edit");
        editMenuItem.textProperty().bind(editTextBinding);
        editMenuItem.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                recipesCtrl.onEditTitleClicked();
            }
        });

        // Add all items to the context menu
        contextMenu.getItems().addAll(
                editMenuItem,
                new SeparatorMenuItem()
        );
    }

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