package client.utils.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchQueryParserTest {

    private SearchQueryParser parser;

    @BeforeEach
    void setUp() {
        parser = new SearchQueryParser();
    }

    @Test
    void parseEmptyInput() {
        assertTrue(parser.parse(null).isEmpty());
        assertTrue(parser.parse("").isEmpty());
        assertTrue(parser.parse("   ").isEmpty());
    }

    @Test
    void parseSimpleTerm() {
        SearchQuery query = parser.parse("pasta");

        assertEquals(1, query.getChildren().size());
        SearchToken token = query.getChildren().get(0).getToken();
        assertEquals(SearchField.TITLE, token.getField());
        assertEquals("pasta", token.getValue());
    }

    @Test
    void parseMultipleTerms() {
        SearchQuery query = parser.parse("pasta salad");

        assertEquals(SearchQuery.LogicalOperator.AND, query.getOperator());
        assertEquals(2, query.getChildren().size());
        assertEquals("pasta", query.getChildren().get(0).getToken().getValue());
        assertEquals("salad", query.getChildren().get(1).getToken().getValue());
    }

    @Test
    void parseQuotedString() {
        SearchQuery query = parser.parse("\"Italian Pasta Salad\"");

        assertEquals(1, query.getChildren().size());
        assertEquals("Italian Pasta Salad", query.getChildren().get(0).getToken().getValue());
    }

    @Test
    void parseFieldSpecifiers() {
        SearchQuery query = parser.parse("title: pasta ingr: cheese step: bake");

        assertEquals(3, query.getChildren().size());
        assertEquals(SearchField.TITLE, query.getChildren().get(0).getToken().getField());
        assertEquals(SearchField.INGREDIENTS, query.getChildren().get(1).getToken().getField());
        assertEquals(SearchField.STEPS, query.getChildren().get(2).getToken().getField());
    }

    @Test
    void parseOrOperator() {
        SearchQuery query = parser.parse("pasta | pizza");

        assertEquals(SearchQuery.LogicalOperator.OR, query.getOperator());
        assertEquals(2, query.getChildren().size());
    }

    @Test
    void parseParentheses() {
        SearchQuery query = parser.parse("(pasta | pizza) cheese");

        assertEquals(SearchQuery.LogicalOperator.AND, query.getOperator());
        assertEquals(2, query.getChildren().size());

        SearchQuery group = query.getChildren().get(0);
        assertEquals(SearchQuery.NodeType.GROUP, group.getNodeType());
        assertEquals(SearchQuery.LogicalOperator.OR, group.getOperator());
    }

    @Test
    void parseGlobalCaseSensitivity() {
        SearchQuery sensitive = parser.parse("case: s pasta");
        SearchQuery insensitive = parser.parse("case: i pasta");

        assertEquals(CaseSensitivity.SENSITIVE, sensitive.getGlobalCaseSensitivity());
        assertEquals(CaseSensitivity.INSENSITIVE, insensitive.getGlobalCaseSensitivity());
    }

    @Test
    void parseLocalCaseModifiers() {
        SearchQuery query = parser.parse("title:= Pasta title:~ noodles title: rice");

        assertEquals(3, query.getChildren().size());
        assertEquals(CaseSensitivity.SENSITIVE,
                query.getChildren().get(0).getToken().getCaseSensitivity());
        assertEquals(CaseSensitivity.INSENSITIVE,
                query.getChildren().get(1).getToken().getCaseSensitivity());
        assertEquals(CaseSensitivity.INHERIT,
                query.getChildren().get(2).getToken().getCaseSensitivity());
    }

    @Test
    void parseFavouritesFilter() {
        assertTrue(parser.parse("fav pasta").isFavouritesOnly());
        assertTrue(parser.parse("pasta fav").isFavouritesOnly());
        assertFalse(parser.parse("pasta").isFavouritesOnly());
        assertFalse(parser.parse("favourite").isFavouritesOnly());
    }

    @Test
    void parseLanguageFilter() {
        SearchQuery query = parser.parse("lang: en pasta");

        assertTrue(query.getAdditionalLanguages().contains("en"));
        assertEquals(1, query.getChildren().size());
    }

    @Test
    void parseComplexQuery() {
        SearchQuery query = parser.parse("fav case: s (title: Pasta | ingr: Cheese) step: bake");

        assertTrue(query.isFavouritesOnly());
        assertEquals(CaseSensitivity.SENSITIVE, query.getGlobalCaseSensitivity());
        assertEquals(SearchQuery.LogicalOperator.AND, query.getOperator());
        assertEquals(2, query.getChildren().size());
    }

    @Test
    void parseKcalFilterLessThan() {
        SearchQuery query = parser.parse("kcal<: 500 pasta");

        List<NutritionFilter> filters = query.getNutritionFilters();
        assertEquals(1, filters.size());
        assertEquals(NutritionField.KCAL, filters.get(0).getField());
        assertEquals(ComparisonOperator.LESS_THAN, filters.get(0).getOperator());
        assertEquals(500, filters.get(0).getValue());
        assertEquals(1, query.getChildren().size());
    }

    @Test
    void parseKcalFilterGreaterThan() {
        SearchQuery query = parser.parse("kcal>: 200");

        List<NutritionFilter> filters = query.getNutritionFilters();
        assertEquals(1, filters.size());
        assertEquals(NutritionField.KCAL, filters.get(0).getField());
        assertEquals(ComparisonOperator.GREATER_THAN, filters.get(0).getOperator());
        assertEquals(200, filters.get(0).getValue());
    }

    @Test
    void parseKcalFilterEqual() {
        SearchQuery query = parser.parse("kcal=: 300");

        List<NutritionFilter> filters = query.getNutritionFilters();
        assertEquals(1, filters.size());
        assertEquals(ComparisonOperator.EQUAL, filters.get(0).getOperator());
        assertEquals(300, filters.get(0).getValue());
    }

    @Test
    void parseKcalFilterDefaultEqual() {
        SearchQuery query = parser.parse("kcal: 300");

        List<NutritionFilter> filters = query.getNutritionFilters();
        assertEquals(1, filters.size());
        assertEquals(ComparisonOperator.EQUAL, filters.get(0).getOperator());
        assertEquals(300, filters.get(0).getValue());
    }

    @Test
    void parseMultipleNutritionFilters() {
        SearchQuery query = parser.parse("kcal<: 500 fat<: 20 protein>: 30");

        List<NutritionFilter> filters = query.getNutritionFilters();
        assertEquals(3, filters.size());

        assertEquals(NutritionField.KCAL, filters.get(0).getField());
        assertEquals(NutritionField.FAT, filters.get(1).getField());
        assertEquals(NutritionField.PROTEIN, filters.get(2).getField());
    }

    @Test
    void parseAllNutritionFieldTypes() {
        SearchQuery query = parser.parse(
                "kcal<: 100 fat<: 10 protein<: 20 carbs<: 30 sugar<: 5 sodium<: 500");

        List<NutritionFilter> filters = query.getNutritionFilters();
        assertEquals(6, filters.size());

        assertEquals(NutritionField.KCAL, filters.get(0).getField());
        assertEquals(NutritionField.FAT, filters.get(1).getField());
        assertEquals(NutritionField.PROTEIN, filters.get(2).getField());
        assertEquals(NutritionField.CARBS, filters.get(3).getField());
        assertEquals(NutritionField.SUGAR, filters.get(4).getField());
        assertEquals(NutritionField.SODIUM, filters.get(5).getField());
    }

    @Test
    void parseServingsFilter() {
        SearchQuery query = parser.parse("servings: 4 kcal<: 500");

        assertTrue(query.getServings().isPresent());
        assertEquals(4, query.getServings().get());
        assertEquals(1, query.getNutritionFilters().size());
    }

    @Test
    void parseNutritionWithTextSearch() {
        SearchQuery query = parser.parse("kcal<: 500 pasta ingr: cheese");

        assertEquals(1, query.getNutritionFilters().size());
        assertEquals(2, query.getChildren().size());
    }
}
