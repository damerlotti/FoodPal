package client.utils.search;

import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeStep;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RecipeSearchMatcher {
    private final String defaultLanguageTag;
    private final Set<Long> favouriteRecipeIds;

    /**
     * Creates a new matcher with the specified default language.
     * @param defaultLanguageTag  the default language tag to match against
     * @param favouriteRecipeIds  the set of favourite recipe IDs
     */
    public RecipeSearchMatcher(String defaultLanguageTag, Set<Long> favouriteRecipeIds) {
        if (defaultLanguageTag != null) {
            this.defaultLanguageTag = defaultLanguageTag;
        } else {
            this.defaultLanguageTag = "en";
        }
        if (favouriteRecipeIds != null) {
            this.favouriteRecipeIds = favouriteRecipeIds;
        } else {
            this.favouriteRecipeIds = Set.of();
        }
    }

    /**
     * Creates a new matcher with default settings.
     */
    public RecipeSearchMatcher() {
        this("en", Set.of());
    }

    /**
     * Checks if a recipe matches the given search query.
     * @param recipe the recipe to check
     * @param query  the search query
     * @return true if the recipe matches the query
     */
    public boolean matches(Recipe recipe, SearchQuery query) {
        if (recipe == null) {
            return false;
        }
        if (query == null) {
            return true;
        }

        // Check favourites filter first
        if (query.isFavouritesOnly() && !favouriteRecipeIds.contains(recipe.getId())) {
            return false;
        }

        // Check language filter
        if (!matchesLanguage(recipe, query)) {
            return false;
        }

        // Check nutrition filters
        if (!matchesNutritionFilters(recipe, query)) {
            return false;
        }

        // If query has no search conditions, it matches (after passing filters above)
        if (query.isEmpty()) {
            return true;
        }

        return evaluateQuery(recipe, query, query.getGlobalCaseSensitivity());
    }

    /**
     * Creates a predicate for filtering recipes based on the query.
     * @param query the search query
     * @return a predicate that returns true for matching recipes
     */
    public Predicate<Recipe> createPredicate(SearchQuery query) {
        return recipe -> matches(recipe, query);
    }

    /**
     * Filters a list of recipes based on the query.
     * @param recipes the list of recipes to filter
     * @param query   the search query
     * @return a new list containing only matching recipes
     */
    public List<Recipe> filter(List<Recipe> recipes, SearchQuery query) {
        if (recipes == null) {
            return List.of();
        }
        if (query == null) {
            return recipes;
        }

        return recipes.stream()
                .filter(createPredicate(query))
                .toList();
    }

    /**
     * Checks if a recipe matches the language requirements.
     */
    private boolean matchesLanguage(Recipe recipe, SearchQuery query) {
        String recipeLanguage = recipe.getLanguageTag();
        if (recipeLanguage == null) {
            recipeLanguage = "en";
        }

        // Normalize language tags for comparison
        String normalizedRecipeLang = normalizeLanguageTag(recipeLanguage);
        String normalizedDefaultLang = normalizeLanguageTag(defaultLanguageTag);

        // Always include recipes matching the default language
        if (normalizedRecipeLang.equals(normalizedDefaultLang)) {
            return true;
        }

        // Check additional languages from the query
        for (String additionalLang : query.getAdditionalLanguages()) {
            String normalizedAdditional = normalizeLanguageTag(additionalLang);
            if (normalizedRecipeLang.equals(normalizedAdditional)
                    || normalizedRecipeLang.startsWith(normalizedAdditional + "-")
                    || normalizedAdditional.startsWith(normalizedRecipeLang + "-")) {
                return true;
            }
        }

        // If no additional languages specified, also match default
        return query.getAdditionalLanguages().isEmpty();
    }

    /**
     * Checks if a recipe matches all nutrition filters.
     */
    private boolean matchesNutritionFilters(Recipe recipe, SearchQuery query) {
        List<NutritionFilter> filters = query.getNutritionFilters();
        if (filters.isEmpty()) {
            return true;
        }

        // Determine the scaling factor based on servings
        BigDecimal scalingFactor = BigDecimal.ONE;
        if (query.getServings().isPresent() && recipe.getServings() > 0) {
            int queryServings = query.getServings().get();
            scalingFactor = BigDecimal.valueOf(queryServings)
                    .divide(BigDecimal.valueOf(recipe.getServings()), 10, RoundingMode.HALF_UP);
        }

        for (NutritionFilter filter : filters) {
            BigDecimal rawValue = getNutritionValue(recipe, filter.getField());
            BigDecimal scaledValue = rawValue.multiply(scalingFactor);
            int intValue = scaledValue.setScale(0, RoundingMode.HALF_UP).intValue();

            if (!filter.matches(intValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the total nutrition value for a specific field from a recipe.
     */
    private BigDecimal getNutritionValue(Recipe recipe, NutritionField field) {
        switch (field) {
            case KCAL:
                return recipe.calculateTotalKcal();
            case FAT:
                return recipe.calculateTotalFat();
            case PROTEIN:
                return recipe.calculateTotalProtein();
            case CARBS:
                return recipe.calculateTotalCarbs();
            case SUGAR:
                return recipe.calculateTotalSugar();
            case SODIUM:
                return recipe.calculateTotalSodium();
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Normalizes a language tag for comparison.
     */
    private String normalizeLanguageTag(String tag) {
        if (tag == null) {
            return "en";
        }
        return tag.toLowerCase().replace("_", "-");
    }

    /**
     * Evaluates a query node against a recipe.
     */
    private boolean evaluateQuery(Recipe recipe, SearchQuery query,
                                  CaseSensitivity globalSensitivity) {
        if (query.getNodeType() == SearchQuery.NodeType.TOKEN) {
            return evaluateToken(recipe, query.getToken(), globalSensitivity);
        }

        List<SearchQuery> children = query.getChildren();
        if (children.isEmpty()) {
            return true;
        }

        if (query.getOperator() == SearchQuery.LogicalOperator.AND) {
            return children.stream()
                    .allMatch(child -> evaluateQuery(recipe, child, globalSensitivity));
        } else {
            return children.stream()
                    .anyMatch(child -> evaluateQuery(recipe, child, globalSensitivity));
        }
    }

    /**
     * Evaluates a single search token against a recipe.
     */
    private boolean evaluateToken(Recipe recipe, SearchToken token,
                                  CaseSensitivity globalSensitivity) {
        if (token == null) {
            return true;
        }

        // Determine effective case sensitivity
        CaseSensitivity effectiveSensitivity = token.getCaseSensitivity();
        if (effectiveSensitivity == CaseSensitivity.INHERIT) {
            effectiveSensitivity = globalSensitivity;
        }
        if (effectiveSensitivity == CaseSensitivity.INHERIT) {
            effectiveSensitivity = CaseSensitivity.INSENSITIVE;
        }

        boolean caseSensitive = effectiveSensitivity == CaseSensitivity.SENSITIVE;
        String searchValue = token.getValue();

        switch (token.getField()) {
            case TITLE:
                return matchesString(recipe.getName(), searchValue, caseSensitive);
            case INGREDIENTS:
                return matchesIngredients(recipe, searchValue, caseSensitive);
            case STEPS:
                return matchesSteps(recipe, searchValue, caseSensitive);
            case LANGUAGE:
                return true;
            case FAVOURITE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if a string contains the search value.
     */
    private boolean matchesString(String text, String searchValue, boolean caseSensitive) {
        if (text == null || searchValue == null) {
            return false;
        }

        if (caseSensitive) {
            return text.contains(searchValue);
        } else {
            return text.toLowerCase().contains(searchValue.toLowerCase());
        }
    }

    /**
     * Checks if any ingredient matches the search value.
     */
    private boolean matchesIngredients(Recipe recipe, String searchValue, boolean caseSensitive) {
        if (recipe.getIngredients() == null) {
            return false;
        }

        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (ri.getIngredient() != null) {
                String ingredientName = ri.getIngredient().getName();
                if (matchesString(ingredientName, searchValue, caseSensitive)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if any step matches the search value.
     */
    private boolean matchesSteps(Recipe recipe, String searchValue, boolean caseSensitive) {
        if (recipe.getSteps() == null) {
            return false;
        }

        for (RecipeStep step : recipe.getSteps()) {
            if (matchesString(step.getText(), searchValue, caseSensitive)) {
                return true;
            }
        }
        return false;
    }
}
