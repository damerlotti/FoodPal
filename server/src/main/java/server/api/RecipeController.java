package server.api;

import commons.Recipe;
import server.database.RecipeRepository;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import java.util.List;
// this class has been partly made by AI
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeRepository repo;
    private final EntityManager entityManager;

    /**
     * Constructor which injects both the repository and entity manager
     * @param repo the repository of recipes
     * @param entityManager the EntityManager used for database operations
     */
    public RecipeController(RecipeRepository repo, EntityManager entityManager) {
        this.repo = repo;
        this.entityManager = entityManager;
    }

    /**
     * Gets all recipes
     * @return a list of all recipes, returns an empty list if no recipes exist
     */
    @GetMapping("/")
    public List<Recipe> getAll() {
        return repo.findAll();
    }

    /**
     * Finds a recipe by id
     * @param id the id of the recipe
     * @return the recipe (or null if it does not exist)
     */
    @GetMapping("/{id}")
    public Recipe getById(@PathVariable("id") long id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Handler for POST requests to /recipes
     * @param recipe the JSON body automatically converted to a Recipe object
     * @return The saved recipe
     */
    @PostMapping("/")
    public Recipe addRecipe(@RequestBody Recipe recipe) {

        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            return null;
        }
        // CascadeType.ALL actually means that all the RecipeSteps in it will be saved automatically
        Recipe saved = repo.save(recipe);
        return saved;  // we return the object with id now
    }

    /**
     * Retrieves the total count of recipes in the database.
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