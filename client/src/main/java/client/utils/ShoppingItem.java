package client.utils;

import commons.Unit;
import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * Represents a single item in the shopping list.
 * Uses JavaFX properties to allow the UI to automatically update when values change.
 */
public class ShoppingItem {

    private final BooleanProperty checked;
    private final StringProperty name;
    private final ObjectProperty<BigDecimal> amount;
    private final ObjectProperty<Unit> unit;
    private final String source; // Instead of plain String, we use StringProperty
    // because changing the name of e.g. "Apple" to "Green Apple" instantly updates the screen.

    /**
     * Creates a new shopping item.
     * @param name the name of the item
     * @param amount the quantity needed
     * @param unit the unit of measurement
     * @param source the name of the recipe this item came from
     */
    public ShoppingItem(String name, BigDecimal amount, Unit unit, String source) {
        this.checked = new SimpleBooleanProperty(false);
        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleObjectProperty<>(amount);
        this.unit = new SimpleObjectProperty<>(unit);
        this.source = new String(source);
    }

    /**
     * Checks if the ite is marked as "bought".
     * @return true if the checkbox is checked, false otherwise.
     */
    public boolean isChecked() {
        return checked.get();
    }

    /**
     * Sets the checked status of the item.
     * @param checked true to mark as bought.
     */
    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    /**
     * Gets the name of the item.
     * @return the name string.
     */
    public String getName() {
        return name.get();
    }

    /**
     * sets the name of the item.
     * @param name new name
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * gets the amount of the item.
     * @return the amount as a BigDecimal
     */
    public BigDecimal getAmount() {
        return amount.get();
    }

    /**
     * sets the amount of the item.
     * @param amount the new amount.
     */
    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    /**
     * gets the unit of measurement.
     * @return the unit enum.
     */
    public Unit getUnit() {
        return unit.get();
    }

    /**
     * sets the unit of measurement.
     * @param unit the new unit
     */
    public void setUnit(Unit unit) {
        this.unit.set(unit);
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
     * Useful for debugging or simple text output or whatever.
     * I never used it, I just added it out of habit.
     * @return a string like "500 GRAM Flour".
     */
    @Override
    public String toString() {
        return amount.get() + " " + unit.get() + " " + name.get();
    }
}
