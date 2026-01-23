package client.scenes;

import client.i18n.I18n;
import commons.Ingredient;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.List;

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

    private Parent addIngredientNode;
    private Scene addIngredientScene;
    private AddIngredientCtrl addIngredientCtrl;

    private Scene editIngredientScene;
    private EditIngredientCtrl editIngredientCtrl;

    private Parent shoppingListNode;
    private ShoppingListCtrl shoppingListCtrl;

    /**
     * Initializes  primary controller the scenes
     * @param primaryStage the main application window
     * @param mainLayout MainLayout controller
     * @param recipes  RecipesCtrl controller
     * @param addRecipe AddRecipe controller
     * @param ingredients Ingredients controller
     * @param ingredientAdd IngredientAdd controller
     */
    public void init(Stage primaryStage,
                     Pair<MainLayoutCtrl, Parent> mainLayout,
                     Pair<RecipesCtrl, Parent> recipes,
                     Pair<AddRecipeCtrl, Parent> addRecipe,
                     Pair<IngredientsCtrl, Parent> ingredients,
                     Pair<AddIngredientCtrl, Parent> ingredientAdd) {

        this.primaryStage = primaryStage;
        this.mainLayoutCtrl = mainLayout.getKey();

        this.recipesCtrl = recipes.getKey();
        this.recipesNode = recipes.getValue();

        this.addRecipeCtrl = addRecipe.getKey();
        this.addRecipeNode = addRecipe.getValue();
        this.addRecipeScene = new Scene(addRecipeNode);

        this.ingredientsCtrl = ingredients.getKey();
        this.ingredientsNode = ingredients.getValue();

        this.addIngredientCtrl = ingredientAdd.getKey();
        this.addIngredientNode = ingredientAdd.getValue();
        this.addIngredientScene = new Scene(addIngredientNode);

        primaryStage.setScene(new Scene(mainLayout.getValue()));
        primaryStage.setTitle(I18n.get("window.main"));
        
        // Initialize language filter with the selected languages from MainLayout
        recipesCtrl.setFilteredLanguages(mainLayoutCtrl.getSelectedLanguageTags());
        
        showRecipes();
        primaryStage.show();
    }

    /**
     * A second temporary init method because
     * I couldnt figure anything else that satisfied checkstyle
     * @param ingredientEdit IngredientEdit controller
     * @param shoppingList ShoppingList controller
     */
    public void initEditAndShopping(Pair<EditIngredientCtrl, Parent> ingredientEdit,
                                    Pair<ShoppingListCtrl, Parent> shoppingList){
        this.editIngredientCtrl = ingredientEdit.getKey();
        this.editIngredientScene = new Scene(ingredientEdit.getValue());

        this.shoppingListCtrl = shoppingList.getKey();
        this.shoppingListNode = shoppingList.getValue();
    }

    /**
     * Shows the menu with recipes
     */
    public void showRecipes() {
        primaryStage.setTitle(I18n.get("window.recipes"));
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
        p.setTitle(I18n.get("window.addRecipe"));
        p.showAndWait();
        recipesCtrl.refresh();
    }

    /**
     * Shows the menu with ingredients
     */
    public void showIngredients() {
        primaryStage.setTitle(I18n.get("window.ingredients"));
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
        p.setScene(addIngredientScene);
        p.setTitle(I18n.get("window.addIngredient"));
        p.showAndWait();
        ingredientsCtrl.refresh();
    }

    /**
     * Shows the ingredient edit scene.
     * @param ingredient ingredient to be edited.
     */
    public void showEditIngredient(Ingredient ingredient){
        Stage p = new Stage();
        p.initModality(Modality.APPLICATION_MODAL);
        p.initOwner(primaryStage);
        p.setScene(editIngredientScene);
        p.setTitle(I18n.get("window.editIngredient"));
        editIngredientCtrl.setIngredient(ingredient);
        p.showAndWait();
        ingredientsCtrl.refresh();
    }

    /**
     * Shows the Shopping List scene
     */
    public void showShoppingList() {
        primaryStage.setTitle(I18n.get("window.shoppingList"));
        mainLayoutCtrl.setCenter(shoppingListNode);
    }

    /**
     * Called when recipe language filter changes.
     * @param selectedLanguageTags the list of selected language tags
     */
    public void onRecipeLanguagesChanged(List<String> selectedLanguageTags) {
        if (recipesCtrl != null) {
            recipesCtrl.setFilteredLanguages(selectedLanguageTags);
        }
    }
}
