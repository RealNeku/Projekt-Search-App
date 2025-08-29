package group6.java.nlp;

import group6.java.model.LinguisticToken;
import group6.java.model.MultiWordExpression;
import group6.java.model.MultiWordExpression.MWEType;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * German language processor specialized for MWE extraction
 */
public class GermanLanguageProcessor {
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private LemmatizerME lemmatizer;
    
    // German-specific patterns and rules
    private static final Pattern COMPOUND_PATTERN = Pattern.compile(
        "^[A-ZÜÄÖ][a-züäöß]*[a-züäöß]{3,}$"); // Matches potential German compounds
    
    private static final Set<String> GERMAN_PREPOSITIONS = Set.of(
        "an", "auf", "aus", "bei", "bis", "durch", "für", "gegen", "hinter", 
        "in", "mit", "nach", "neben", "ohne", "über", "um", "unter", "von", 
        "vor", "während", "wegen", "zwischen", "zu"
    );
    
    private static final Set<String> GERMAN_ARTICLES = Set.of(
        "der", "die", "das", "den", "dem", "des", "ein", "eine", "einer", 
        "eines", "einem", "einen"
    );
    
    private static final Set<String> MODAL_VERBS = Set.of(
        "können", "müssen", "dürfen", "sollen", "wollen", "mögen", "möchten"
    );
    
    // Common German phrasal verbs and separable verbs
    private static final Set<String> SEPARABLE_PREFIXES = Set.of(
        "ab", "an", "auf", "aus", "bei", "ein", "fest", "her", "hin", "los", 
        "mit", "nach", "über", "um", "unter", "vor", "weg", "weiter", "zu", "zurück"
    );
    
    public GermanLanguageProcessor() throws Exception {
        initializeGermanModels();
    }
    
    /**
     * Initialize German language models
     */
    private void initializeGermanModels() throws Exception {
        try {
            // Try to load German models first, fall back to English if not available
            SentenceModel sentenceModel = loadGermanSentenceModel();
            sentenceDetector = new SentenceDetectorME(sentenceModel);
            
            TokenizerModel tokenizerModel = loadGermanTokenizerModel();
            tokenizer = new TokenizerME(tokenizerModel);
            
            POSModel posModel = loadGermanPOSModel();
            posTagger = new POSTaggerME(posModel);
            
            try {
                LemmatizerModel lemmaModel = loadGermanLemmatizerModel();
                lemmatizer = new LemmatizerME(lemmaModel);
                System.out.println("German language models loaded successfully");
            } catch (Exception e) {
                System.out.println("Warning: German lemmatizer model not found. Using word forms as lemmas.");
                lemmatizer = null;
            }
        } catch (Exception e) {
            System.out.println("Warning: German models not found. Falling back to English models for basic processing.");
            // Fall back to English models from the existing framework
            SentenceModel sentenceModel = ModelLoader.loadSentenceModel();
            sentenceDetector = new SentenceDetectorME(sentenceModel);
            
            TokenizerModel tokenizerModel = ModelLoader.loadTokenizerModel();
            tokenizer = new TokenizerME(tokenizerModel);
            
            POSModel posModel = ModelLoader.loadPOSModel();
            posTagger = new POSTaggerME(posModel);
            
            lemmatizer = null;
        }
    }
    
    /**
     * Process German text and extract linguistic tokens
     */
    public List<LinguisticToken> processText(String text) {
        List<LinguisticToken> tokens = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return tokens;
        }
        
        // Preprocess German text
        text = preprocessGermanText(text);
        
        String[] sentences = sentenceDetector.sentDetect(text);
        for (int sentenceIndex = 0; sentenceIndex < sentences.length; sentenceIndex++) {
            String sentence = sentences[sentenceIndex];
            
            String[] words = tokenizer.tokenize(sentence);
            if (words.length == 0) continue;
            
            String[] posTags = posTagger.tag(words);
            String[] lemmas;
            
            if (lemmatizer != null) {
                lemmas = lemmatizer.lemmatize(words, posTags);
            } else {
                lemmas = performBasicGermanLemmatization(words, posTags);
            }
            
            for (int tokenIndex = 0; tokenIndex < words.length; tokenIndex++) {
                String word = words[tokenIndex];
                String posTag = posTags[tokenIndex];
                String lemma = lemmas[tokenIndex];
                
                LinguisticToken token = new LinguisticToken(word, lemma, posTag,
                        sentence, sentenceIndex, tokenIndex);
                tokens.add(token);
            }
        }
        
