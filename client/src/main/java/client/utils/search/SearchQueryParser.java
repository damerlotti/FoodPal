package client.utils.search;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryParser {
    private String input;
    private int pos;
    private CaseSensitivity globalCaseSensitivity;
    private boolean favouritesOnly;
    private List<String> additionalLanguages;

    /**
     * Parses a search query string into a SearchQuery object.
     * @param query the query string to parse
     * @return the parsed SearchQuery
     * @throws SearchParseException if the query is invalid
     */
    public SearchQuery parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            return SearchQuery.empty();
        }

        this.input = query;
        this.pos = 0;
        this.globalCaseSensitivity = CaseSensitivity.INSENSITIVE;
        this.favouritesOnly = false;
        this.additionalLanguages = new ArrayList<>();

        // First pass: extract global settings (case: and fav)
        extractGlobalSettings();

        // Reset position for main parsing
        this.pos = 0;

        // Parse the expression
        SearchQuery result = parseExpression();

        // Apply global settings
        SearchQuery.LogicalOperator op = result.getOperator();
        if (op == null) {
            op = SearchQuery.LogicalOperator.AND;
        }

        List<SearchQuery> resultChildren;
        if (result.getNodeType() == SearchQuery.NodeType.GROUP) {
            resultChildren = result.getChildren();
        } else {
            resultChildren = List.of(result);
        }

        return SearchQuery.builder()
                .operator(op)
                .children(resultChildren)
                .globalCaseSensitivity(globalCaseSensitivity)
                .favouritesOnly(favouritesOnly)
                .additionalLanguages(additionalLanguages)
                .build();
    }

    /**
     * Extracts global settings (case: and fav) from the query.
     * These are processed first and removed from further parsing.
     */
    private void extractGlobalSettings() {
        int tempPos = 0;
        while (tempPos < input.length()) {
            while (tempPos < input.length() && Character.isWhitespace(input.charAt(tempPos))) {
                tempPos++;
            }
            if (tempPos >= input.length()) {
                break;
            }

            if (matchesAt(tempPos, "case:")) {
                int valueStart = tempPos + 5;
                while (valueStart < input.length()
                        && Character.isWhitespace(input.charAt(valueStart))) {
                    valueStart++;
                }
                if (valueStart < input.length()) {
                    char c = input.charAt(valueStart);
                    if (c == 's' || c == 'S') {
                        globalCaseSensitivity = CaseSensitivity.SENSITIVE;
                    } else if (c == 'i' || c == 'I') {
                        globalCaseSensitivity = CaseSensitivity.INSENSITIVE;
                    }
                }
            }

            if (matchesWordAt(tempPos, "fav")) {
                favouritesOnly = true;
            }

            tempPos++;
        }
    }

    private boolean matchesAt(int position, String str) {
        if (position + str.length() > input.length()) {
            return false;
        }
        return input.substring(position, position + str.length()).equalsIgnoreCase(str);
    }

    private boolean matchesWordAt(int position, String word) {
        if (!matchesAt(position, word)) {
            return false;
        }
        int endPos = position + word.length();
        if (endPos >= input.length()) {
            return true;
        }
        char nextChar = input.charAt(endPos);
        return !Character.isLetterOrDigit(nextChar) && nextChar != ':';
    }

    /**
     * Parses an expression (handles OR at the top level).
     */
    private SearchQuery parseExpression() {
        List<SearchQuery> orTerms = new ArrayList<>();
        orTerms.add(parseAndExpression());

        while (true) {
            skipWhitespace();
            if (pos >= input.length()) {
                break;
            }

            if (peek() == '|') {
                consume();
                skipWhitespace();
                orTerms.add(parseAndExpression());
            } else {
                break;
            }
        }

        if (orTerms.size() == 1) {
            return orTerms.get(0);
        }
        return SearchQuery.group(SearchQuery.LogicalOperator.OR, orTerms);
    }

    /**
     * Parses an AND expression (implicit AND between terms).
     */
    private SearchQuery parseAndExpression() {
        List<SearchQuery> andTerms = new ArrayList<>();

        while (true) {
            skipWhitespace();
            if (pos >= input.length()) {
                break;
            }

            char c = peek();
            if (c == ')' || c == '|') {
                break;
            }

            SearchQuery term = parseTerm();
            if (term != null && !term.isEmpty()) {
                andTerms.add(term);
            }
        }

        if (andTerms.isEmpty()) {
            return SearchQuery.empty();
        }
        if (andTerms.size() == 1) {
            return andTerms.get(0);
        }
        return SearchQuery.group(SearchQuery.LogicalOperator.AND, andTerms);
    }

    /**
     * Parses a single term (token, group, or global setting).
     */
    private SearchQuery parseTerm() {
        skipWhitespace();
        if (pos >= input.length()) {
            return null;
        }

        char c = peek();

        if (c == '(') {
            return parseGroup();
        }

        return parseFieldValue();
    }

    /**
     * Parses a parenthesized group.
     */
    private SearchQuery parseGroup() {
        expect('(');
        SearchQuery inner = parseExpression();
        skipWhitespace();
        expect(')');
        return inner;
    }

    /**
     * Parses a field:value pair or a plain value.
     */
    private SearchQuery parseFieldValue() {
        skipWhitespace();
        if (pos >= input.length()) {
            return null;
        }

        // Check for global settings to skip
        if (matchesAt(pos, "case:")) {
            pos += 5;
            skipWhitespace();
            if (pos < input.length()) {
                char c = peek();
                if (c == 's' || c == 'S' || c == 'i' || c == 'I') {
                    pos++;
                }
            }
            return null;
        }

        if (matchesWordAt(pos, "fav")) {
            pos += 3;
            return null;
        }

        // Try to match a field specifier
        SearchField field = null;
        CaseSensitivity localCase = CaseSensitivity.INHERIT;

        // Check for field prefixes
        if (matchesAt(pos, "title:")) {
            field = SearchField.TITLE;
            pos += 6;
            localCase = parseLocalCaseModifier();
        } else if (matchesAt(pos, "ingr:")) {
            field = SearchField.INGREDIENTS;
            pos += 5;
            localCase = parseLocalCaseModifier();
        } else if (matchesAt(pos, "step:")) {
            field = SearchField.STEPS;
            pos += 5;
            localCase = parseLocalCaseModifier();
        } else if (matchesAt(pos, "lang:")) {
            field = SearchField.LANGUAGE;
            pos += 5;
            parseLocalCaseModifier();
            skipWhitespace();
            String langValue = parseValue();
            if (langValue != null && !langValue.isEmpty()) {
                additionalLanguages.add(langValue);
            }
            return null;
        }

        // If no field specified, default to TITLE
        if (field == null) {
            field = SearchField.TITLE;
        }

        skipWhitespace();

        // Parse the value
        String value = parseValue();
        if (value == null || value.isEmpty()) {
            return null;
        }

        SearchToken token = new SearchToken(field, value, localCase);
        return SearchQuery.token(token);
    }

    /**
     * Parses local case modifier (:= or :~).
     */
    private CaseSensitivity parseLocalCaseModifier() {
        if (pos >= input.length()) {
            return CaseSensitivity.INHERIT;
        }

        char c = peek();
        if (c == '=') {
            pos++;
            return CaseSensitivity.SENSITIVE;
        } else if (c == '~') {
            pos++;
            return CaseSensitivity.INSENSITIVE;
        }
        return CaseSensitivity.INHERIT;
    }

    /**
     * Parses a value (quoted string or single word).
     */
    private String parseValue() {
        skipWhitespace();
        if (pos >= input.length()) {
            return null;
        }

        char c = peek();

        // Quoted string
        if (c == '"') {
            return parseQuotedString();
        }

        // Unquoted word (until whitespace or special char)
        return parseWord();
    }

    /**
     * Parses a double-quoted string.
     */
    private String parseQuotedString() {
        expect('"');
        StringBuilder sb = new StringBuilder();

        while (pos < input.length()) {
            char c = peek();
            if (c == '"') {
                pos++;
                break;
            }
            // Handle escape sequences
            if (c == '\\' && pos + 1 < input.length()) {
                pos++;
                char escaped = peek();
                if (escaped == '"' || escaped == '\\') {
                    sb.append(escaped);
                    pos++;
                } else {
                    sb.append('\\').append(escaped);
                    pos++;
                }
            } else {
                sb.append(c);
                pos++;
            }
        }

        return sb.toString();
    }

    /**
     * Parses an unquoted word.
     */
    private String parseWord() {
        StringBuilder sb = new StringBuilder();

        while (pos < input.length()) {
            char c = peek();
            // Stop at whitespace or special characters
            if (Character.isWhitespace(c) || c == '|' || c == '(' || c == ')' || c == '"') {
                break;
            }
            // Also stop if we hit a new field specifier
            if (isFieldSpecifierAhead()) {
                break;
            }
            sb.append(c);
            pos++;
        }

        return sb.toString();
    }

    /**
     * Checks if a field specifier is ahead (to properly separate terms).
     */
    private boolean isFieldSpecifierAhead() {
        int lookAhead = pos;
        StringBuilder word = new StringBuilder();

        while (lookAhead < input.length()) {
            char c = input.charAt(lookAhead);
            if (c == ':') {
                String prefix = word.toString().toLowerCase();
                return prefix.equals("title") || prefix.equals("ingr")
                        || prefix.equals("step") || prefix.equals("lang")
                        || prefix.equals("case");
            }
            if (!Character.isLetter(c)) {
                break;
            }
            word.append(c);
            lookAhead++;
        }
        return false;
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private char peek() {
        return input.charAt(pos);
    }

    private char consume() {
        return input.charAt(pos++);
    }

    private void expect(char expected) {
        if (pos >= input.length() || peek() != expected) {
            throw new SearchParseException(
                    "Expected '" + expected + "'",
                    input,
                    pos
            );
        }
        pos++;
    }
}
