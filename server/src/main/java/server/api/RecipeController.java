package server.api;

import commons.Recipe;
import server.database.RecipeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeRepository repo;

    /**
     * Constructor which injects the repository
     * @param repo the repository of recipes
     */
    public RecipeController(RecipeRepository repo) {
        this.repo = repo;
    }

    /**
     * Gets all recipes
     * @return a list of all recipes
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
}