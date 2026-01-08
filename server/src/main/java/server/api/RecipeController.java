package server.api;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import server.database.RecipeRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Locale;

// this class has been partly made by AI
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeRepository repo;
    private final EntityManager entityManager;
    private final SimpMessagingTemplate msg;

    /**
     * Constructor which injects both the repository and entity manager
     * @param repo the repository of recipes
     * @param entityManager the EntityManager used for database operations
     * @param msg the SimpMessagingTemplate helps to send updates when there are changes
     */
    public RecipeController(RecipeRepository repo, EntityManager entityManager,
                            SimpMessagingTemplate msg) {
        this.repo = repo;
        this.entityManager = entityManager;
        this.msg = msg;
    }

    /**
     * Gets all recipes
     *
     * @return a list of all recipes, returns an empty list if no recipes exist
     */
    @GetMapping("/")
    public List<Recipe> getAll() {
        return repo.findAll();
    }

    /**
     * Finds a recipe by id
     *
     * @param id the id of the recipe
     * @return the recipe (or null if it does not exist)
     */
    @GetMapping("/{id}")
    public Recipe getById(@PathVariable("id") long id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Handler for POST requests to /recipes
     *
     * @param recipe the JSON body automatically converted to a Recipe object
     * @return The saved recipe
     */
    @PostMapping("/")
    public Recipe addRecipe(@RequestBody Recipe recipe) {

        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            return null;
        }

        if (recipe.getLanguageTag() == null || recipe.getLanguageTag().isBlank()) {
            recipe.setLanguageTag(Locale.ENGLISH.toLanguageTag());
        }

        // CascadeType.ALL actually means that all the RecipeSteps in it will be saved automatically
        Recipe saved = repo.save(recipe);

        // this is the main list channel, and those who listen should reload
        msg.convertAndSend("/topic/recipes", saved);
        return saved;  // we return the object with id now
    }

    /**
     * Retrieves the total count of recipes in the database.
     *
     * @return a String in the format "Total recipes: X" where X is the
     * number of recipes in the database
     */
    @GetMapping("/count")
    public String getRecipeCount() {
        Long count = (Long) entityManager.createQuery("SELECT COUNT(r) FROM Recipe r")
                .getSingleResult();
        return "Total recipes: " + count;
    }

    /**
     * Updates an existing recipe in the database.
     *
     * @param id            of recipe to update
     * @param updatedRecipe recipe containing updated data
     * @return updated recipe, or null if id doesn't exist
     */
    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable long id, @RequestBody Recipe updatedRecipe) {
        Recipe existing = repo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setName(updatedRecipe.getName());

        // Update language tag if provided (non-null/non-blank). Leave unchanged otherwise.
        if (updatedRecipe.getLanguageTag() != null && !updatedRecipe.getLanguageTag().isBlank()) {
            existing.setLanguageTag(updatedRecipe.getLanguageTag());
        }

        existing.getIngredients().clear();
        if (updatedRecipe.getIngredients() != null) {
            for (RecipeIngredient ri : updatedRecipe.getIngredients()) {
                ri.setRecipe(existing);
                existing.getIngredients().add(ri);
            }
        }
        existing.getSteps().clear();
        if (updatedRecipe.getSteps() != null) {
            for (RecipeStep step : updatedRecipe.getSteps()) {
                step.setRecipe(existing);
                existing.getSteps().add(step);
            }
        }
        Recipe saved = repo.save(existing);
        msg.convertAndSend("/topic/recipes", saved);  // maybe the title changed
        msg.convertAndSend("/topic/recipes/" + id, saved); // or maybe just step/ingredients changed
        return saved;
    }

    /**
     * Handles the delete recipe request
     * @param id is the id of the recipe to delete
     */
    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable long id) {
        repo.deleteById(id);
        msg.convertAndSend("/topic/recipes", id);
    }

}
