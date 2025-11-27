package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "RecipeSteps")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RecipeStep {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
//    @JsonBackReference("recipe-steps")
    private Recipe recipe;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "order_index")
    private int orderIndex;

    /**
     * Empty constructor.
     */
    public RecipeStep() { }

    /**
     * Creates a new recipe step with the given parameters.
     * @param id The id of the recipe in the database.
     * @param text The text / preparation info for this step.
     */
    public RecipeStep(long id, String text) {
        this.id = id;
        this.text = text;
    }

    /**
     * Returns the database id of the recipe step.
     * @return The database id of the recipe step.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the database id of the recipe step.
     * @param id The database id of the recipe step.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the preparation info for this step.
     * @return The preparation info for this step.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the preparation info for this step.
     * @param text The preparation info for this step.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the recipe this step belongs to.
     * @return The recipe.
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe this step belongs to.
     * @param recipe The recipe this step belongs to.
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


    /**
     * Returns the index of the recipe step in the given step list.
     * @return the index of the recipe step in the given step list.
     */
    public int getOrderIndex() {
        return orderIndex;
    }

    /**
     * Sets the index of the recipe step in the given step list.
     * @param orderIndex The index of the recipe step in the given step list.
     */
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
