package client.utils.search;

public class SearchParseException extends RuntimeException {
    private final int position;
    private final String query;

    /**
     * Creates a new parse exception.
     * @param message  the error message
     * @param query    the original query string
     * @param position the position where the error occurred
     */
    public SearchParseException(String message, String query, int position) {
        super(message + " at position " + position + " in query: " + query);
        this.position = position;
        this.query = query;
    }

    /**
     * Creates a new parse exception without position info.
     * @param message the error message
     */
    public SearchParseException(String message) {
        super(message);
        this.position = -1;
        this.query = null;
    }

    /**
     * Returns the position where the error occurred.
     * @return the error position, or -1 if not available
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the original query string.
     * @return the query string, or null if not available
     */
    public String getQuery() {
        return query;
    }
}
