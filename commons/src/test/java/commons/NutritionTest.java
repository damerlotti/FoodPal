package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NutritionTest {

    @Test
    void testComputeKcalPer100gWithAllValues() {
        Nutrition nutrition = new Nutrition(
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("20"),
                new BigDecimal("6"),
                new BigDecimal("7")
        );
        assertEquals(new BigDecimal("190"), nutrition.getKcalPer100g());
    }

    @Test
    void testComputeKcalPer100gWithNullValues() {
        Nutrition nutrition = new Nutrition(null, null, null, null, null);
        assertEquals(BigDecimal.ZERO, nutrition.getKcalPer100g());
    }

    @Test
    void testComputeKcalPer100gWithPartialValues() {
        Nutrition nutrition = new Nutrition(new BigDecimal("15"), null, null, null, null);
        assertEquals(new BigDecimal("135"), nutrition.getKcalPer100g());
    }

    @Test
    void testSetFatRecalculatesKcal() {
        Nutrition nutrition = new Nutrition(
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("20"),
                new BigDecimal("6"),
                new BigDecimal("7")
        );
        nutrition.setFatPer100g(new BigDecimal("20"));
        assertEquals(new BigDecimal("280"), nutrition.getKcalPer100g());
    }

    @Test
    void testSetProteinRecalculatesKcal() {
        Nutrition nutrition = new Nutrition(
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("20"),
                new BigDecimal("6"),
                new BigDecimal("7")
        );
        nutrition.setProteinPer100g(new BigDecimal("10"));
        assertEquals(new BigDecimal("210"), nutrition.getKcalPer100g());
    }

    @Test
    void testSetCarbsRecalculatesKcal() {
        Nutrition nutrition = new Nutrition(
                new BigDecimal("10"),
                new BigDecimal("5"),
                new BigDecimal("20"),
                new BigDecimal("6"),
                new BigDecimal("7")
        );
        nutrition.setCarbsPer100g(new BigDecimal("30"));
        assertEquals(new BigDecimal("230"), nutrition.getKcalPer100g());
    }
}