package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {

    private final ServerUtils server;
    private ObservableList<Recipe> data;

    // fxml link, keep in mind that it must match with the id in scene builder
    @FXML
    private ListView<Recipe> recipeList;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param server the server
     */
    @Inject
    public OverviewCtrl(ServerUtils server) {
        this.server = server;
    }

    // runs immediately when the screen opens
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // special javafx list that updates the UI automatically
        data = FXCollections.observableArrayList();
        recipeList.setItems(data);
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
}