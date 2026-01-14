package client.scenes;

import client.config.ConfigManager;
import commons.Recipe;
import jakarta.inject.Inject;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;
import java.util.HashSet;
import java.util.Set;
//this class has been generated with AI
public class RecipeListCellFactory implements Callback<ListView<Recipe>, ListCell<Recipe>> {

    private final ConfigManager configManager;
    /**
     * Constructor
     * @param configManager the config manager to use for storing favorites
     */
    @Inject
    public RecipeListCellFactory(ConfigManager configManager) {
        this.configManager = configManager;
    }


    @Override
    public ListCell<Recipe> call(ListView<Recipe> listView) {
        return new RecipeListCell();
    }

    private class RecipeListCell extends ListCell<Recipe> {
        private final HBox hbox = new HBox(8);
        private final Text text = new Text();
        private final Button starButton = new Button();
        private final SVGPath starShape = new SVGPath();

        public RecipeListCell() {
            // Configure star SVG
            starShape.setContent("M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19" +
                    ",8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z");
            starButton.setGraphic(starShape);

            // Initial styling - ALL INLINE, NO CSS
            starButton.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-padding: 5; " +
                            "-fx-cursor: hand;"
            );

            // Set initial star color to gray
            starShape.setStyle("-fx-fill: gray; -fx-opacity: 0.6;");

            // Layout
            HBox.setHgrow(text, Priority.ALWAYS);
            hbox.getChildren().addAll(starButton, text);
            hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Click handler - toggle favorite
            starButton.setOnAction(event -> {
                Recipe recipe = getItem();
                if (recipe != null) {
                    long recipeId = recipe.getId();
                    String recipeName = recipe.getName();

                    // Get config from manager
                    var config = configManager.getConfig();

                    // Toggle favorite status in config
                    if (config.isFavorite(recipeId)) {
                        config.removeFavorite(recipeId);
                        starShape.setStyle("-fx-fill: gray; -fx-opacity: 0.6;");
                    } else {
                        config.addFavorite(recipeId, recipeName);
                        starShape.setStyle("-fx-fill: gold;");
                    }

                    // IMPORTANT: Save config to disk after changes
                    configManager.save();

                    System.out.println("Star toggled for: " + recipe.getName() +
                            " (Favorite: " + config.isFavorite(recipeId) + ")");
                    updateItem(recipe, false);
                }
            });

            // Hover effects - also inline
            starButton.setOnMouseEntered(e -> {
                Recipe recipe = getItem();
                if (recipe != null) {
                    if (configManager.getConfig().isFavorite(recipe.getId())) {
                        // Already favorited - make gold brighter
                        starShape.setStyle("-fx-fill: gold; -fx-opacity: 1.0;");
                    } else {
                        // Not favorited - make gray brighter
                        starShape.setStyle("-fx-fill: gray; -fx-opacity: 1.0;");
                    }
                }
            });

            starButton.setOnMouseExited(e -> {
                Recipe recipe = getItem();
                if (recipe != null) {
                    if (configManager.getConfig().isFavorite(recipe.getId())) {
                        // Already favorited - solid gold
                        starShape.setStyle("-fx-fill: gold;");
                    } else {
                        // Not favorited - faded gray
                        starShape.setStyle("-fx-fill: gray; -fx-opacity: 0.6;");
                    }
                }
            });
        }

        @Override
        protected void updateItem(Recipe recipe, boolean empty) {
            super.updateItem(recipe, empty);

            if (empty || recipe == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                text.setText(recipe.getName());

                // Update star appearance based on favorite status
                if (configManager.getConfig().isFavorite(recipe.getId())) {
                    starShape.setStyle("-fx-fill: gold;");
                    setStyle("-fx-background-color: #fff8e1; " +  // Light gold background
                            "-fx-border-color: #ffd54f; " +       // Gold border
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;");
                } else {
                    starShape.setStyle("-fx-fill: gray; -fx-opacity: 0.6;");
                    setStyle("");
                }

                setGraphic(hbox);
            }
        }
    }

    /**
     * Helper method to check if a recipe is favorite
     * @param recipeId the id of the recipe
     * @return true if the recipe is favourite, else false
     */
    public boolean isFavorite(long recipeId) {  // Remove 'static'
        return configManager.getConfig().isFavorite(recipeId);
    }

    /**
     * Helper method to get all favorite IDs
     * @return returns a set of all recipe's ID of those that are favourite
     */
    public Set<Long> getFavoriteIds() {
        return new HashSet<>(configManager.getConfig().getFavoriteRecipes().keySet());
    }

    /**
     * getter for the config manager
     * @return the config manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}