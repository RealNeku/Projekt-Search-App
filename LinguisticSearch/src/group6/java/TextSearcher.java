package group6.java;

import java.util.ArrayList;
import java.util.List;

public class TextSearcher {

    /**
     * Searches sentences for a query match by type: "word" (syntax tokens), "lemma", or "pos"
     */
    public static List<AnnotatedSentence> search(List<AnnotatedSentence> sentences, String query, String type) {
        List<AnnotatedSentence> results = new ArrayList<>();
        if (sentences == null || query == null || type == null) return results;

        String typeLower = type.toLowerCase();

        for (AnnotatedSentence sentence : sentences) {
            String[] arrayToSearch;

            switch (typeLower) {
                case "word":
                case "syntax":
                    // synonym for word search on tokens
                    arrayToSearch = sentence.syntax.tokens;
                    break;
                case "lemma":
                    arrayToSearch = sentence.lemma.lemmas;
                    break;
                case "pos":
                    arrayToSearch = sentence.pos.posTags;
                    break;
                default:
                    continue;
            }
            if (arrayToSearch == null) continue;

            for (String s : arrayToSearch) {
                if (s != null && s.equalsIgnoreCase(query)) {
                    results.add(sentence);
                    break;
                }
            }
        }

        return results;
    }
}