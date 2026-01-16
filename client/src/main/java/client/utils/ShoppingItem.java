package client.utils;

import commons.Ingredient;
import commons.Measurement;
import javafx.beans.property.*;

/**
 * Represents a single item in the shopping list.
 * Uses JavaFX properties to allow the UI to automatically update when values change.
 */
public class ShoppingItem {

    private final BooleanProperty checked;
    private final ObjectProperty<Ingredient> ingredient;
    private final ObjectProperty<Measurement> measurement;
    private final String source;

    /**
     * Creates a new shopping item.
     * @param ingredient the ingredient for this item
     * @param measurement the amount needed
     * @param source the name of the recipe this item came from
     */
    public ShoppingItem(Ingredient ingredient, Measurement measurement, String source) {
        this.checked = new SimpleBooleanProperty(false);
        this.ingredient = new SimpleObjectProperty<>(ingredient);
        this.measurement = new SimpleObjectProperty<>(measurement);
        this.source = source;
    }

    /**
     * Checks if the item is marked as "bought".
     * @return true if the checkbox is checked, false otherwise.
     */
    public boolean getChecked() {
        return checked.get();
    }

    /**
     * Get the whole boolean property for shoppingListService to work
     * @return the checked object
     */
    public BooleanProperty checkedProperty() {
        return checked;
    }

    /**
     * Sets the checked status of the item.
     * @param checked true to mark as bought.
     */
    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    /**
     * Getter for the ingredient within the shopping item
     * @return the ingredient
     */
    public Ingredient getIngredient() {
        return ingredient.get();
    }

    /**
     * Setter for the ingredient within the shopping item
     * @param ingredient the ingredient being set
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient.set(ingredient);
    }

    /**
     * Getter for the measurement
     * @return the measurement object of the shopping item
     */
    public Measurement getMeasurement() {
        return measurement.get();
    }

    /**
     * Getter for the whole object property of the measurement
     * @return the measurement
     */
    public ObjectProperty<Measurement> measurementProperty() {
        return measurement;
    }

    /**
     * Setter for the measurement
     * @param measurement the measurement being set
     */
    public void setMeasurement(Measurement measurement) {
        this.measurement.set(measurement);
    }

    /**
     * gets the source of the item.
     * since source is immutable, there is no setter.
     * @return the source string.
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns a string representation of the item.
     * Useful for debugging or simple text output.
     * @return a string like representing the object
     */
    @Override
    public String toString() {
        if (getIngredient() == null || getMeasurement() == null) {
            return "Invalid shopping item";
        }
        return getMeasurement().toString() + " " + getIngredient().getName();
    }
}
