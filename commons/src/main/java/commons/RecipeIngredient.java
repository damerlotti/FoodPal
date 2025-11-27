package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "RecipeIngredients")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RecipeIngredient {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
//    @JsonBackReference("recipe-ingredients")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
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

    public RecipeIngredient() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Measurement getAmount() {
        return amount;
    }

    public void setAmount(Measurement amount) {
        this.amount = amount;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
