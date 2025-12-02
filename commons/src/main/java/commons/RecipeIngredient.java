package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "RecipeIngredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference("recipe-ingredients")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(
            name = "amount_value",
            precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
            ),
        @AttributeOverride(name = "unit", column = @Column(
            name = "amount_unit",
            length = Measurement.MEASUREMENT_UNIT_LENGTH)
            ),
    })
    private Measurement amount;

    @Column(name = "order_index")
    private int orderIndex;

    /**
     * Empty constructor.
     */
    public RecipeIngredient() { }

    /**
     * Returns the id.
     * @return The id.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id The id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the recipe this item belongs to.
     * @return The recipe.
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe this item belongs to.
     * @param recipe The recipe this item belongs to.
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Returns the ingredient this item reference.
     * @return The ingredient this item references.
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * Sets the ingredient this item references.
     * @param ingredient The ingredient this item references.
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * Returns the amount of the ingredient required by the recipe.
     * @return The amount of the ingredient required by the recipe.
     */
    public Measurement getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the ingredient required by the recipe.
     * @param amount The amount of the ingredient required by the recipe.
     */
    public void setAmount(Measurement amount) {
        this.amount = amount;
    }

    /**
     * Returns the index of the recipe ingredient in the given ingredient list.
     * @return the index of the recipe ingredient in the given ingredient list.
     */
    public int getOrderIndex() {
        return orderIndex;
    }

    /**
     * Sets the index of the recipe ingredient in the given ingredient list.
     * @param orderIndex The index of the recipe ingredient in the given ingredient list.
     */
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     *
     * @return the recipe ingredient in a human-readable format: value + unit + name
     */
    @Override
    public String toString() {
        // If the amount of ingredient are empty, return something accordingly
        if(amount == null || ingredient == null) {
            return "Unknown Ingredient";
        }

        java.math.BigDecimal value = amount.getValue();
        Unit unit = amount.getUnit();
        String name = ingredient.getName();

        // Getting rid of the unnecessary 0's: 2.5000 will be 2.5
        String valueString = value.stripTrailingZeros().toPlainString();

        // "2 eggs" instead of "2 pieces of eggs"
        if(unit == Unit.PIECE) {
            return valueString + " " + name;
        }

        // "salt (to taste)" instead of just "salt"
        // This is a temporary solution for the informal units
        if(unit == Unit.INFORMAL) {
            return ingredient.getName() + " (to taste)";
        }

        String unitName = unit.getHumanReadableName();

        // if the value is plural, add an "s" after it. "3 cup" will become "3 cups"
        // NOTE: this logic is me purely relying on the beauty of the English language.
        // "glass" would become "glasss" instead of "glasses" which is obviously wrong.
        if(value.compareTo(java.math.BigDecimal.ONE) > 0) {
            unitName += "s";
        }

        // value + unit + name = 2.5 cups of All-purpose Flour (more human-friendly compared to 2.5 CUP All-purpose Flour)
        return valueString + " " + unitName + " of " + name;
    }
}
