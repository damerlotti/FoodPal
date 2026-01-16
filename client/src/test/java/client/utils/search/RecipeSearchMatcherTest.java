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

class RecipeSearchMatcherTest {

    private RecipeSearchMatcher matcher;
    private SearchQueryParser parser;

    @BeforeEach
    void setUp() {
        matcher = new RecipeSearchMatcher("en", Set.of(1L, 3L));
        parser = new SearchQueryParser();
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
    void matchesEmptyAndNullQueries() {
        Recipe recipe = createRecipe(1L, "Pasta", "en", List.of(), List.of());

        assertTrue(matcher.matches(recipe, parser.parse("")));
        assertTrue(matcher.matches(recipe, null));
        assertFalse(matcher.matches(null, parser.parse("pasta")));
    }

    @Test
    void matchesTitleSearch() {
        Recipe recipe = createRecipe(1L, "Italian Pasta Salad", "en", List.of(), List.of());

        assertTrue(matcher.matches(recipe, parser.parse("Pasta")));
        assertTrue(matcher.matches(recipe, parser.parse("PASTA")));
        assertFalse(matcher.matches(recipe, parser.parse("Pizza")));
    }

    @Test
    void matchesIngredientSearch() {
        Recipe recipe = createRecipe(1L, "Pasta", "en", List.of("Tomato", "Cheese"), List.of());

        assertTrue(matcher.matches(recipe, parser.parse("ingr: Cheese")));
        assertTrue(matcher.matches(recipe, parser.parse("ingr: TOMATO")));
        assertFalse(matcher.matches(recipe, parser.parse("ingr: Mushroom")));
    }

    @Test
    void matchesStepSearch() {
        Recipe recipe = createRecipe(1L, "Pasta", "en",
                List.of(), List.of("Boil water", "Add pasta"));

        assertTrue(matcher.matches(recipe, parser.parse("step: Boil")));
        assertTrue(matcher.matches(recipe, parser.parse("step: PASTA")));
        assertFalse(matcher.matches(recipe, parser.parse("step: Bake")));
    }

    @Test
    void matchesCaseSensitive() {
        Recipe recipe = createRecipe(1L, "Pasta Carbonara", "en", List.of(), List.of());

        assertTrue(matcher.matches(recipe, parser.parse("case: s Pasta")));
        assertFalse(matcher.matches(recipe, parser.parse("case: s pasta")));

        assertTrue(matcher.matches(recipe, parser.parse("title:= Pasta")));
        assertFalse(matcher.matches(recipe, parser.parse("title:= pasta")));

        // Local overrides global
        assertTrue(matcher.matches(recipe, parser.parse("case: s title:~ pasta")));
    }

    @Test
    void matchesFavouritesFilter() {
        Recipe favourite = createRecipe(1L, "Pasta", "en", List.of(), List.of());
        Recipe notFavourite = createRecipe(2L, "Pizza", "en", List.of(), List.of());

        assertTrue(matcher.matches(favourite, parser.parse("fav")));
        assertFalse(matcher.matches(notFavourite, parser.parse("fav")));
    }

    @Test
    void matchesLanguageFilter() {
        Recipe english = createRecipe(1L, "Pasta", "en", List.of(), List.of());
        Recipe romanian = createRecipe(2L, "Sarmale", "ro-RO", List.of(), List.of());

        assertTrue(matcher.matches(english, parser.parse("lang: en")));
        assertFalse(matcher.matches(romanian, parser.parse("lang: en")));
        assertTrue(matcher.matches(romanian, parser.parse("lang: ro")));
    }

    @Test
    void matchesAndOperator() {
        Recipe recipe = createRecipe(1L, "Pasta Carbonara", "en", List.of("Bacon"), List.of());

        assertTrue(matcher.matches(recipe, parser.parse("Pasta ingr: Bacon")));
        assertFalse(matcher.matches(recipe, parser.parse("Pasta ingr: Mushroom")));
    }

    @Test
    void matchesOrOperator() {
        Recipe pasta = createRecipe(1L, "Pasta", "en", List.of(), List.of());
        Recipe pizza = createRecipe(2L, "Pizza", "en", List.of(), List.of());
        Recipe soup = createRecipe(3L, "Soup", "en", List.of(), List.of());

        assertTrue(matcher.matches(pasta, parser.parse("Pasta | Pizza")));
        assertTrue(matcher.matches(pizza, parser.parse("Pasta | Pizza")));
        assertFalse(matcher.matches(soup, parser.parse("Pasta | Pizza")));
    }

    @Test
    void matchesParenthesesGrouping() {
        Recipe recipe = createRecipe(1L, "Italian Pasta", "en", List.of("Cheese"), List.of());

        assertTrue(matcher.matches(recipe, parser.parse("(Pasta | Pizza) ingr: Cheese")));
        assertFalse(matcher.matches(recipe, parser.parse("(Salad | Soup) ingr: Cheese")));
    }
}
