package client.utils.search;

import commons.Recipe;

import java.util.List;
import java.util.Set;

public class RecipeSearchService {
    private final SearchQueryParser parser;
    private final RecipeSearchMatcher matcher;

    /**
     * Creates a new search service with the specified settings.
     * @param defaultLanguageTag the default language tag
     * @param favouriteRecipeIds the set of favourite recipe IDs
     */
    public RecipeSearchService(String defaultLanguageTag, Set<Long> favouriteRecipeIds) {
        this.parser = new SearchQueryParser();
        this.matcher = new RecipeSearchMatcher(defaultLanguageTag, favouriteRecipeIds);
    }

    /**
     * Creates a new search service with default settings.
     */
    public RecipeSearchService() {
        this("en", Set.of());
    }

    /**
     * Searches recipes using the specified query string.
     * @param recipes     the list of recipes to search
     * @param queryString the search query string
     * @return a list of matching recipes
     * @throws SearchParseException if the query is invalid
     */
    public List<Recipe> search(List<Recipe> recipes, String queryString) {
        SearchQuery query = parser.parse(queryString);
        return matcher.filter(recipes, query);
    }

    /**
     * Checks if a single recipe matches the query string.
     * @param recipe      the recipe to check
     * @param queryString the search query string
     * @return true if the recipe matches
     * @throws SearchParseException if the query is invalid
     */
    public boolean matches(Recipe recipe, String queryString) {
        SearchQuery query = parser.parse(queryString);
        return matcher.matches(recipe, query);
    }

    /**
     * Parses a query string without evaluating it.
     * Useful for validating queries or debugging.
     * @param queryString the query string to parse
     * @return the parsed query
     * @throws SearchParseException if the query is invalid
     */
    public SearchQuery parseQuery(String queryString) {
        return parser.parse(queryString);
    }

    /**
     * Returns the underlying parser.
     * @return the search query parser
     */
    public SearchQueryParser getParser() {
        return parser;
    }

    /**
     * Returns the underlying matcher.
     * @return the recipe search matcher
     */
    public RecipeSearchMatcher getMatcher() {
        return matcher;
    }
}
