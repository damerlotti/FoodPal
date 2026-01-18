package server.api;

import commons.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import server.database.IngredientRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class IngredientControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void testPostIngredientValidReturnsCreatedIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");

        ResponseEntity<Ingredient> response = restTemplate.postForEntity(
                "/ingredients/", ingredient, Ingredient.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getId() > 0);
        assertEquals("Salt", response.getBody().getName());
    }

    @Test
    void testPostIngredientEmptyNameReturnsNull() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("");

        ResponseEntity<Ingredient> response = restTemplate.postForEntity(
                "/ingredients/", ingredient, Ingredient.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testPostIngredientNullNameReturnsNull() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(null);

        ResponseEntity<Ingredient> response = restTemplate.postForEntity(
                "/ingredients/", ingredient, Ingredient.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetIngredientsReturnsList() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Pepper");
        ingredientRepository.save(ingredient);

        ResponseEntity<Ingredient[]> response = restTemplate.getForEntity(
                "/ingredients/", Ingredient[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    void testDeleteIngredientRemovesIngredient() {
        // Create an ingredient that is not referenced by any recipe
        Ingredient ingredient = new Ingredient();
        ingredient.setName("TestIngredientToDelete");
        Ingredient saved = ingredientRepository.save(ingredient);

        restTemplate.delete("/ingredients/" + saved.getId());

        assertFalse(ingredientRepository.findById(saved.getId()).isPresent());
    }
}
