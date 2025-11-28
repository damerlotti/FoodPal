package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Recipes")
public class Recipe {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference("recipe-ingredients")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference("recipe-steps")
    private List<RecipeStep> steps = new ArrayList<>();

    /**
     * Parameter-less constructor.
     */
    public Recipe() { }

    /**
     * Creates a new Recipe.
     * @param id The id of the Recipe, used for DB indexing.
     * @param name Display name of the recipe.
     * @param ingredients Ordered list of ingredients used by the recipe.
     * @param steps Ordered list of steps to prepare the recipe.
     */
    public Recipe(long id, String name,
                  List<RecipeIngredient> ingredients, List<RecipeStep> steps) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    /**
     * Returns the id of the recipe.
     * @return The id of the recipe.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the recipe.
     * @param id The id of the recipe.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of the recipe.
     * @return The name of the recipe.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the recipe.
     * @param name The name of the recipe.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of recipe ingredients.
     * @return The list of recipe ingredients.
     */
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    /**
     * Sets the list of recipe ingredients, by reference.
     * @param ingredients The list of recipe ingredients.
     */
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Returns the preparation steps of the recipe.
     * @return The preparation steps of the recipe.
     */
    public List<RecipeStep> getSteps() {
        return steps;
    }

    /**
     * Set the preparation steps of the recipe, by reference.
     * @param steps The preparation steps of the recipe
     */
    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }
}
