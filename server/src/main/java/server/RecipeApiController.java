package server;

import commons.Recipe;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeApiController {

    private final EntityManager entityManager;

    /**
     * Constructs a new RecipeController with the specified EntityManager.
     * @param entityManager the EntityManager used for database operations,
     *        injected by Spring Framework
     */
    public RecipeApiController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Retrieves all recipes from the database.
     * This endpoint returns a complete list of all recipes stored in the system,
     * including their ingredients, steps, and other associated data.
     *
     * @return a List of all Recipe entities in the database,
     *         returns an empty list if no recipes exist
     * @see Recipe
     */
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return entityManager.createQuery("SELECT r FROM Recipe r", Recipe.class).getResultList();
    }

    /**
     * Retrieves a specific recipe by its unique identifier.
     * This endpoint returns a single recipe matching the provided ID.
     * If no recipe is found with the given ID, returns null.
     * @param id the unique identifier of the recipe to retrieve,
     *        extracted from the URL path
     * @return the Recipe entity with the specified ID,
     *         or null if no recipe is found
     * @see Recipe
     */
    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable Long id) {
        return entityManager.find(Recipe.class, id);
    }

    /**
     * Retrieves the total count of recipes in the database.
     * This endpoint returns a formatted string containing the number
     * of recipes currently stored in the system.
     * @return a String in the format "Total recipes: X" where X is the
     *         number of recipes in the database
     */
    @GetMapping("/count")
    public String getRecipeCount() {
        Long count = (Long) entityManager.createQuery("SELECT COUNT(r) FROM Recipe r")
                .getSingleResult();
        return "Total recipes: " + count;
    }
}