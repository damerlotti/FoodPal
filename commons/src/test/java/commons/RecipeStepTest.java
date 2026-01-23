package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecipeStepTest {
    @Test
    void testToStringFirstStep() {
        RecipeStep step = new RecipeStep(1L, "Mix the ingredients");
        step.setOrderIndex(0);
        assertEquals("1. Mix the ingredients", step.toString());
    }

    @Test
    void testToStringLaterStep() {
        RecipeStep step = new RecipeStep(2L, "Bake for 30 minutes");
        step.setOrderIndex(4);
        assertEquals("5. Bake for 30 minutes", step.toString());
    }

    @Test
    void testEqualsWithSameId() {
        RecipeStep step1 = new RecipeStep(5L, "Step A");
        RecipeStep step2 = new RecipeStep(5L, "Step B");
        assertEquals(step1, step2);
    }

    @Test
    void testEqualsWithDifferentId() {
        RecipeStep step1 = new RecipeStep(1L, "Same text");
        RecipeStep step2 = new RecipeStep(2L, "Same text");
        assertNotEquals(step1, step2);
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        RecipeStep step1 = new RecipeStep(5L, "Text A");
        RecipeStep step2 = new RecipeStep(5L, "Text B");
        assertEquals(step1.hashCode(), step2.hashCode());
    }
}
