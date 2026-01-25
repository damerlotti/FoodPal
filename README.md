# CSEP Template Project

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

    mvn -pl server -am spring-boot:run

to run the server and

    mvn -pl client -am javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Get the template project running from the command line first to ensure you have the required tools on your sytem.

Once it is working, you can try importing the project into your favorite IDE. Especially the client is a bit more tricky to set up there due to the dependency on a JavaFX SDK.
To help you get started, you can find additional instructions in the corresponding README of the client project.

## Extra Features

### 4.2 Automated Change Synchronization

As an addition to this epic, we have implemented synchronization for the ingredients page as well. When a user adds, edits, or deletes an ingredient, this gets reflected across all other clients as well. This was done by a subscription to a channel specific for ingredients.

### 4.3 Nutritional Value

As an addition to this epic, we implemented tracking of 2 more different nutritional values (**sugar** and **sodium**).
Even further, in order to facilitate the user's potential needs for tracking and easily finding recipes befitting their current nutritional needs, we added a custom querying system allowing them to easily filter and find recipes with different nutrional information (more information in [How to Search](#44-searching-for-recipes) ).

### 4.4 Searching for Recipes

As an addition to this epic, we implemented a more complex query language, supporting proper filtering with the following given specs:

- **String Handling**:
  - Quotation marks (") can be used to treat the entire enclosed string as a single argument
    - Without quotes, space-separated terms are treated as separate arguments.
- **Case Sensitivity**:
  - `case:s`: Case-sensitive search.
  - `case:i`: Case-insensitive search.
  - Default: Inherits the global case sensitivity setting.
  - For each token of type `<string>`, a local modifier of case sensitivity can be specified, overriding the global one:
    - `<field>:~ "Test"` will be **case insensitive**
    - `<field>:= "Test"` will be **case sensitive**
- **Default Behavior**:
  - If no field is specified, the search defaults to the title field.
- **Logical Operators**:
  - **AND**: By default, multiple filters (e.g., `title:"apple" step:"bake"`) are combined with an AND operation.
  - **OR**: Use `|` to combine filters with an OR operation (e.g., title:"apple" | step:"bake").
  - Grouping: Parentheses () can be used to group filters and control precedence (e.g., `(title:"apple" | ingr:"sugar") step:"bake"`).
- **Search Fields**:
  1. **title**:
     - `<field>:[~ | =] <string>`
     - Searches within the title of the item.
  2. **ingr**:
     - `<field>:[~ | =] <string>`
     - Searches within the ingredients.
  3. **step**:
     - `<field>:[~ | =] <string>`
     - Searches within the steps.
  4. **lang**:
     - `<field>:[~ | =] <string>`
     - Adds a language filter to the query, in addition to the default language.
  5. **favourite**:
     - `toggle`, checks just whether it is present in the query as a separate token
     - Filters for items marked as favorites.
  6. **servings**:
     - `<field>: <integer>`
     - Scales up the following requirements
  7. **kcal**:
     - `<field> [< | = | >] <integer>` (i.e. `kcal: 100`, `kcal <: 100`, ...)
     - Filters recipes for TOTAL calories, modified by `servings`
  8. **fat**:
     - `<field> [< | = | >] <integer>` (i.e. `fat: 100`, `fat<: 100`, ...)
     - Filters recipes for TOTAL fat, modified by `servings`
  9. **protein**:
     - `<field> [< | = | >] <integer>` (i.e. `protein: 100`, `protein<: 100`, ...)
     - Filters recipes for TOTAL protein, modified by `servings`
  10. **carbs**:
      - `<field> [< | = | >] <integer>` (i.e. `carbs: 100`, `carbs<: 100`, ...)
      - Filters recipes for TOTAL carbs, modified by `servings`
  11. **sugar**:
      - `<field> [< | = | >] <integer>` (i.e. `sugar: 100`, `sugar<: 100`, ...)
      - Filters recipes for TOTAL sugar, modified by `servings`
  12. **sodium**:
      - `<field> [< | = | >] <integer>` (i.e. `sodium: 100`, `sodium<: 100`, ...)
      - Filters recipes for TOTAL sodium, modified by `servings`

### 4.5 Shopping List

We have extended this epic by implementing checkboxes, which allow users to mark items on their shopping list or in the overview of ingredients which are going to be added from a recipe. This could help, for example, in marking them as bought when a person is at the supermarket.
Ingredients which are marked in the overview will still be marked in the shopping list. Furthermore, as an addition to the user stories, the shopping list gets persisted across restarts of the app. So if a user closes the app and then opens it again, it will still find those ingredients in the shopping list.

### 4.6 Live Language Switch

As an adition to this epic, we implemented support for a 4th language, leading to our app supporting full localisation in English, Dutch, Romanian and French.

### General

Right clicking a recipe in the main scene shows a contextual menu for easy access.
