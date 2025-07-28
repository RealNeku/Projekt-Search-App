package group6.java.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 + * Represents a search history entry with search details and timestamp
 + */
public class SearchHistoryEntry {
    private String searchTerm;
    private String searchType;
    private LocalDateTime timestamp;
    private int resultCount;
    private String sourceInfo;

    public SearchHistoryEntry(String searchTerm, String searchType, int resultCount, String sourceInfo) {
        this.searchTerm = searchTerm;
        this.searchType = searchType;
        this.resultCount = resultCount;
        this.sourceInfo = sourceInfo;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getSearchTerm() { return searchTerm; }
    public String getSearchType() { return searchType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getResultCount() { return resultCount; }
    public String getSourceInfo() { return sourceInfo; }

    /**
      * Get formatted timestamp for display
      */
    public String getFormattedTimestamp() {
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
           return timestamp.format(formatter);
    }

    /**
     * Get display string for the search entry
     */
     public String getDisplayString() {
         return String.format("[%s] %s by %s (%d results)",
            getFormattedTimestamp(), searchTerm, searchType, resultCount);
     }

     /**
      * Get short display string for compact view
      */
     public String getShortDisplayString() {
           return String.format("%s (%s): %d", searchTerm, searchType, resultCount);
     }

     @Override
     public String toString() {
         return getDisplayString();
     }

     @Override
     public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchHistoryEntry that = (SearchHistoryEntry) obj;
        return searchTerm.equals(that.searchTerm) &&
                searchType.equals(that.searchType) &&
                sourceInfo.equals(that.sourceInfo);
     }

     @Override
     public int hashCode() {
        return searchTerm.hashCode() + searchType.hashCode() + sourceInfo.hashCode();
     }
}
