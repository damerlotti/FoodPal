package client.utils.search;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecipeSearchServiceTest {

    private RecipeSearchService searchService;
    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        searchService = new RecipeSearchService("en", Set.of(1L, 2L));
        testRecipes = createTestRecipes();
    }

    private List<Recipe> createTestRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(createRecipe(1L, "Italian Pasta Carbonara", "en",
                List.of("Spaghetti", "Bacon", "Parmesan Cheese"),
                List.of("Cook pasta", "Fry bacon", "Combine")));
        recipes.add(createRecipe(2L, "Margherita Pizza", "en",
                List.of("Dough", "Tomato Sauce", "Mozzarella Cheese"),
                List.of("Stretch dough", "Add toppings", "Bake")));
        recipes.add(createRecipe(3L, "Greek Salad", "en",
                List.of("Cucumber", "Tomato", "Feta Cheese"),
                List.of("Chop vegetables", "Add dressing")));
        recipes.add(createRecipe(4L, "Sarmale", "ro-RO",
                List.of("Varza", "Carne"),
                List.of("Prepara", "Gateste")));
        return recipes;
    }

    private Recipe createRecipe(long id, String name, String languageTag,
                                List<String> ingredients, List<String> steps) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setLanguageTag(languageTag);

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (String ingredientName : ingredients) {
            RecipeIngredient ri = new RecipeIngredient();
            Ingredient ing = new Ingredient();
            ing.setName(ingredientName);
            ri.setIngredient(ing);
            ri.setRecipe(recipe);
            recipeIngredients.add(ri);
        }
        recipe.setIngredients(recipeIngredients);

        List<RecipeStep> recipeSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            RecipeStep step = new RecipeStep();
            step.setText(steps.get(i));
            step.setOrderIndex(i);
            step.setRecipe(recipe);
            recipeSteps.add(step);
        }
        recipe.setSteps(recipeSteps);

        return recipe;
    }

    @Test
    void searchByTitleAndIngredient() {
        List<Recipe> byTitle = searchService.search(testRecipes, "Pasta");
        assertEquals(1, byTitle.size());
        assertEquals("Italian Pasta Carbonara", byTitle.get(0).getName());

        List<Recipe> byIngr = searchService.search(testRecipes, "ingr: Cheese");
        assertEquals(3, byIngr.size());
    }

    @Test
    void searchFavourites() {
        List<Recipe> favourites = searchService.search(testRecipes, "fav");
        assertEquals(2, favourites.size());
        assertTrue(favourites.stream().allMatch(r -> Set.of(1L, 2L).contains(r.getId())));

        List<Recipe> favPasta = searchService.search(testRecipes, "fav Pasta");
        assertEquals(1, favPasta.size());
    }

    @Test
    void searchWithOperators() {
        List<Recipe> orSearch = searchService.search(testRecipes, "Pizza | Salad");
        assertEquals(2, orSearch.size());

        List<Recipe> andSearch = searchService.search(testRecipes, "title: Pasta ingr: Bacon");
        assertEquals(1, andSearch.size());
    }

    @Test
    void searchCaseSensitivity() {
        List<Recipe> sensitive = searchService.search(testRecipes, "case: s pasta");
        assertTrue(sensitive.isEmpty());

        List<Recipe> insensitive = searchService.search(testRecipes, "case: i PASTA");
        assertEquals(1, insensitive.size());
    }

    @Test
    void searchComplexQuery() {
        List<Recipe> results = searchService.search(testRecipes,
                "fav (title: Pasta | title: Pizza) ingr: Cheese");
        assertEquals(2, results.size());
    }
}
