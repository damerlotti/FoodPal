package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {

    private final ServerUtils server;
    private ObservableList<Recipe> data;
    private final PrimaryCtrl primaryCtrl;

    // fxml link, keep in mind that it must match with the id in scene builder
    // that's the left-hand side
    @FXML
    private ListView<Recipe> recipeList;

    // and this is the right-hand side
    @FXML
    private Label recipeTitle;
    @FXML
    private ListView<String> ingredientsList;
    @FXML
    private ListView<String> stepsList;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param server the server utils
     * @param primaryCtrl primaryCtrl
     */
    @Inject
    public OverviewCtrl(ServerUtils server, PrimaryCtrl primaryCtrl) {
        this.server = server;
        this.primaryCtrl = primaryCtrl;
    }

    // runs immediately when the screen opens
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // special javafx list that updates the UI automatically
        data = FXCollections.observableArrayList();
        recipeList.setItems(data);
        recipeList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showRecipeDetails(newValue)
        );
        // the ui list contains our data, currently empty
        refresh();
    }

    /**
     * The logic linked to the refresh button
     */
    public void refresh() {
        //System.out.println("Refreshing list...");
        var recipes = server.getRecipes();
        data.clear();
        data.addAll(recipes);
    }

    /**
     * Shows the details of a recipe (ingredients, preparation steps)
     * @param recipe
     */
    private void showRecipeDetails(Recipe recipe) {
        if (recipe == null) {
            recipeTitle.setText("Select a recipe");
            ingredientsList.getItems().clear();
            stepsList.getItems().clear();
            return;
        }
        recipeTitle.setText(recipe.getName());
        ingredientsList.getItems().clear();
        for (var ing : recipe.getIngredients()) {
            ingredientsList.getItems().add(ing.toString());
        }
        stepsList.getItems().clear();
        for (var step : recipe.getSteps()) {
            stepsList.getItems().add(step.getText());
        }
    }

    /**
     * Switches to add recipe
     */
    @FXML
    public void switchToAddRecipe() {
        primaryCtrl.showAddRecipe();
    }
}