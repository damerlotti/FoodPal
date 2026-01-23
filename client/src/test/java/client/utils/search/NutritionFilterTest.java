package client.utils.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NutritionFilterTest {

    @Test
    void matchesLessThan() {
        NutritionFilter filter = new NutritionFilter(
                NutritionField.KCAL, ComparisonOperator.LESS_THAN, 500);

        assertTrue(filter.matches(499));
        assertTrue(filter.matches(0));
        assertFalse(filter.matches(500));
        assertFalse(filter.matches(501));
    }

    @Test
    void matchesEqual() {
        NutritionFilter filter = new NutritionFilter(
                NutritionField.FAT, ComparisonOperator.EQUAL, 100);

        assertTrue(filter.matches(100));
        assertFalse(filter.matches(99));
        assertFalse(filter.matches(101));
    }

    @Test
    void matchesGreaterThan() {
        NutritionFilter filter = new NutritionFilter(
                NutritionField.PROTEIN, ComparisonOperator.GREATER_THAN, 50);

        assertTrue(filter.matches(51));
        assertTrue(filter.matches(1000));
        assertFalse(filter.matches(50));
        assertFalse(filter.matches(49));
    }

    @Test
    void testEqualsAndHashCode() {
        NutritionFilter filter1 = new NutritionFilter(
                NutritionField.KCAL, ComparisonOperator.LESS_THAN, 500);
        NutritionFilter filter2 = new NutritionFilter(
                NutritionField.KCAL, ComparisonOperator.LESS_THAN, 500);
        NutritionFilter filter3 = new NutritionFilter(
                NutritionField.FAT, ComparisonOperator.LESS_THAN, 500);

        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());
        assertNotEquals(filter1, filter3);
    }

    @Test
    void testToString() {
        NutritionFilter filter = new NutritionFilter(
                NutritionField.KCAL, ComparisonOperator.LESS_THAN, 500);

        String str = filter.toString();
        assertTrue(str.contains("KCAL"));
        assertTrue(str.contains("LESS_THAN"));
        assertTrue(str.contains("500"));
    }
}
