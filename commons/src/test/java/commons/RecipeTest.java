package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
}
