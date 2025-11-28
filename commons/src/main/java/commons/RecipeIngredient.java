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
}
