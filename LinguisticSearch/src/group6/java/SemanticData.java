package group6.java;

/**
 * Holds semantic annotation data for a given sentence.
 * Could include named entity tags, semantic roles, or other semantic info aligned with tokens.
 */
public class SemanticData {
    public final String[] namedEntityTags;   // Named entity tags aligned to tokens (e.g., PERSON, LOCATION, O)
    public final String[] semanticRoles;     // Semantic roles aligned to tokens (optional, can be null or empty)

    // Constructor with both named entity tags and semantic roles
    public SemanticData(String[] namedEntityTags, String[] semanticRoles) {
        this.namedEntityTags = namedEntityTags;
        this.semanticRoles = semanticRoles;
    }

    // Constructor with just named entity tags (semanticRoles default to empty array)
    public SemanticData(String[] namedEntityTags) {
        this(namedEntityTags, new String[0]);  // Calls the two-argument constructor with empty semanticRoles
    }

    // No-argument constructor initializes empty arrays
    public SemanticData() {
        this.namedEntityTags = new String[0];
        this.semanticRoles = new String[0];
    }
}
