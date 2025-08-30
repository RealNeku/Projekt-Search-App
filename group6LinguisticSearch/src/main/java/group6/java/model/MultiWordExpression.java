package group6.java.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a German multiword expression (MWE) extracted from text
 */
public class MultiWordExpression {
    private List<LinguisticToken> tokens;
    private String expression;
    private String lemmatizedForm;
    private MWEType type;
    private double confidence;
    private String sourceUrl;
    private String context;
    private int startPosition;
    private int endPosition;
    
    public enum MWEType {
        COMPOUND_NOUN,           // Zusammengesetzte Nomen (e.g., "Bundeskanzler")
        PHRASAL_VERB,           // Phrasal verbs (e.g., "aufhören")
        PREPOSITIONAL_PHRASE,   // Prepositional phrases (e.g., "im Gegensatz zu")
        IDIOMATIC_EXPRESSION,   // Idioms (e.g., "ins Gras beißen")
        NAMED_ENTITY,           // Named entities (e.g., "Vereinte Nationen")
        COLLOCATION,            // Collocations (e.g., "schwere Entscheidung")
        LIGHT_VERB_CONSTRUCTION, // Light verb constructions (e.g., "eine Entscheidung treffen")
        OTHER
    }
    
    public MultiWordExpression(List<LinguisticToken> tokens, MWEType type) {
        this.tokens = new ArrayList<>(tokens);
        this.type = type;
        this.expression = buildExpression();
        this.lemmatizedForm = buildLemmatizedForm();
        this.confidence = 0.0;
    }
    
    public MultiWordExpression(String expression, MWEType type) {
        this.expression = expression;
        this.type = type;
        this.tokens = new ArrayList<>();
        this.lemmatizedForm = expression; // Default to expression if no tokens
        this.confidence = 0.0;
    }
    
    private String buildExpression() {
        if (tokens.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(tokens.get(i).getWord());
        }
        return sb.toString();
    }
    
    private String buildLemmatizedForm() {
        if (tokens.isEmpty()) return expression;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(tokens.get(i).getLemma());
        }
        return sb.toString();
    }
    
    // Getters and Setters
    public List<LinguisticToken> getTokens() { return tokens; }
    public String getExpression() { return expression; }
    public String getLemmatizedForm() { return lemmatizedForm; }
    public MWEType getType() { return type; }
    public double getConfidence() { return confidence; }
    public String getSourceUrl() { return sourceUrl; }
    public String getContext() { return context; }
    public int getStartPosition() { return startPosition; }
    public int getEndPosition() { return endPosition; }
    
    public void setTokens(List<LinguisticToken> tokens) { 
        this.tokens = tokens; 
        this.expression = buildExpression();
        this.lemmatizedForm = buildLemmatizedForm();
    }
    public void setExpression(String expression) { this.expression = expression; }
    public void setLemmatizedForm(String lemmatizedForm) { this.lemmatizedForm = lemmatizedForm; }
    public void setType(MWEType type) { this.type = type; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public void setContext(String context) { this.context = context; }
    public void setStartPosition(int startPosition) { this.startPosition = startPosition; }
    public void setEndPosition(int endPosition) { this.endPosition = endPosition; }
    
    public int getLength() {
        return tokens.size();
    }
    
    public boolean isContiguous() {
        if (tokens.size() <= 1) return true;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).getTokenIndex() != tokens.get(i-1).getTokenIndex() + 1) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("MWE{expression='%s', type=%s, confidence=%.2f, tokens=%d}", 
                           expression, type, confidence, tokens.size());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MultiWordExpression mwe = (MultiWordExpression) obj;
        return expression.equals(mwe.expression) && 
               type == mwe.type &&
               Math.abs(confidence - mwe.confidence) < 0.001;
    }
    
    @Override
    public int hashCode() {
        return expression.hashCode() * 31 + type.hashCode();
    }
}