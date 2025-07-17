package ui;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Holds the results of a search.
 * Keeps track of the query, what kind of search it was,
 * the matched items, and when the search happened.
 */
public class SearchResult {

    // What was searched for, like "lemma = gehen"
    private String query;

    // The list of matches found for the query
    private List<String> matches;

    // The search type, e.g. "word", "lemma", or "POS"
    private String searchType;

    // When this search was made
    private LocalDateTime timestamp;

    /**
     * Creates a SearchResult and records the current time automatically.
     *
     * @param query what was searched
     * @param matches the matching items found
     * @param searchType type of search done
     */
    public SearchResult(String query, List<String> matches, String searchType) {
        this.query = query;
        this.matches = matches;
        this.searchType = searchType;
        this.timestamp = LocalDateTime.now(); // timestamp set now
    }

    public String getQuery() {
        return query;
    }

    public List<String> getMatches() {
        return matches;
    }

    public String getSearchType() {
        return searchType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
