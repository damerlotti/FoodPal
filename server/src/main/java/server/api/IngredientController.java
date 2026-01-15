package server.api;

import commons.Ingredient;
import commons.Recipe;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.IngredientRepository;
import org.springframework.web.bind.annotation.*;
import server.database.RecipeRepository;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepo;
    private final RecipeRepository recipeRepo;
    private final SimpMessagingTemplate msg;

    /**
     * Constructor which injects the IngredientRepository
     * @param ingredientRepo the ingredient repository
     * @param recipeRepo the recipe repository
     * @param msg websocket message to update users
     */
    public IngredientController(IngredientRepository ingredientRepo,
                                RecipeRepository recipeRepo,
                                SimpMessagingTemplate msg) {
        this.ingredientRepo = ingredientRepo;
        this.recipeRepo = recipeRepo;
        this.msg = msg;
    }

    /**
     * Gets all ingredients
     * @return a list of all ingredients
     */
    @GetMapping("/")
    public List<Ingredient> getAll() {
        return ingredientRepo.findAll();
    }

    /**
     * Handler for POST requests to /ingredients
     * @param ingredient the JSON body automatically converted to an Ingredient object
     * @return The saved Ingredient
     */
    @PostMapping("/")
    public Ingredient add(@RequestBody Ingredient ingredient) {
        // TODO: maybe we'll need to check for duplicates here
        if (ingredient.getName() == null || ingredient.getName().isEmpty()) {
            return null;
        }
        return ingredientRepo.save(ingredient);
    }

    /**
     * Handler for DELETE requests to /ingredients/id
     * @param id the id of the Ingredient to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        // We first delete the ingredient from all recipes first
        List<Recipe> allRecipes = recipeRepo.findAll();

        for(Recipe recipe : allRecipes) {
            boolean removed = recipe.getIngredients()
                    .removeIf(ri -> ri.getIngredient().getId() == id);
            if(removed) {
                // Save the recipe
                Recipe savedRecipe = recipeRepo.save(recipe);
                // Update the list for all users
                msg.convertAndSend("/topic/recipes", savedRecipe);
                // Update the list for users that are specifically
                // looking at a recipe with the ingredient in it
                msg.convertAndSend("/topic/recipes" + savedRecipe.getId(), savedRecipe);
            }
        }
        // Then we delete the ingredient itself by its id
        ingredientRepo.deleteById(id);

        // WebSocket message for deleting the ingredient
        msg.convertAndSend("/topic/ingredients", id);
    }
}