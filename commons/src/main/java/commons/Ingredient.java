package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "Ingredients")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ingredient {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Nutrition nutrition;

    /**
     * Empty constructor.
     */
    public Ingredient() { }

    /**
     * Creates a new ingredient with the given parameters.
     * @param id The id used in the database.
     * @param name The name of the ingredient.
     * @param nutrition The nutritional value of the ingredient.
     */
    public Ingredient(long id, String name, Nutrition nutrition) {
        this.id = id;
        this.name = name;
        this.nutrition = nutrition;
    }

    /**
     * Returns the id of the ingredient.
     * @return The id of the ingredient.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the ingredient.
     * @param id The id of the ingredient.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of the ingredient.
     * @return The name of the ingredient.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the ingredient.
     * @param name The name of the ingredient.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the nutritional information of this ingredient.
     * @return The nutritional information of this ingredient.
     */
    public Nutrition getNutrition() {
        return nutrition;
    }

    /**
     * Sets the nutritional information of this ingredient.
     * @param nutrition The nutritional information of this ingredient.
     */
    public void setNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
    }

    @Override
    public String toString() {
        return name;
    }
}
