package client.utils.search;

import java.util.Objects;

public class SearchToken {
    private final SearchField field;
    private final String value;
    private final CaseSensitivity caseSensitivity;

    /**
     * Creates a new search token.
     * @param field           The field to search in
     * @param value           The value to search for
     * @param caseSensitivity The case sensitivity mode for this token
     */
    public SearchToken(SearchField field, String value, CaseSensitivity caseSensitivity) {
        this.field = field;
        this.value = value;
        this.caseSensitivity = caseSensitivity;
    }

    /**
     * Creates a new search token with inherited case sensitivity.
     * @param field The field to search in
     * @param value The value to search for
     */
    public SearchToken(SearchField field, String value) {
        this(field, value, CaseSensitivity.INHERIT);
    }

    /**
     * Returns the field to search in.
     * @return the search field
     */
    public SearchField getField() {
        return field;
    }

    /**
     * Returns the value to search for.
     * @return the search value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the case sensitivity mode for this token.
     * @return the case sensitivity mode
     */
    public CaseSensitivity getCaseSensitivity() {
        return caseSensitivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchToken that = (SearchToken) o;
        return field == that.field
                && Objects.equals(value, that.value)
                && caseSensitivity == that.caseSensitivity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value, caseSensitivity);
    }

    @Override
    public String toString() {
        return "SearchToken{"
                + "field=" + field
                + ", value='" + value + '\''
                + ", caseSensitivity=" + caseSensitivity
                + '}';
    }
}
