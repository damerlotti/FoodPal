package client.utils.search;

import commons.Ingredient;
import commons.Measurement;
import commons.Nutrition;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

    /**
     * Helper class to store stuff because checkstyle rules enforce a maximum 
     * of 10 parameters per function :skull:
     */
    private static class TestNutritionData {
        BigDecimal kcalPer100g = BigDecimal.ZERO;
        BigDecimal fatPer100g = BigDecimal.ZERO;
        BigDecimal proteinPer100g = BigDecimal.ZERO;
        BigDecimal carbsPer100g = BigDecimal.ZERO;
        BigDecimal grams = new BigDecimal("100");

        TestNutritionData withKcal(BigDecimal kcal) {
            this.kcalPer100g = kcal;
            return this;
        }

        TestNutritionData withFat(BigDecimal fat) {
            this.fatPer100g = fat;
            return this;
        }

        TestNutritionData withProtein(BigDecimal protein) {
            this.proteinPer100g = protein;
            return this;
        }

        TestNutritionData withCarbs(BigDecimal carbs) {
            this.carbsPer100g = carbs;
            return this;
        }

        TestNutritionData withGrams(BigDecimal grams) {
            this.grams = grams;
            return this;
        }
    }

    private Recipe createRecipeWithNutrition(long id, String name, int servings,
                                             TestNutritionData data) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setServings(servings);
        recipe.setLanguageTag("en");

        // Create nutrition info for 100g
        Nutrition nutrition = new Nutrition(
                data.fatPer100g, data.proteinPer100g, data.carbsPer100g,
                BigDecimal.ZERO, BigDecimal.ZERO);
        nutrition.setKcalPer100g(data.kcalPer100g);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Test Ingredient");
        ingredient.setNutrition(nutrition);

        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(ingredient);
        ri.setAmount(new Measurement(data.grams, Unit.G));
        ri.setRecipe(recipe);

        recipe.setIngredients(List.of(ri));
        recipe.setSteps(List.of());

        return recipe;
    }

    @Test
    void matchesKcalFilterLessThan() {
        // Recipe with 200g of ingredient that has 100 kcal per 100g = 200 total kcal
        TestNutritionData lowCalData = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("200"));
        Recipe lowCalRecipe = createRecipeWithNutrition(1L, "Low Cal", 2, lowCalData);

        // Recipe with 500g of ingredient that has 100 kcal per 100g = 500 total kcal
        TestNutritionData highCalData = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("500"));
        Recipe highCalRecipe = createRecipeWithNutrition(2L, "High Cal", 2, highCalData);

        assertTrue(matcher.matches(lowCalRecipe, parser.parse("kcal<: 300")));
        assertFalse(matcher.matches(highCalRecipe, parser.parse("kcal<: 300")));
    }

    @Test
    void matchesKcalFilterGreaterThan() {
        TestNutritionData lowCalData = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("200"));
        Recipe lowCalRecipe = createRecipeWithNutrition(1L, "Low Cal", 2, lowCalData);

        TestNutritionData highCalData = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("500"));
        Recipe highCalRecipe = createRecipeWithNutrition(2L, "High Cal", 2, highCalData);

        assertFalse(matcher.matches(lowCalRecipe, parser.parse("kcal>: 300")));
        assertTrue(matcher.matches(highCalRecipe, parser.parse("kcal>: 300")));
    }

    @Test
    void matchesKcalFilterEqual() {
        TestNutritionData data = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("200"));
        Recipe recipe = createRecipeWithNutrition(1L, "Exact", 2, data);

        assertTrue(matcher.matches(recipe, parser.parse("kcal: 200")));
        assertFalse(matcher.matches(recipe, parser.parse("kcal: 199")));
        assertFalse(matcher.matches(recipe, parser.parse("kcal: 201")));
    }

    @Test
    void matchesFatFilter() {
        // 200g with 10g fat per 100g = 20g total fat
        TestNutritionData data = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withFat(new BigDecimal("10"))
                .withGrams(new BigDecimal("200"));
        Recipe recipe = createRecipeWithNutrition(1L, "Test", 2, data);

        assertTrue(matcher.matches(recipe, parser.parse("fat<: 25")));
        assertFalse(matcher.matches(recipe, parser.parse("fat<: 15")));
    }

    @Test
    void matchesProteinFilter() {
        // 200g with 25g protein per 100g = 50g total protein
        TestNutritionData data = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withProtein(new BigDecimal("25"))
                .withGrams(new BigDecimal("200"));
        Recipe recipe = createRecipeWithNutrition(1L, "Test", 2, data);

        assertTrue(matcher.matches(recipe, parser.parse("protein>: 40")));
        assertFalse(matcher.matches(recipe, parser.parse("protein>: 60")));
    }

    @Test
    void matchesMultipleNutritionFilters() {
        TestNutritionData data = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withFat(new BigDecimal("10"))
                .withProtein(new BigDecimal("25"))
                .withGrams(new BigDecimal("200"));
        Recipe recipe = createRecipeWithNutrition(1L, "Test", 2, data);

        // Should pass all filters (200kcal, 20g fat, 50g protein)
        assertTrue(matcher.matches(recipe, parser.parse("kcal<: 300 fat<: 30 protein>: 40")));
        // Should fail the fat filter
        assertFalse(matcher.matches(recipe, parser.parse("kcal<: 300 fat<: 15 protein>: 40")));
    }

    @Test
    void matchesNutritionFilterWithServingsScaling() {
        // Recipe with 2 servings: 200g @ 100kcal/100g = 200 total kcal
        // Per serving = 100 kcal
        TestNutritionData data = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("200"));
        Recipe recipe = createRecipeWithNutrition(1L, "Test", 2, data);

        // Without servings scaling: 200 kcal total
        assertTrue(matcher.matches(recipe, parser.parse("kcal<: 250")));
        assertFalse(matcher.matches(recipe, parser.parse("kcal<: 150")));

        // With servings: 1 - should scale to 100 kcal (200 / 2 * 1)
        assertTrue(matcher.matches(recipe, parser.parse("servings: 1 kcal<: 150")));
        assertFalse(matcher.matches(recipe, parser.parse("servings: 1 kcal<: 50")));

        // With servings: 4 - should scale to 400 kcal (200 / 2 * 4)
        assertTrue(matcher.matches(recipe, parser.parse("servings: 4 kcal<: 500")));
        assertFalse(matcher.matches(recipe, parser.parse("servings: 4 kcal<: 300")));
    }

    @Test
    void matchesNutritionFilterWithTextSearch() {
        TestNutritionData data = new TestNutritionData()
                .withKcal(new BigDecimal("100"))
                .withGrams(new BigDecimal("200"));
        Recipe recipe = createRecipeWithNutrition(1L, "Pasta Carbonara", 2, data);

        assertTrue(matcher.matches(recipe, parser.parse("kcal<: 300 Pasta")));
        assertTrue(matcher.matches(recipe, parser.parse("Pasta kcal<: 300")));
        assertFalse(matcher.matches(recipe, parser.parse("kcal<: 300 Pizza")));
        assertFalse(matcher.matches(recipe, parser.parse("kcal<: 100 Pasta")));
    }
}
