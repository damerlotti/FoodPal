package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientTest {
    @Test
    //Used ChatGPT for calculations
    public void estimatedKcalTestWithGrams() {
        Nutrition nutrition = new Nutrition(
                new BigDecimal("10"), //fat
                new BigDecimal("5"),  //protein
                new BigDecimal("20")  //carbs
        );
        // Kcal per 100g = 10*9 + 5*4 + 20*4 = 90 + 20 + 80 = 190
        Ingredient ingredient = new Ingredient();
        ingredient.setNutrition(nutrition);
        ingredient.setName("Test Ingredient");

        Measurement amount = new Measurement(new BigDecimal("0.05"), Unit.KG);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(amount);

        BigDecimal expected = new BigDecimal("95.00000000");
        BigDecimal actual = recipeIngredient.calculateKcal();

        assertEquals(actual, expected);
    }

    @Test
    //Used ChatGPT for calculations
    public void estimatedKcalTestWithLitres() {
        Nutrition nutrition = new Nutrition(
                new BigDecimal("10"), //fat
                new BigDecimal("5"),  //protein
                new BigDecimal("20")  //carbs
        );
        // Kcal per 100g = 10*9 + 5*4 + 20*4 = 90 + 20 + 80 = 190
        Ingredient ingredient = new Ingredient();
        ingredient.setNutrition(nutrition);
        ingredient.setName("Test Ingredient");

        Measurement amount = new Measurement(new BigDecimal("50"), Unit.ML);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(amount);

        BigDecimal expected = new BigDecimal("95.00000000");
        BigDecimal actual = recipeIngredient.calculateKcal();

        assertEquals(actual, expected);
    }
}