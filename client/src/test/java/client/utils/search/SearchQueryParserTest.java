package client.utils.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
