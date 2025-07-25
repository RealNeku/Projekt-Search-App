package group6.java;
///**
// * This class holds the linguistic analysis results for a single sentence.
// * It stores:
// *  - the original sentence,
// *  - the tokens (words),
// *  - the POS tags for each token,
// *  - the lemmas for each token.
// */
//public class AnnotatedSentence {
//    public final String sentence;     // The full original sentence text
//    public final String[] tokens;     // Individual words in the sentence
//    public final String[] posTags;    // POS tags for each token
//    public final String[] lemmas;     // Lemmas for each token
//
//    public AnnotatedSentence(String sentence, String[] tokens, String[] posTags, String[] lemmas) {
//        this.sentence = sentence;
//        this.tokens = tokens;
//        this.posTags = posTags;
//        this.lemmas = lemmas;
//    }
//}

/**
 * This class holds the full linguistic annotation for a sentence,
 * grouped by linguistic categories: syntax, pos, lemma, semantic
 */
public class AnnotatedSentence {
    public final SyntaxData syntax;
    public final POSData pos;
    public final LemmaData lemma;
    public final SemanticData semantic;

    public AnnotatedSentence(SyntaxData syntax, POSData pos, LemmaData lemma, SemanticData semantic) {
        this.syntax = syntax;
        this.pos = pos;
        this.lemma = lemma;
        this.semantic = semantic;
    }

    public LemmaData getLemmaData() {
        return lemma;
    }

    public POSData getPosData() {
        return pos;
    }
    public SyntaxData getSyntaxData() {
        return syntax;
    }
    public SemanticData getSemanticData() {
        return semantic;
    }

}