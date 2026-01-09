/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.config.Config;
import client.config.ConfigManager;
import client.scenes.*;
import com.google.inject.Injector;

import client.utils.ServerUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     *
     * @param args arguments for the main function
     * @throws URISyntaxException exception
     * @throws IOException        exception
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Called by the JavaFX launcher before {@link #start}. We use this to load
     * the global config once so controllers can access it during startup.
     *
     * Development mode can be enabled by passing -Dclient.development=true to the JVM.
     */
    @Override
    public void init() throws Exception {
        super.init();

        // TODO(calco): change how development mode is determined if needed
        ConfigManager confMngr = INJECTOR.getInstance(ConfigManager.class);
        confMngr.init(true);
        Config config = confMngr.getConfig();

        // config initalisation stuff goes here
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, " +
                    "but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var mainLayout = FXML.load(MainLayoutCtrl.class, "client", "scenes", "MainLayout.fxml");
        var recipes = FXML.load(RecipesCtrl.class, "client", "scenes", "Recipes.fxml");
        var addRecipe = FXML.load(AddRecipeCtrl.class, "client", "scenes", "AddRecipe.fxml");
        var ingredients = FXML.load(IngredientsCtrl.class, "client", "scenes", "Ingredients.fxml");
        var addIngredient = FXML.load(AddIngredientCtrl.class,
                "client", "scenes", "AddIngredient.fxml");
        var shoppingList = FXML.load(ShoppingListCtrl.class,
                "client", "scenes", "ShoppingList.fxml");
        // Get PrimaryCtrl from Guice
        var primaryCtrl = INJECTOR.getInstance(PrimaryCtrl.class);

        // Initialize all scenes inside PrimaryCtrl
        primaryCtrl.init(primaryStage, mainLayout, recipes,
                addRecipe, ingredients, addIngredient, shoppingList);

    /**
     * Called when the application is shutting down. Save the global config here.
     */
    @Override
    public void stop() throws Exception {
        // Persist any changes in the global config
        ConfigManager conf = INJECTOR.getInstance(ConfigManager.class);
//        conf.getConfig().setPrefLocale(I18n.getLocaleString());
        conf.save();

        super.stop();
    }
}