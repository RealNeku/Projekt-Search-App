package group6.java;

/**
 * Holds POS tagging data for a given sentence
 */
public class POSData {
    public final String[] posTags;   // POS tags aligned to tokens

    public POSData(String[] posTags) {
        this.posTags = posTags;
    }
}