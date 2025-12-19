package client.utils;

import commons.RecipeIngredient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.google.inject.Singleton;

import java.util.List;

/**
 * Service class responsible for managing the shopping list data.
 * Annotated as @Singleton to ensure the same list instance is shared across the application.
 * The data is stored in memory and is not persisted to the server.
 */
@Singleton
public class ShoppingListService {

    private final ObservableList<ShoppingItem> items;

    /**
     * Initializes the empty observable list.
     */
    public ShoppingListService() {
        items = FXCollections.observableArrayList();
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

    /**
     * Helper method to convert a RecipeIngredient into a ShoppingItem
     * (but I haven't done that part yet)
     * @param recipeIngredient the ingredient from a recipe.
     * @return a new ShoppingItem to be added to the list.
     */
    public ShoppingItem createItemFromIngredient(RecipeIngredient recipeIngredient) {
        return new ShoppingItem(
                recipeIngredient.getIngredient().getName(),
                recipeIngredient.getAmount().getValue(),
                recipeIngredient.getAmount().getUnit(),
                recipeIngredient.getRecipe().getName() //the source!!!!!
        );
    }
}
