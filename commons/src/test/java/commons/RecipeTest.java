package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {
    @Test
    void testEstimatedRecipeKcal() {
        // Ingredient 1
        Nutrition n1 = new Nutrition(
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("20")
        );
        Ingredient i1 = new Ingredient();
        i1.setNutrition(n1);
        RecipeIngredient ri1 = new RecipeIngredient();
        ri1.setIngredient(i1);
        ri1.setAmount(new Measurement(new BigDecimal("50"), Unit.G));

        // Ingredient 2
        Nutrition n2 = new Nutrition(
                new BigDecimal("5"),
                new BigDecimal("5"),
                new BigDecimal("15")
        );
        Ingredient i2 = new Ingredient();
        i2.setNutrition(n2);
        RecipeIngredient ri2 = new RecipeIngredient();
        ri2.setIngredient(i2);
        ri2.setAmount(new Measurement(new BigDecimal("100"), Unit.ML));

        Recipe recipe = new Recipe();
        recipe.setIngredients(List.of(ri1, ri2));

        // Total kcal = 100 + 100 = 200
        assertEquals(new BigDecimal("146"), recipe.estimatedRecipeKcal());
    }

    @Test
    void testToExportableMarkdown() {
        Recipe recipe = new Recipe();
        recipe.setName("Test Recipe");

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");
        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(ingredient);
        ri.setAmount(new Measurement(new BigDecimal("1"), Unit.PIECE));
        recipe.setIngredients(new ArrayList<>(List.of(ri)));

        RecipeStep step = new RecipeStep();
        step.setText("Add salt");
        step.setOrderIndex(0);
        recipe.setSteps(new ArrayList<>(List.of(step)));

        String markdown = recipe.toExportableMarkdown();

        assertTrue(markdown.contains("# Test Recipe"));
        assertTrue(markdown.contains("## Ingredients"));
        assertTrue(markdown.contains("## Steps"));
        assertTrue(markdown.contains("1. Add salt"));
    }

    @Test
    void testToExportablePlaintext() {
        Recipe recipe = new Recipe();
        recipe.setName("Test Recipe");

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");
        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(ingredient);
        ri.setAmount(new Measurement(new BigDecimal("1"), Unit.PIECE));
        recipe.setIngredients(new ArrayList<>(List.of(ri)));

        RecipeStep step = new RecipeStep();
        step.setText("Add salt");
        step.setOrderIndex(0);
        recipe.setSteps(new ArrayList<>(List.of(step)));

        String plaintext = recipe.toExportablePlaintext();

        assertTrue(plaintext.contains("Test Recipe"));
        assertTrue(plaintext.contains("Ingredients"));
        assertTrue(plaintext.contains("Steps"));
        assertFalse(plaintext.contains("#"));
    }

    @Test
    void testCloneRecipeCreatesIndependentCopy() {
        Recipe original = new Recipe();
        original.setName("Original");

        RecipeStep step = new RecipeStep();
        step.setText("Step 1");
        step.setOrderIndex(0);
        step.setRecipe(original);
        original.setSteps(new ArrayList<>(List.of(step)));

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Ingredient 1");
        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(ingredient);
        ri.setAmount(new Measurement(new BigDecimal("100"), Unit.G));
        ri.setRecipe(original);
        original.setIngredients(new ArrayList<>(List.of(ri)));

        Recipe cloned = original.cloneRecipe();

        assertEquals(original.getName(), cloned.getName());
        assertEquals(original.getSteps().size(), cloned.getSteps().size());
        assertEquals(original.getIngredients().size(), cloned.getIngredients().size());

        cloned.setName("Cloned");
        assertNotEquals(original.getName(), cloned.getName());

        assertNotSame(original.getSteps().get(0), cloned.getSteps().get(0));
    }
}