        return tokens;
    }
    
    /**
     * Preprocess German text for better tokenization
     */
    private String preprocessGermanText(String text) {
        // Handle German-specific characters and conventions
        text = text.replaceAll("ß", "ss"); // Optional: normalize ß
        
        // Handle hyphenated compounds
        text = text.replaceAll("([a-züäöß]+)-([a-züäöß]+)", "$1$2");
        
        // Normalize quotation marks
        text = text.replaceAll("[\u201E\u201C\u201D\u201F]", "\"");
        text = text.replaceAll("[\u201A\u2018\u2019]", "'");
        
        return text;
    }
    
    /**
     * Basic German lemmatization when no lemmatizer model is available
     */
    private String[] performBasicGermanLemmatization(String[] words, String[] posTags) {
        String[] lemmas = new String[words.length];
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            String pos = posTags[i];
            
            // Basic German lemmatization rules
            if (pos.startsWith("NN")) { // Nouns
                lemmas[i] = lemmatizeGermanNoun(word);
            } else if (pos.startsWith("VV")) { // Verbs
                lemmas[i] = lemmatizeGermanVerb(word);
            } else if (pos.startsWith("ADJ")) { // Adjectives
                lemmas[i] = lemmatizeGermanAdjective(word);
            } else {
                lemmas[i] = word;
            }
        }
        
        return lemmas;
    }
    
    private String lemmatizeGermanNoun(String word) {
        // Remove common German noun endings
        if (word.endsWith("en") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("er") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("e") && word.length() > 2) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
    
    private String lemmatizeGermanVerb(String word) {
        // Remove common German verb endings
        if (word.endsWith("en") && word.length() > 3) {
            return word; // Infinitive form is often the lemma
        }
        if (word.endsWith("t") && word.length() > 2) {
            return word.substring(0, word.length() - 1) + "en";
        }
        if (word.endsWith("st") && word.length() > 3) {
            return word.substring(0, word.length() - 2) + "en";
        }
        return word;
    }
    
    private String lemmatizeGermanAdjective(String word) {
        // Remove common German adjective endings
        if (word.endsWith("en") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("er") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("e") && word.length() > 2) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
    
    // Model loading methods (will try to load German models)
    private SentenceModel loadGermanSentenceModel() throws Exception {
        String[] fileNames = {
            "/models/de-sent.bin",
            "/models/german-sentence-model.bin",
            "/models/opennlp-de-ud-gsd-sentence-1.0-1.9.3.bin"
        };
        
        for (String path : fileNames) {
            try (InputStream modelIn = this.getClass().getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new SentenceModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }
        
        // Fall back to English model
        return ModelLoader.loadSentenceModel();
    }
    
    private TokenizerModel loadGermanTokenizerModel() throws Exception {
        String[] fileNames = {
            "/models/de-token.bin",
            "/models/german-tokenizer-model.bin",
            "/models/opennlp-de-ud-gsd-tokens-1.0-1.9.3.bin"
        };
        
        for (String path : fileNames) {
            try (InputStream modelIn = this.getClass().getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new TokenizerModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }
        
        // Fall back to English model
        return ModelLoader.loadTokenizerModel();
    }
    
    private POSModel loadGermanPOSModel() throws Exception {
        String[] fileNames = {
            "/models/de-pos-maxent.bin",
            "/models/german-pos-model.bin",
            "/models/opennlp-de-ud-gsd-pos-1.0-1.9.3.bin"
        };
        
        for (String path : fileNames) {
            try (InputStream modelIn = this.getClass().getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new POSModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }
        
        // Fall back to English model
        return ModelLoader.loadPOSModel();
    }
    
    private LemmatizerModel loadGermanLemmatizerModel() throws Exception {
        String[] fileNames = {
            "/models/de-lemmatizer.bin",
            "/models/german-lemmatizer-model.bin",
            "/models/opennlp-de-ud-gsd-lemmas-1.0-1.9.3.bin"
        };
        
        for (String path : fileNames) {
            try (InputStream modelIn = this.getClass().getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new LemmatizerModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }
        
        throw new Exception("German lemmatizer model not found");
    }
    
    // Helper methods for MWE extraction
    public boolean isGermanPreposition(String word) {
        return GERMAN_PREPOSITIONS.contains(word.toLowerCase());
    }
    
    public boolean isGermanArticle(String word) {
        return GERMAN_ARTICLES.contains(word.toLowerCase());
    }
    
    public boolean isModalVerb(String word) {
        return MODAL_VERBS.contains(word.toLowerCase());
    }
    
    public boolean isSeparablePrefix(String word) {
        return SEPARABLE_PREFIXES.contains(word.toLowerCase());
    }
    
    public boolean isPotentialCompound(String word) {
        return COMPOUND_PATTERN.matcher(word).matches() && word.length() > 6;
    }
}