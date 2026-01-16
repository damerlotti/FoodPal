package client.utils.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchQuery {
    public enum LogicalOperator {
        AND,
        OR
    }

    public enum NodeType {
        TOKEN,
        GROUP
    }

    private final NodeType nodeType;
    private final SearchToken token;
    private final LogicalOperator operator;
    private final List<SearchQuery> children;
    private final CaseSensitivity globalCaseSensitivity;
    private final boolean favouritesOnly;
    private final List<String> additionalLanguages;

    /**
     * Private constructor for building queries.
     */
    private SearchQuery(NodeType nodeType, SearchToken token, LogicalOperator operator,
                        List<SearchQuery> children, CaseSensitivity globalCaseSensitivity,
                        boolean favouritesOnly, List<String> additionalLanguages) {
        this.nodeType = nodeType;
        this.token = token;
        this.operator = operator;
        if (children != null) {
            this.children = new ArrayList<>(children);
        } else {
            this.children = new ArrayList<>();
        }
        this.globalCaseSensitivity = globalCaseSensitivity;
        this.favouritesOnly = favouritesOnly;
        if (additionalLanguages != null) {
            this.additionalLanguages = new ArrayList<>(additionalLanguages);
        } else {
            this.additionalLanguages = new ArrayList<>();
        }
    }

    /**
     * Creates an empty query that matches everything.
     * @return an empty search query
     */
    public static SearchQuery empty() {
        return new SearchQuery(NodeType.GROUP, null, LogicalOperator.AND,
                new ArrayList<>(), CaseSensitivity.INSENSITIVE, false, new ArrayList<>());
    }

    /**
     * Creates a token query (leaf node).
     * @param token the search token
     * @return a new token query
     */
    public static SearchQuery token(SearchToken token) {
        return new SearchQuery(NodeType.TOKEN, token, null,
                null, CaseSensitivity.INSENSITIVE, false, new ArrayList<>());
    }

    /**
     * Creates a group query with the specified operator.
     * @param operator the logical operator (AND/OR)
     * @param children the child queries
     * @return a new group query
     */
    public static SearchQuery group(LogicalOperator operator, List<SearchQuery> children) {
        return new SearchQuery(NodeType.GROUP, null, operator,
                children, CaseSensitivity.INSENSITIVE, false, new ArrayList<>());
    }

    /**
     * Creates a builder for constructing complex queries.
     * @return a new query builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the node type of this query.
     *
     * @return the node type
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Returns the search token if this is a TOKEN node.
     * @return the search token, or null for GROUP nodes
     */
    public SearchToken getToken() {
        return token;
    }

    /**
     * Returns the logical operator if this is a GROUP node.
     * @return the logical operator, or null for TOKEN nodes
     */
    public LogicalOperator getOperator() {
        return operator;
    }

    /**
     * Returns the child queries if this is a GROUP node.
     * @return the list of child queries (empty for TOKEN nodes)
     */
    public List<SearchQuery> getChildren() {
        return new ArrayList<>(children);
    }

    /**
     * Returns the global case sensitivity setting.
     * @return the global case sensitivity
     */
    public CaseSensitivity getGlobalCaseSensitivity() {
        return globalCaseSensitivity;
    }

    /**
     * Returns whether to filter by favourites only.
     * @return true if only favourites should be searched
     */
    public boolean isFavouritesOnly() {
        return favouritesOnly;
    }

    /**
     * Returns the list of additional languages to include in the search.
     * @return the list of additional language tags
     */
    public List<String> getAdditionalLanguages() {
        return new ArrayList<>(additionalLanguages);
    }

    /**
     * Checks if this query is empty (matches everything).
     * @return true if the query has no conditions
     */
    public boolean isEmpty() {
        if (nodeType == NodeType.TOKEN) {
            return token == null;
        }
        return children.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchQuery that = (SearchQuery) o;
        return favouritesOnly == that.favouritesOnly
                && nodeType == that.nodeType
                && Objects.equals(token, that.token)
                && operator == that.operator
                && Objects.equals(children, that.children)
                && globalCaseSensitivity == that.globalCaseSensitivity
                && Objects.equals(additionalLanguages, that.additionalLanguages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeType, token, operator, children,
                globalCaseSensitivity, favouritesOnly, additionalLanguages);
    }

    @Override
    public String toString() {
        if (nodeType == NodeType.TOKEN) {
            return "Query{" + token + "}";
        }
        return "Query{" + operator + ", children=" + children
                + ", globalCase=" + globalCaseSensitivity
                + ", favOnly=" + favouritesOnly
                + ", langs=" + additionalLanguages + "}";
    }

    /**
     * Builder for constructing SearchQuery objects.
     */
    public static class Builder {
        private NodeType nodeType = NodeType.GROUP;
        private SearchToken token;
        private LogicalOperator operator = LogicalOperator.AND;
        private List<SearchQuery> children = new ArrayList<>();
        private CaseSensitivity globalCaseSensitivity = CaseSensitivity.INSENSITIVE;
        private boolean favouritesOnly = false;
        private List<String> additionalLanguages = new ArrayList<>();

        /**
         * Sets this to a token node.
         * @param token the search token
         * @return this builder
         */
        public Builder token(SearchToken token) {
            this.nodeType = NodeType.TOKEN;
            this.token = token;
            return this;
        }

        /**
         * Sets the logical operator for group nodes.
         * @param operator the logical operator
         * @return this builder
         */
        public Builder operator(LogicalOperator operator) {
            this.operator = operator;
            return this;
        }

        /**
         * Adds a child query.
         * @param child the child query to add
         * @return this builder
         */
        public Builder addChild(SearchQuery child) {
            this.nodeType = NodeType.GROUP;
            this.children.add(child);
            return this;
        }

        /**
         * Sets all child queries.
         * @param children the list of child queries
         * @return this builder
         */
        public Builder children(List<SearchQuery> children) {
            this.nodeType = NodeType.GROUP;
            this.children = new ArrayList<>(children);
            return this;
        }

        /**
         * Sets the global case sensitivity.
         * @param caseSensitivity the global case sensitivity
         * @return this builder
         */
        public Builder globalCaseSensitivity(CaseSensitivity caseSensitivity) {
            this.globalCaseSensitivity = caseSensitivity;
            return this;
        }

        /**
         * Sets whether to filter by favourites only.
         * @param favouritesOnly true to filter by favourites
         * @return this builder
         */
        public Builder favouritesOnly(boolean favouritesOnly) {
            this.favouritesOnly = favouritesOnly;
            return this;
        }

        /**
         * Adds an additional language to include in the search.
         * @param languageTag the language tag to add
         * @return this builder
         */
        public Builder addLanguage(String languageTag) {
            this.additionalLanguages.add(languageTag);
            return this;
        }

        /**
         * Sets all additional languages.
         * @param languages the list of language tags
         * @return this builder
         */
        public Builder additionalLanguages(List<String> languages) {
            this.additionalLanguages = new ArrayList<>(languages);
            return this;
        }

        /**
         * Builds the SearchQuery.
         * @return the constructed SearchQuery
         */
        public SearchQuery build() {
            return new SearchQuery(nodeType, token, operator, children,
                    globalCaseSensitivity, favouritesOnly, additionalLanguages);
        }
    }
}
