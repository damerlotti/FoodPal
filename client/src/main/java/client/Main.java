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

    @Override
    public void start(Stage primaryStage) throws Exception {
        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, " +
                    "but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        // Some testing for the ServerUtils, to see if we can get all the recipes from the client
        // Can be removed
        System.out.println("TESTING SERVER CONNECTION");

        client.utils.ServerUtils server = new client.utils.ServerUtils();

        var recipes = server.getRecipes();

        System.out.println("Connection successful!");
        System.out.println("Found " + recipes.size() + " recipes:");
        for (commons.Recipe r : recipes) {
            System.out.println("   - " + r.getName());
        }

        var mainLayout = FXML.load(MainLayoutCtrl.class, "client", "scenes", "MainLayout.fxml");
        var overview = FXML.load(OverviewCtrl.class, "client", "scenes", "Overview.fxml");
        var addRecipe = FXML.load(AddRecipeCtrl.class, "client", "scenes", "AddRecipe.fxml");
        var ingredients = FXML.load(IngredientsCtrl.class, "client", "scenes", "Ingredients.fxml");

        // Get PrimaryCtrl from Guice
        var primaryCtrl = INJECTOR.getInstance(PrimaryCtrl.class);

        // Initialize all scenes inside PrimaryCtrl
        primaryCtrl.init(primaryStage, mainLayout, overview, addRecipe, ingredients);

    }
}