package group6.java.model;

import java.util.List;

public class SearchResult {
    private String searchTerm;
    private String searchType; // "word", "lemma", or "pos"
    private List<LinguisticToken> matches;
    private int totalMatches;

    public SearchResult(String searchTerm, String searchType, List<LinguisticToken> matches) {
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.matches = matches;
         this.totalMatches = matches.size();
    }


    public String getSearchTerm() { return searchTerm; }
    public String getSearchType() { return searchType; }
    public List<LinguisticToken> getMatches() { return matches; }
    public int getTotalMatches() { return totalMatches; }

    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public void setSearchType(String searchType) { this.searchType = searchType; }
    public void setMatches(List<LinguisticToken> matches) {
        this.matches = matches;
        this.totalMatches = matches.size();
    }

    @Override
    public String toString() {
        return String.format("SearchResult{term='%s', type='%s', matches=%d}",
                     searchTerm, searchType, totalMatches);
    }
}