import java.util.ArrayList;
import java.util.List;

public class TextSearcher {

    /**
     * Searches sentences for a query match by type: "word" (syntax tokens), "lemma", or "pos"
     */
    public static List<AnnotatedSentence> search(List<AnnotatedSentence> sentences, String query, String type) {
        List<AnnotatedSentence> results = new ArrayList<>();

        for (AnnotatedSentence sentence : sentences) {
            String[] arrayToSearch;

            switch (type.toLowerCase()) {
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
                // extend to semantic search when semantic data is implemented
                default:
                    continue;
            }

            for (String s : arrayToSearch) {
                if (s.equalsIgnoreCase(query)) {
                    results.add(sentence);
                    break;
                }
            }
        }

        return results;
    }
}
