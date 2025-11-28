package server;

import commons.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
// NOTE: Some ingredients may serialize as IDs due to Jackson circular references
// This is a known minor issue that doesn't affect functionality
@Component
public class DataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;

    /**
     * Constructs a new DataInitializer with the specified EntityManager.
     * @param entityManager the EntityManager used for database operations
     */
    public DataInitializer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Executes after Spring Boot application starts. Initializes the database with sample data
     * if no recipes currently exist.
     * @param args the command line arguments passed to the application
     * @throws Exception if any error occurs during data initialization
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (dataExists()) {
            System.out.println("Data already exists, skipping initialization.");
            return;
        }

        System.out.println("Initializing sample recipes...");
        initializeIngredientsAndRecipes();
        System.out.println("5 sample recipes initialized successfully!");
    }

    /**
     * Checks whether recipe data already exists in the database.
     * @return true if recipes exist in the database, false otherwise
     */
    private boolean dataExists() {
        Long recipeCount = (Long) entityManager
                .createQuery("SELECT COUNT(r) FROM Recipe r")
                .getSingleResult();
        return recipeCount > 0;
    }

    /**
     * Initializes all sample ingredients and recipes in the database.
     * Creates 20 common ingredients and 5 sample recipes with their respective
     * ingredients and preparation steps.
     */
    private void initializeIngredientsAndRecipes() {
        // Create all ingredients
        Ingredient zucchini = createIngredient("Zucchini");
        Ingredient spaghetti = createIngredient("Spaghetti");
        Ingredient eggs = createIngredient("Eggs");
        Ingredient pancetta = createIngredient("Pancetta");
        Ingredient parmesan = createIngredient("Parmesan Cheese");
        Ingredient chicken = createIngredient("Chicken Breast");
        Ingredient bellPepper = createIngredient("Bell Pepper");
        Ingredient broccoli = createIngredient("Broccoli");
        Ingredient soySauce = createIngredient("Soy Sauce");
        Ingredient flour = createIngredient("All-purpose Flour");
        Ingredient butter = createIngredient("Butter");
        Ingredient chocolate = createIngredient("Chocolate Chips");
        Ingredient groundBeef = createIngredient("Ground Beef");
        Ingredient lettuce = createIngredient("Lettuce");
        Ingredient tomato = createIngredient("Tomato");
        Ingredient cheese = createIngredient("Cheese");
        Ingredient onion = createIngredient("Onion");
        Ingredient garlic = createIngredient("Garlic");
        Ingredient milk = createIngredient("Milk");
        Ingredient sugar = createIngredient("Sugar");
        Ingredient bakingSoda = createIngredient("Baking Soda");

        // Create recipes
        createSpaghettiCarbonara(spaghetti, eggs, pancetta, parmesan);
        createChickenStirFry(chicken, bellPepper, broccoli, soySauce);
        createChocolateChipCookies(flour, butter, sugar, eggs, chocolate, bakingSoda);
        createBeefTacos(groundBeef, onion, garlic, cheese, lettuce, tomato);
        createVegetableLasagna(flour, bellPepper, zucchini, cheese, tomato);
    }

    /**
     * Creates and persists a Spaghetti Carbonara recipe with its ingredients and steps.
     * @param spaghetti the spaghetti ingredient
     * @param eggs the eggs ingredient
     * @param pancetta the pancetta ingredient
     * @param parmesan the parmesan cheese ingredient
     */
    private void createSpaghettiCarbonara(Ingredient spaghetti, Ingredient eggs,
                                          Ingredient pancetta, Ingredient parmesan) {
        Recipe carbonara = createRecipe("Spaghetti Carbonara");
        addIngredientToRecipe(carbonara, spaghetti, new BigDecimal("400"), Unit.G);
        addIngredientToRecipe(carbonara, eggs, new BigDecimal("3"), Unit.PIECE);
        addIngredientToRecipe(carbonara, pancetta, new BigDecimal("150"), Unit.G);
        addIngredientToRecipe(carbonara, parmesan, new BigDecimal("100"), Unit.G);
        addStepToRecipe(carbonara, "Cook spaghetti in boiling salted water until al dente.", 0);
        addStepToRecipe(carbonara, "Cook pancetta in a pan until crispy.", 1);
        addStepToRecipe(carbonara, "Beat eggs with grated parmesan cheese.", 2);
        addStepToRecipe(carbonara,
                "Combine hot pasta with pancetta, then mix with egg mixture off heat.", 3);
        entityManager.persist(carbonara);
    }

    /**
     * Creates and persists a Chicken Stir Fry recipe with its ingredients and steps.
     * @param chicken the chicken breast ingredient
     * @param bellPepper the bell pepper ingredient
     * @param broccoli the broccoli ingredient
     * @param soySauce the soy sauce ingredient
     */
    private void createChickenStirFry(Ingredient chicken, Ingredient bellPepper,
                                      Ingredient broccoli, Ingredient soySauce) {
        Recipe stirFry = createRecipe("Chicken Stir Fry");
        addIngredientToRecipe(stirFry, chicken, new BigDecimal("500"), Unit.G);
        addIngredientToRecipe(stirFry, bellPepper, new BigDecimal("2"), Unit.PIECE);
        addIngredientToRecipe(stirFry, broccoli, new BigDecimal("1"), Unit.PIECE);
        addIngredientToRecipe(stirFry, soySauce, new BigDecimal("3"), Unit.TBSP);
        addStepToRecipe(stirFry, "Cut chicken into strips and season.", 0);
        addStepToRecipe(stirFry, "Stir-fry vegetables in a hot wok for 3-4 minutes.", 1);
        addStepToRecipe(stirFry, "Add chicken and cook until done.", 2);
        addStepToRecipe(stirFry, "Add soy sauce and simmer for 2 minutes.", 3);
        entityManager.persist(stirFry);
    }

    /**
     * Creates and persists a Chocolate Chip Cookies recipe with its ingredients and steps.
     * @param flour the all-purpose flour ingredient
     * @param butter the butter ingredient
     * @param sugar the sugar ingredient
     * @param eggs the eggs ingredient
     * @param chocolate the chocolate chips ingredient
     * @param bakingSoda the baking soda ingredient
     */
    private void createChocolateChipCookies(Ingredient flour, Ingredient butter,
                                            Ingredient sugar, Ingredient eggs,
                                            Ingredient chocolate, Ingredient bakingSoda) {
        Recipe cookies = createRecipe("Chocolate Chip Cookies");
        addIngredientToRecipe(cookies, flour, new BigDecimal("2.5"), Unit.CUP);
        addIngredientToRecipe(cookies, butter, new BigDecimal("1"), Unit.CUP);
        addIngredientToRecipe(cookies, sugar, new BigDecimal("0.75"), Unit.CUP);
        addIngredientToRecipe(cookies, eggs, new BigDecimal("2"), Unit.PIECE);
        addIngredientToRecipe(cookies, chocolate, new BigDecimal("2"), Unit.CUP);
        addIngredientToRecipe(cookies, bakingSoda, new BigDecimal("1"), Unit.TSP);
        addStepToRecipe(cookies, "Preheat oven to 350°F (175°C).", 0);
        addStepToRecipe(cookies, "Cream butter and sugars together.", 1);
        addStepToRecipe(cookies, "Add eggs and vanilla, mix well.", 2);
        addStepToRecipe(cookies, "Combine dry ingredients and mix into wet ingredients.", 3);
        addStepToRecipe(cookies, "Fold in chocolate chips.", 4);
        addStepToRecipe(cookies, "Bake for 10-12 minutes until golden brown.", 5);
        entityManager.persist(cookies);
    }

    /**
     * Creates and persists a Beef Tacos recipe with its ingredients and steps.
     * @param groundBeef the ground beef ingredient
     * @param onion the onion ingredient
     * @param garlic the garlic ingredient
     * @param cheese the cheese ingredient
     * @param lettuce the lettuce ingredient
     * @param tomato the tomato ingredient
     */
    private void createBeefTacos(Ingredient groundBeef, Ingredient onion,
                                 Ingredient garlic, Ingredient cheese,
                                 Ingredient lettuce, Ingredient tomato) {
        Recipe tacos = createRecipe("Beef Tacos");
        addIngredientToRecipe(tacos, groundBeef, new BigDecimal("500"), Unit.G);
        addIngredientToRecipe(tacos, onion, new BigDecimal("1"), Unit.PIECE);
        addIngredientToRecipe(tacos, garlic, new BigDecimal("2"), Unit.PIECE);
        addIngredientToRecipe(tacos, cheese, new BigDecimal("200"), Unit.G);
        addIngredientToRecipe(tacos, lettuce, new BigDecimal("1"), Unit.PIECE);
        addIngredientToRecipe(tacos, tomato, new BigDecimal("2"), Unit.PIECE);
        addStepToRecipe(tacos, "Brown ground beef with chopped onion and garlic.", 0);
        addStepToRecipe(tacos, "Season with taco spices.", 1);
        addStepToRecipe(tacos, "Prepare toppings: chop lettuce, dice tomatoes, grate cheese.", 2);
        addStepToRecipe(tacos, "Warm taco shells and assemble with beef and toppings.", 3);
        entityManager.persist(tacos);
    }

    /**
     * Creates and persists a Vegetable Lasagna recipe with its ingredients and steps.
     * @param flour the flour ingredient (representing lasagna sheets)
     * @param bellPepper the bell pepper ingredient
     * @param zucchini the zucchini ingredient
     * @param cheese the cheese ingredient
     * @param tomato the tomato ingredient
     */
    private void createVegetableLasagna(Ingredient flour, Ingredient bellPepper,
                                        Ingredient zucchini, Ingredient cheese,
                                        Ingredient tomato) {
        Recipe lasagna = createRecipe("Vegetable Lasagna");
        addIngredientToRecipe(lasagna, flour, new BigDecimal("12"), Unit.PIECE);
        addIngredientToRecipe(lasagna, bellPepper, new BigDecimal("2"), Unit.PIECE);
        addIngredientToRecipe(lasagna, zucchini, new BigDecimal("2"), Unit.PIECE);
        addIngredientToRecipe(lasagna, cheese, new BigDecimal("300"), Unit.G);
        addIngredientToRecipe(lasagna, tomato, new BigDecimal("4"), Unit.PIECE);
        addStepToRecipe(lasagna, "Preheat oven to 375°F (190°C).", 0);
        addStepToRecipe(lasagna, "Sauté vegetables until tender.", 1);
        addStepToRecipe(lasagna, "Layer lasagna sheets with vegetables and cheese.", 2);
        addStepToRecipe(lasagna, "Bake for 45 minutes until bubbly and golden.", 3);
        entityManager.persist(lasagna);
    }

    /**
     * Creates and persists a new Ingredient with the specified name.
     * @param name the name of the ingredient to create
     * @return the created and persisted Ingredient object
     */
    private Ingredient createIngredient(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        entityManager.persist(ingredient);
        return ingredient;
    }

    /**
     * Creates a new Recipe object with the specified name.
     * Note: This method only creates the object but does not persist it.
     * @param name the name of the recipe to create
     * @return the created Recipe object
     */
    private Recipe createRecipe(String name) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        return recipe;
    }

    /**
     * Adds an ingredient to a recipe with the specified amount and unit.
     * Creates a RecipeIngredient association between the recipe and ingredient.
     * @param recipe the recipe to add the ingredient to
     * @param ingredient the ingredient to add to the recipe
     * @param amount the amount of the ingredient needed
     * @param unit the unit of measurement for the amount
     */
    private void addIngredientToRecipe(Recipe recipe, Ingredient ingredient,
                                       BigDecimal amount, Unit unit) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(new Measurement(amount, unit));
        recipeIngredient.setOrderIndex(recipe.getIngredients().size());
        recipeIngredient.setRecipe(recipe);
        recipe.getIngredients().add(recipeIngredient);
    }

    /**
     * Adds a preparation step to a recipe with the specified text and order index.
     * Creates a RecipeStep and associates it with the recipe.
     * @param recipe the recipe to add the step to
     * @param text the text description of the preparation step
     * @param order the order index of the step in the recipe's step sequence
     */
    private void addStepToRecipe(Recipe recipe, String text, int order) {
        RecipeStep step = new RecipeStep();
        step.setText(text);
        step.setOrderIndex(order);
        step.setRecipe(recipe);
        recipe.getSteps().add(step);
    }
}