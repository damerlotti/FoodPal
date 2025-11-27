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
package server;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import server.database.IngredientRepository;
import server.database.RecipeRepository;

import java.util.List;

@SpringBootApplication
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    /**
     * Main function
     * @param args arguments for main
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * Method which creates initial data
     * @param recipeRepo the repository of recipes
     * @param ingredientRepo the repository of ingredients
     * @return a CommandLineRunner with a lambda-implemented run method
     */
    @Bean
    public CommandLineRunner demo(RecipeRepository recipeRepo,
                                  IngredientRepository ingredientRepo) {
        return (args) -> {
            // SAFETY CHECK: Only run if the database is empty!
            if (recipeRepo.count() > 0) {
                System.out.println("Database already has data.");
                return;
            }

            Ingredient milk = new Ingredient();
            milk.setName("Milk");
            ingredientRepo.save(milk);

            Recipe pancakes = new Recipe();
            pancakes.setName("Grandma's Pancakes");

            RecipeStep step1 = new RecipeStep();
            step1.setText("Mix it all up");
            step1.setOrderIndex(0);
            step1.setRecipe(pancakes);

            RecipeIngredient usage = new RecipeIngredient();
            usage.setIngredient(milk);
            usage.setRecipe(pancakes);
            usage.setOrderIndex(0);

            pancakes.setSteps(List.of(step1));
            pancakes.setIngredients(List.of(usage));

            recipeRepo.save(pancakes);

            System.out.println("Created initial data.");
        };
    }
}