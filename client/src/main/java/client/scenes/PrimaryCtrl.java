package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

public class PrimaryCtrl {

    private Stage primaryStage;
    private MainLayoutCtrl mainLayoutCtrl;

    private Parent overviewNode;
    private OverviewCtrl overviewCtrl;

    private Parent addRecipeNode;
    // the problem with just the visuals was that every time we instantiated
    // a new scene for the same node, which won't work
    private Scene addRecipeScene;
    private AddRecipeCtrl addRecipeCtrl;

    private Parent ingredientsNode;
    private IngredientsCtrl ingredientsCtrl;

    private Parent ingredientAddNode;
    private Scene ingredientAddScene;
    private IngredientAddCtrl ingredientAddCtrl;

    /**
     * Initializes  primary controller the scenes
     * @param primaryStage the main application window
     * @param mainLayout MainLayout controller
     * @param overview  OverviewCtrl controller
     * @param addRecipe AddRecipe controller
     * @param ingredients Ingredients controller
     * @param ingredientAdd IngredientAdd controller
     */
    public void init(Stage primaryStage,
                     Pair<MainLayoutCtrl, Parent> mainLayout,
                     Pair<OverviewCtrl, Parent> overview,
                     Pair<AddRecipeCtrl, Parent> addRecipe,
                     Pair<IngredientsCtrl, Parent> ingredients,
                     Pair<IngredientAddCtrl, Parent> ingredientAdd) {

        this.primaryStage = primaryStage;

        this.mainLayoutCtrl = mainLayout.getKey();

        this.overviewCtrl = overview.getKey();
        this.overviewNode = overview.getValue();

        this.addRecipeCtrl = addRecipe.getKey();
        this.addRecipeNode = addRecipe.getValue();
        this.addRecipeScene = new Scene(addRecipeNode);

        this.ingredientsCtrl = ingredients.getKey();
        this.ingredientsNode = ingredients.getValue();

        this.ingredientAddCtrl = ingredientAdd.getKey();
        this.ingredientAddNode = ingredientAdd.getValue();
        this.ingredientAddScene = new Scene(ingredientAddNode);

        primaryStage.setScene(new Scene(mainLayout.getValue()));
        primaryStage.setTitle("FoodPal");
        showOverview();
        primaryStage.show();
    }

    /**
     * Shows the menu with recipes
     */
    public void showOverview() {
        primaryStage.setTitle("FoodPal - Recipes");
        mainLayoutCtrl.setCenter(overviewNode);
        overviewCtrl.refresh();
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
        overviewCtrl.refresh();
    }

    /**
     * Shows the menu with ingredients
     */
    public void showIngredients() {
        primaryStage.setTitle("FoodPal - Ingredients");
        mainLayoutCtrl.setCenter(ingredientsNode);
        ingredientsCtrl.refresh();
    }

    /**
     * Shows the add ingredient scene
     */
    public void showAddIngredient() { //AI generated this
        Stage p = new Stage();
        p.initModality(Modality.APPLICATION_MODAL);
        p.initOwner(primaryStage);
        p.setScene(ingredientAddScene);
        p.setTitle("Add Ingredient");
        p.showAndWait();
        ingredientsCtrl.refresh();
    }
}
