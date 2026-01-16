package client.utils;

import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.google.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;

/**
 * Service class responsible for managing the shopping list data.
 * Annotated as @Singleton to ensure the same list instance is shared across the application.
 * The data is stored in memory and is not persisted to the server.
 */
@Singleton
public class ShoppingListService {

    private final ObservableList<ShoppingItem> items;
    private final ConfigManager configManager;
    /**
     * Initializes the empty observable list.
     * @param configManager inject the config boss
     */
    @Inject
    public ShoppingListService(ConfigManager configManager) {
        this.configManager = configManager;
        items = FXCollections.observableArrayList();
        loadFromConfig();
        items.addListener((ListChangeListener<ShoppingItem>) c -> saveToConfig());
    }

    /**
     * Reads the basic shopping objects and turns them into smart javafx ones
     */
    private void loadFromConfig() {
        Config config = configManager.getConfig();
        if (config.getShoppingList() != null) {
            List<ShoppingItem> loadedItems = config.getShoppingList().stream().map(basic -> {
                ShoppingItem item = new ShoppingItem(basic.ingredient,
                        basic.measurement, basic.source);
                item.setChecked(basic.checked);
                item.checkedProperty().addListener((obs, old, newVal) -> saveToConfig());
                item.measurementProperty().addListener((obs, oldVal, newVal) -> saveToConfig());
                return item;
            }).collect(Collectors.toList());

            items.setAll(loadedItems);
        }
    }

    /**
     * Convert the smart objects into basic objects
     * and write them in the JSON
     */
    private void saveToConfig() {
        Config config = configManager.getConfig();

        List<Config.ShoppingItemBasic> basics = items.stream()
                .map(item -> new Config.ShoppingItemBasic(
                        item.getChecked(),
                        item.getIngredient(),
                        item.getMeasurement(),
                        item.getSource()))
                .collect(Collectors.toList());
        config.setShoppingList(basics);
        configManager.save();
    }

    /**
     * Returns the live list of shopping items.
     * @return the observable list containing allll items.
     */
    public ObservableList<ShoppingItem> getItems() {
        return items;
    }

    /**
     * adds a collection of new items to the shopping list.
     * @param newItems the list of shoppingitems to be added.
     */
    public void addItems(List<ShoppingItem> newItems) {
        for (ShoppingItem item : newItems) {
            item.checkedProperty().addListener((obs, old, newVal) -> saveToConfig());
            item.measurementProperty().addListener((obs, oldVal, newVal) -> saveToConfig());
        }
        this.items.addAll(newItems);
    }

    /**
     * removes a single item from the shopping list.
     * @param item the item to be removed
     */
    public void removeItem(ShoppingItem item) {
        this.items.remove(item);
    }

    /**
     * clears all items from the shopping list.
     */
    public void clear() {
        this.items.clear();
    }

}
