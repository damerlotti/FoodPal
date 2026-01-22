package server.api;

import commons.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import server.database.IngredientRepository;
import server.database.RecipeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class IngredientControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeRepository recipeRepository;

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

    @Test
    void testGetIngredientByIdReturnsIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Cumin");
        Ingredient saved = ingredientRepository.save(ingredient);

        ResponseEntity<Ingredient> response = restTemplate.getForEntity(
                "/ingredients/" + saved.getId(), Ingredient.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cumin", response.getBody().getName());
    }

    @Test
    void testUpdateIngredientChangesName() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("OldName");
        Ingredient saved = ingredientRepository.save(ingredient);
        Ingredient updateInfo = new Ingredient();
        updateInfo.setName("NewName");

        ResponseEntity<Ingredient> response = restTemplate.exchange(
                "/ingredients/" + saved.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateInfo),
                Ingredient.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("NewName", response.getBody().getName());

        Ingredient inDb = ingredientRepository.findById(saved.getId()).orElse(null);
        assertNotNull(inDb);
        assertEquals("NewName", inDb.getName());
    }

    @Test
    void testDeleteIngredientRemovesIngredientFromRepo() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("TestIngredientToDelete");
        Ingredient saved = ingredientRepository.save(ingredient);

        restTemplate.delete("/ingredients/" + saved.getId());

        assertFalse(ingredientRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void testDeleteIngredientRemovesItFromAssociatedRecipes() {
        Ingredient flour = new Ingredient();
        flour.setName("Flour");
        flour = ingredientRepository.save(flour);

        Recipe recipe = new Recipe();
        recipe.setName("Cake");

        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(flour);
        ri.setRecipe(recipe);
        ri.setAmount(new Measurement(BigDecimal.valueOf(500), Unit.G));

        recipe.setIngredients(new ArrayList<>(List.of(ri)));
        recipe = recipeRepository.save(recipe);
        final Long recipeId = recipe.getId();

        new TransactionTemplate(transactionManager).execute(status -> {
            assertEquals(1, recipeRepository.findById(recipeId).
                    get().getIngredients().size());
            return null;
        });
        restTemplate.delete("/ingredients/" + flour.getId());
        assertFalse(ingredientRepository.findById(flour.getId()).isPresent());

        new TransactionTemplate(transactionManager).execute(status -> {
            Recipe updatedRecipe = recipeRepository.findById(recipeId).orElse(null);
            assertNotNull(updatedRecipe);
            assertTrue(updatedRecipe.getIngredients().isEmpty(),
                    "Recipe should have 0 ingredients after the ingredient was deleted");
            return null;
        });
    }
}
