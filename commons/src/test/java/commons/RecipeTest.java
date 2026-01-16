package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {
    @Test
    void cloneRecipe() {
        // Build original recipe
        Recipe original = new Recipe();
        original.setId(123L);
        original.setName("Pancakes");
        original.setLanguageTag("ro-RO");

        Ingredient flour = new Ingredient(1L, "Flour", null);
        Measurement amount = new Measurement(new BigDecimal("2.5"), Unit.CUP);

        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(flour);
        ri.setAmount(amount);
        ri.setOrderIndex(0);
        ri.setRecipe(original);

        RecipeStep step = new RecipeStep();
        step.setText("Mix ingredients");
        step.setOrderIndex(0);
        step.setRecipe(original);

        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(ri);
        List<RecipeStep> steps = new ArrayList<>();
        steps.add(step);

        original.setIngredients(ingredients);
        original.setSteps(steps);

        // Clone
        Recipe cloned = original.cloneRecipe();

        // Basic checks
        assertNotSame(original, cloned,
                "Cloned recipe must be a different instance");
        assertEquals(original.getName(), cloned.getName(),
                "Name should be copied");
        assertEquals(
                original.getLanguageTag(),
                cloned.getLanguageTag(),
                "Language tag should be copied");

        // Id should not be copied (cloneRecipe does not set id)
        assertEquals(0L, cloned.getId(),
                "Cloned recipe id should be default (0)");

        // Ingredients list should be a different list instance with cloned items
        assertNotSame(original.getIngredients(), cloned.getIngredients(),
                "Ingredients list should be a different instance");
        assertEquals(original.getIngredients().size(), cloned.getIngredients().size(),
                "Ingredients size should match");

        RecipeIngredient originalRI = original.getIngredients().get(0);
        RecipeIngredient clonedRI = cloned.getIngredients().get(0);

        assertNotSame(originalRI, clonedRI,
                "Each RecipeIngredient should be a different instance");
        // Ingredient (the referenced Ingredient) is intentionally the same instance
        assertSame(originalRI.getIngredient(), clonedRI.getIngredient(),
                "Referenced Ingredient should be the same instance");
        // Measurement is copied by reference in cloneRecipe
        assertSame(originalRI.getAmount(), clonedRI.getAmount(),
                "Measurement instance is reused by the clone");
        // The recipe reference on the cloned ingredient must point to the cloned recipe
        assertSame(cloned, clonedRI.getRecipe(),
                "Cloned RecipeIngredient must reference the cloned recipe");

        // Steps checks
        assertNotSame(original.getSteps(), cloned.getSteps(),
                "Steps list should be a different instance");
        assertEquals(original.getSteps().size(), cloned.getSteps().size(),
                "Steps size should match");

        RecipeStep originalStep = original.getSteps().get(0);
        RecipeStep clonedStep = cloned.getSteps().get(0);

        assertNotSame(originalStep, clonedStep,
                "Each RecipeStep should be a different instance");
        assertEquals(originalStep.getText(), clonedStep.getText(),
                "Step text should be copied");
        assertEquals(originalStep.getOrderIndex(), clonedStep.getOrderIndex(),
                "Step orderIndex should be copied");
        assertSame(cloned, clonedStep.getRecipe(),
                "Cloned RecipeStep must reference the cloned recipe");

        // Mutating the clone should not affect the original
        cloned.setName("Changed");
        clonedStep.setText("Changed step");
        clonedRI.setOrderIndex(5);

        assertNotEquals(original.getName(), cloned.getName(),
                "Changing cloned name must not change original");
        assertNotEquals(originalStep.getText(), clonedStep.getText(),
                "Changing cloned step text must not change original step text");
        assertNotEquals(originalRI.getOrderIndex(), clonedRI.getOrderIndex(),
                "Changing cloned ingredient orderIndex must not change original");
    }

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
    void setLanguageTag_setsValidTag() {
        Recipe recipe = new Recipe();
        recipe.setLanguageTag("ro-RO");
        assertEquals("ro-RO", recipe.getLanguageTag());
    }

    @Test
    void setLanguageTag_nullFallsBackToEnglish() {
        Recipe recipe = new Recipe();
        recipe.setLanguageTag(null);
        assertEquals(Locale.ENGLISH.toLanguageTag(), recipe.getLanguageTag());
    }

    @Test
    void setLanguageTag_blankFallsBackToEnglish() {
        Recipe recipe = new Recipe();
        recipe.setLanguageTag("   ");
        assertEquals(Locale.ENGLISH.toLanguageTag(), recipe.getLanguageTag());
    }
}
