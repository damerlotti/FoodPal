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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.http.HttpRequest.BodyPublishers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Ingredient;
import commons.Recipe;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;

public class ServerUtils {

    private static final String SERVER_URL = "http://localhost:8080/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Checks if the server is available
     *
     * @return true/false depending on availability
     */
    public boolean isServerAvailable() {
        try {
            ClientBuilder.newClient(new ClientConfig()) //
                    .target(SERVER_URL) //
                    .request(APPLICATION_JSON) //
                    .get();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof ConnectException) {
                return false;
            }
        }
        return true;
    }

    /**
     * Asks the server for all recipes.
     *
     * @return A list of recipes, or null if something breaks.
     */
    public List<Recipe> getRecipes() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "recipes/"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Error: " + response.statusCode());
                return List.of(); // problem => empty list
            }

            return objectMapper.readValue(response.body(), new TypeReference<List<Recipe>>() {
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return List.of(); // problem => empty list
        }
    }

    /**
     * Sends a new recipe to the server to be saved.
     *
     * @param recipe the recipe object to save.
     * @return the saved recipe (or null if it failed).
     */
    public Recipe addRecipe(Recipe recipe) {
        try {
            String myJson = objectMapper.writeValueAsString(recipe);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "recipes/"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(myJson))
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Error: server returned " + response.statusCode());
                return null;
            }
            return objectMapper.readValue(response.body(), Recipe.class);
        } catch (Exception e) {
            System.out.println("Connection error while adding recipe.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends the update recipe to the server to be saved
     *
     * @param recipe the recipe object to save
     */
    public void updateRecipe(Recipe recipe) {
        try {
            String json = objectMapper.writeValueAsString(recipe);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "recipes/" + recipe.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Error updating recipe: " + response.statusCode());
                return;
            }
            objectMapper.readValue(response.body(), Recipe.class);

        } catch (Exception e) {
            System.out.println("Connection error while updating recipe.");
            e.printStackTrace();
        }
    }

    /**
     * Asks the server for all ingredients.
     *
     * @return A list of ingredients, or null if something breaks.
     */
    public List<Ingredient> getIngredients() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "ingredients/"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Error: " + response.statusCode());
                return List.of(); // problem => empty list
            }

            return objectMapper
                    .readValue(response.body(), new TypeReference<List<Ingredient>>() {
                    });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return List.of(); // problem => empty list
        }
    }

    /**
     * Sends a new ingredient to the server to be saved.
     *
     * @param ingredient the Ingredient object to save.
     * @return the saved Ingredient (or null if it failed).
     */
    public Ingredient addIngredient(Ingredient ingredient) {
        try {
            String myJson = objectMapper.writeValueAsString(ingredient);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "ingredients/"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(myJson))
                    .build();

            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Error: server returned " + response.statusCode());
                return null;
            }
            return objectMapper.readValue(response.body(), Ingredient.class);
        } catch (Exception e) {
            System.out.println("Connection error while adding recipe.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Asks the server to delete an ingredient,by sending its id; IngredientController intercepts it
     *
     * @param ingredient
     */
    public void deleteIngredient(Ingredient ingredient) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "ingredients/" + ingredient.getId()))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Asks the server to delete a recipe by sending its id
     *
     * @param recipe is the recipe being deleted
     */
    public void deleteRecipe(Recipe recipe) {
        try {
            URI uri = URI.create(SERVER_URL + "recipes/" + recipe.getId());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}