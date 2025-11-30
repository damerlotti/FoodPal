package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainLayoutCtrl {

    private final PrimaryCtrl primaryCtrl;

    @FXML
    private StackPane contentArea;

    /**
     * Constructor which injects the PrimaryCtrl, to which we'll tell what to display
     * @param primaryCtrl
     */
    @Inject
    public MainLayoutCtrl(PrimaryCtrl primaryCtrl) {
        this.primaryCtrl = primaryCtrl;
    }

    /**
     * Helper method to replace the center content
     * @param content the actual visuals
     */
    public void setCenter(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    /**
     * Targetting the View -> Recipes menuitem, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showRecipes() {
        primaryCtrl.showOverview();
    }

    /**
     * Targetting the View -> Ingredients menu item, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showIngredients() {
        primaryCtrl.showIngredients();
    }
}