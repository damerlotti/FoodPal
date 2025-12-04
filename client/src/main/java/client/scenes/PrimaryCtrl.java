package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

public class PrimaryCtrl {

    private Stage primaryStage;
    private MainLayoutCtrl mainLayoutCtrl;

    private Parent recipesNode;
    private RecipesCtrl recipesCtrl;

    private Parent addRecipeNode;
    // the problem with just the visuals was that every time we instantiated
    // a new scene for the same node, which won't work
    private Scene addRecipeScene;
    private AddRecipeCtrl addRecipeCtrl;

    private Parent ingredientsNode;
    private IngredientsCtrl ingredientsCtrl;

    /**
     * Initializes  primary controller the scenes
     * @param primaryStage the main application window
     * @param mainLayout MainLayout controller
     * @param recipes  RecipesCtrl controller
     * @param addRecipe AddRecipe controller
     * @param ingredients Ingredients controller
     */
    public void init(Stage primaryStage,
                     Pair<MainLayoutCtrl, Parent> mainLayout,
                     Pair<RecipesCtrl, Parent> recipes,
                     Pair<AddRecipeCtrl, Parent> addRecipe,
                     Pair<IngredientsCtrl, Parent> ingredients) {

        this.primaryStage = primaryStage;

        this.mainLayoutCtrl = mainLayout.getKey();

        this.recipesCtrl = recipes.getKey();
        this.recipesNode = recipes.getValue();

        this.addRecipeCtrl = addRecipe.getKey();
        this.addRecipeNode = addRecipe.getValue();
        this.addRecipeScene = new Scene(addRecipeNode);

        this.ingredientsCtrl = ingredients.getKey();
        this.ingredientsNode = ingredients.getValue();

        primaryStage.setScene(new Scene(mainLayout.getValue()));
        primaryStage.setTitle("FoodPal");
        showRecipes();
        primaryStage.show();
    }

    /**
     * Shows the menu with recipes
     */
    public void showRecipes() {
        primaryStage.setTitle("FoodPal - Recipes");
        mainLayoutCtrl.setCenter(recipesNode);
        recipesCtrl.refresh();
    }

    /**
     * Method to switch to the addRecipeScene
     */
    public void showAddRecipe() {
        Stage p = new Stage();

        // this is so that users can not do anything else in the main window until this closes
        p.initModality(Modality.APPLICATION_MODAL);
        p.initOwner(primaryStage);

        p.setScene(addRecipeScene);
        p.setTitle("Add Recipe");
        p.showAndWait();
        recipesCtrl.refresh();
    }

    /**
     * Shows the menu with ingredients
     */
    public void showIngredients() {
        primaryStage.setTitle("FoodPal - Ingredients");
        mainLayoutCtrl.setCenter(ingredientsNode);
        ingredientsCtrl.refresh();
    }
}