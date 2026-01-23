package client.scenes;

import client.i18n.I18n;
import client.utils.*;
import com.google.inject.Inject;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.IOException;

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
    @FXML private Button addItemButton;
    @FXML private Button resetListButton;
    @FXML private Button downloadButton;
    @FXML private Label titleLabel;

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
        initializeLanguage();
    }

    /**
     * Binding the buttons to their ids
     */
    private void initializeLanguage() {
        titleLabel.textProperty().bind(I18n.createStringBinding("shoppingList.title"));
        addItemButton.textProperty().bind(I18n.createStringBinding("shoppingList.addItem"));
        resetListButton.textProperty().bind(I18n.createStringBinding("shoppingList.reset"));
        downloadButton.textProperty().bind(I18n.createStringBinding("shoppingList.download"));
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
                    I18n.get("shoppingList.manualSource")
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
        if (shoppingListService.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(I18n.get("shoppingList.emptyList"));
            alert.setHeaderText(null);
            alert.setContentText(I18n.get("shoppingList.listIsEmpty"));
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("fileChooser.saveShoppingList"));
        fileChooser.setInitialFileName("shopping_list.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md")
        );
        Window owner = shoppingListView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(owner);
        if (file != null) {
            saveToFile(file);
        }
    }

    private void saveToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            writer.println(I18n.get("shoppingList.fileTitle"));
            writer.println("=============");
            writer.println();
            for (ShoppingItem item : shoppingListService.getItems()) {
                String checkMark = item.getChecked() ? "[x]" : "[ ]";
                String amount = item.getMeasurement().getValue().stripTrailingZeros()
                        .toPlainString();
                String unit = item.getMeasurement().getUnit() != null
                        ? item.getMeasurement().getUnit().toString().toLowerCase() : "";
                String name = item.getIngredient().getName();

                String source = "";
                if (item.getSource() != null) {
                    source = "  (" + item.getSource() + ")";
                }
                writer.printf("%s %s %s %s%s%n", checkMark, amount, unit, name, source);
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(I18n.get("shoppingList.error"));
            alert.setHeaderText(I18n.get("shoppingList.couldNotSaveFile"));
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
