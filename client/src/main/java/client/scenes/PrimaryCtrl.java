package client.scenes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class PrimaryCtrl {

    private Stage primaryStage;

    private Scene overviewScene;
    private Scene addRecipeScene;

    private OverviewCtrl overviewCtrl;
    private AddRecipeCtrl addRecipeCtrl;

    /**
     * Initializes  primary controller the scenes
     * @param primaryStage the main application window
     * @param overview  OverviewCtrl controller
     * @param addRecipe AddRecipe controller
     */
    public void init(Stage primaryStage,
                     Pair<OverviewCtrl, Parent> overview,
                     Pair<AddRecipeCtrl, Parent> addRecipe) {

        this.primaryStage = primaryStage;
        this.overviewScene = new Scene(overview.getValue());
        this.addRecipeScene = new Scene(addRecipe.getValue());
        showOverview();
        primaryStage.show();
    }

    /**
     * Method to add to the overviewScene
     */
    public void showOverview() {
        primaryStage.setTitle("Overview");
        primaryStage.setScene(overviewScene);
    }

    /**
     * Method to switch to the addRecipeScene
     */
    public void showAddRecipe() {
        primaryStage.setTitle("Add Recipe");
        primaryStage.setScene(addRecipeScene);
    }
}