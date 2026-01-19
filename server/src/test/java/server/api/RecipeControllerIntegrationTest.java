package server.api;

import commons.Ingredient;
import commons.Measurement;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import server.database.IngredientRepository;
import server.database.RecipeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RecipeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private Ingredient savedIngredient;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        savedIngredient = ingredientRepository.save(new Ingredient(0, "Salt", null));
    }

    @Test
    void testPostRecipeValidReturnsCreatedRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Pasta");

        ResponseEntity<Recipe> response = restTemplate.postForEntity(
                "/recipes/", recipe, Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getId() > 0);
        assertEquals("Pasta", response.getBody().getName());
    }

    @Test
    void testPostRecipeEmptyNameReturnsNull() {
        Recipe recipe = new Recipe();
        recipe.setName("");

        ResponseEntity<Recipe> response = restTemplate.postForEntity(
                "/recipes/", recipe, Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testPostRecipeNullNameReturnsNull() {
        Recipe recipe = new Recipe();
        recipe.setName(null);

        ResponseEntity<Recipe> response = restTemplate.postForEntity(
                "/recipes/", recipe, Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetRecipesReturnsList() {
        Recipe recipe = new Recipe();
        recipe.setName("Test Recipe");
        recipeRepository.save(recipe);

        ResponseEntity<Recipe[]> response = restTemplate.getForEntity(
                "/recipes/", Recipe[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
    }

    @Test
    void testGetRecipeByIdExistsReturnsRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Soup");
        Recipe saved = recipeRepository.save(recipe);

        ResponseEntity<Recipe> response = restTemplate.getForEntity(
                "/recipes/" + saved.getId(), Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Soup", response.getBody().getName());
    }

    @Test
    void testGetRecipeByIdNotFoundReturnsNull() {
        ResponseEntity<Recipe> response = restTemplate.getForEntity(
                "/recipes/999", Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetRecipeCountReturnsCorrectCount() {
        Recipe r1 = new Recipe();
        r1.setName("Recipe 1");
        Recipe r2 = new Recipe();
        r2.setName("Recipe 2");
        recipeRepository.save(r1);
        recipeRepository.save(r2);

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/recipes/count", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Total recipes: 2", response.getBody());
    }

    @Test
    void testPutRecipeExistsUpdatesRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Old Name");
        Recipe saved = recipeRepository.save(recipe);

        Recipe updated = new Recipe();
        updated.setName("New Name");

        ResponseEntity<Recipe> response = restTemplate.exchange(
                "/recipes/" + saved.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updated),
                Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Name", response.getBody().getName());
    }

    @Test
    void testPutRecipeNotFoundReturnsNull() {
        Recipe updated = new Recipe();
        updated.setName("New Name");

        ResponseEntity<Recipe> response = restTemplate.exchange(
                "/recipes/999",
                HttpMethod.PUT,
                new HttpEntity<>(updated),
                Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteRecipeRemovesRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("To Delete");
        Recipe saved = recipeRepository.save(recipe);

        restTemplate.delete("/recipes/" + saved.getId());

        ResponseEntity<Recipe> response = restTemplate.getForEntity(
                "/recipes/" + saved.getId(), Recipe.class);

        assertNull(response.getBody());
    }

    @Test
    void testPostRecipeWithStepsSavesSteps() {
        Recipe recipe = new Recipe();
        recipe.setName("Recipe With Steps");

        RecipeStep step1 = new RecipeStep();
        step1.setText("Boil water");
        step1.setRecipe(recipe);

        RecipeStep step2 = new RecipeStep();
        step2.setText("Add pasta");
        step2.setRecipe(recipe);

        recipe.setSteps(new ArrayList<>(List.of(step1, step2)));

        ResponseEntity<Recipe> response = restTemplate.postForEntity(
                "/recipes/", recipe, Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getSteps().size());
    }

    @Test
    void testPostRecipeWithIngredientsSavesIngredients() {
        Recipe recipe = new Recipe();
        recipe.setName("Recipe With Ingredients");

        RecipeIngredient ri = new RecipeIngredient();
        ri.setIngredient(savedIngredient);
        ri.setAmount(new Measurement(BigDecimal.ONE, Unit.G));
        ri.setRecipe(recipe);

        recipe.setIngredients(new ArrayList<>(List.of(ri)));

        ResponseEntity<Recipe> response = restTemplate.postForEntity(
                "/recipes/", recipe, Recipe.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getIngredients().size());
    }
}
