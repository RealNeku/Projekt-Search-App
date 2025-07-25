package group6.java;

import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes text with OpenNLP: sentence detection, tokenization, POS tagging, lemmatization.
 */
public class TextProcessor {

    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private LemmatizerME lemmatizer;

    public TextProcessor() throws IOException {
        this("resources/models/en-sent.bin",
                "resources/models/en-token.bin",
                "resources/models/en-pos-maxent.bin",
                "resources/models/en-lemmatizer.bin");
    }

    /**
     * Constructor that initializes OpenNLP components with model file paths.
     *
     * @param sentModelPath  path to sentence detection model file
     * @param tokenModelPath path to tokenizer model file
     * @param posModelPath   path to POS tagging model file
     * @param lemmaModelPath path to lemmatizer model file
     * @throws IOException if any model fails to load
     */
    public TextProcessor(String sentModelPath, String tokenModelPath, String posModelPath, String lemmaModelPath) throws IOException {
        // Load sentence model
        try (InputStream sentModelStream = new FileInputStream(sentModelPath)) {
            sentenceDetector = new SentenceDetectorME(new SentenceModel(sentModelStream));
        } catch (IOException e) {
            System.err.println("Error loading sentence detection model from '" + sentModelPath + "': " + e.getMessage());
            throw e;
        }

        // Load tokenizer model
        try (InputStream tokenModelStream = new FileInputStream(tokenModelPath)) {
            tokenizer = new TokenizerME(new TokenizerModel(tokenModelStream));
        } catch (IOException e) {
            System.err.println("Error loading tokenizer model from '" + tokenModelPath + "': " + e.getMessage());
            throw e;
        }

        // Load POS tagger model
        try (InputStream posModelStream = new FileInputStream(posModelPath)) {
            posTagger = new POSTaggerME(new POSModel(posModelStream));
        } catch (IOException e) {
            System.err.println("Error loading POS tagging model from '" + posModelPath + "': " + e.getMessage());
            throw e;
        }

        // Load lemmatizer model
        try (InputStream lemmaModelStream = new FileInputStream(lemmaModelPath)) {
            lemmatizer = new LemmatizerME(new LemmatizerModel(lemmaModelStream));
        } catch (IOException e) {
            System.err.println("Error loading lemmatizer model from '" + lemmaModelPath + "': " + e.getMessage());
            throw e;
        }
    }

    /**
     * Processes input text into annotated sentences containing tokens, POS tags, lemmas, and semantic placeholders.
     *
     * @param text the input text to process
     * @return list of annotated sentences
     */
    public List<AnnotatedSentence> processText(String text) {
        List<AnnotatedSentence> results = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            System.err.println("Input text is null or empty.");
            return results;
        }

        String[] sentences = sentenceDetector.sentDetect(text);

        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence);
            String[] posTags = posTagger.tag(tokens);
            String[] lemmas = lemmatizer.lemmatize(tokens, posTags);

            SyntaxData syntaxData = new SyntaxData(sentence, tokens);
            POSData posData = new POSData(posTags);
            LemmaData lemmaData = new LemmaData(lemmas);
            SemanticData semanticData = new SemanticData(); // If unused, just pass empty

            AnnotatedSentence annotated = new AnnotatedSentence(syntaxData, posData, lemmaData, semanticData);
            results.add(annotated);
        }

        return results;
    }
}

