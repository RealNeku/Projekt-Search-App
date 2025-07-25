package group6.java;

/**
 * Holds syntax-related data: original sentence and its tokens
 */
public class SyntaxData {
    public final String sentence;    // original sentence
    public final String[] tokens;    // tokenized words

    public SyntaxData(String sentence, String[] tokens) {
        this.sentence = sentence;
        this.tokens = tokens;
    }
}