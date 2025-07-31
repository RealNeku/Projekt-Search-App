package group6.java.nlp;

import group6.java.model.LinguisticToken;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.util.regex.Pattern;

public class GermanMWEProcessor {
    private StanfordCoreNLP pipeline;
    
    // German MWE patterns for compound detection
    private static final Pattern COMPOUND_PATTERN = Pattern.compile("\\w+(?:-\\w+)+");
    private static final Pattern PHRASAL_VERB_PATTERN = Pattern.compile("\\b(?:ab|an|auf|aus|bei|durch|ein|mit|nach|über|um|unter|vor|weg|zu)\\w*\\b");
    
    // German MWE categories
    private static final Set<String> NAMED_ENTITY_TYPES = Set.of("PERSON", "LOCATION", "ORGANIZATION", "MISC");
    private static final Set<String> COMPOUND_TYPES = Set.of("COMPOUND_NOUN", "COMPOUND_ADJ", "COMPOUND_VERB");

    public GermanMWEProcessor() {
        initializePipeline();
    }

    /**
     * Initialize Stanford CoreNLP pipeline for German
     */
    private void initializePipeline() {
        try {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
            props.setProperty("tokenize.language", "de");
            props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger");
            props.setProperty("ner.model", "edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz");
            props.setProperty("parse.model", "edu/stanford/nlp/models/parser/nndep/UD_German.gz");
            
            this.pipeline = new StanfordCoreNLP(props);
            System.out.println("German MWE Pipeline initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing German MWE pipeline: " + e.getMessage());
            // Fallback to basic properties
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos");
            props.setProperty("tokenize.language", "de");
            this.pipeline = new StanfordCoreNLP(props);
        }
    }

    /**
     * Process German text and detect multi-word expressions
     * @param text Input German text to process
     * @return List of LinguisticToken objects with MWE information
     */
    public List<LinguisticToken> processText(String text) {
        List<LinguisticToken> tokens = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return tokens;
        }

        try {
            Annotation document = new Annotation(text);
            pipeline.annotate(document);

            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            
            for (int sentenceIndex = 0; sentenceIndex < sentences.size(); sentenceIndex++) {
                CoreMap sentence = sentences.get(sentenceIndex);
                List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);
                
                // Detect MWEs in this sentence
                List<MWESpan> mweSpans = detectMWEs(coreLabels, sentence.toString());
                
                for (int tokenIndex = 0; tokenIndex < coreLabels.size(); tokenIndex++) {
                    CoreLabel token = coreLabels.get(tokenIndex);
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    
                    // Find if this token is part of an MWE
                    MWEInfo mweInfo = getMWEInfo(tokenIndex, mweSpans);
                    
                    LinguisticToken linguisticToken = new LinguisticToken(
                        word,
                        mweInfo.type,
                        mweInfo.label,
                        mweInfo.isStart,
                        mweInfo.isEnd,
                        mweInfo.fullForm,
                        sentence.toString(),
                        sentenceIndex,
                        tokenIndex
                    );
                    
                    tokens.add(linguisticToken);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing text: " + e.getMessage());
            e.printStackTrace();
        }

        return tokens;
    }

    /**
     * Detect multi-word expressions in a sentence
     */
    private List<MWESpan> detectMWEs(List<CoreLabel> tokens, String sentenceText) {
        List<MWESpan> mweSpans = new ArrayList<>();
        
        // Detect compounds
        mweSpans.addAll(detectCompounds(tokens));
        
        // Detect named entities
        mweSpans.addAll(detectNamedEntities(tokens));
        
        // Detect phrasal verbs
        mweSpans.addAll(detectPhrasalVerbs(tokens));
        
        // Detect separable verb constructions
        mweSpans.addAll(detectSeparableVerbs(tokens));
        
        return mweSpans;
    }

    /**
     * Detect German compound words
     */
    private List<MWESpan> detectCompounds(List<CoreLabel> tokens) {
        List<MWESpan> compounds = new ArrayList<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i).get(CoreAnnotations.TextAnnotation.class);
            String pos = tokens.get(i).get(CoreAnnotations.PartOfSpeechAnnotation.class);
            
            // Detect hyphenated compounds
            if (COMPOUND_PATTERN.matcher(word).matches()) {
                compounds.add(new MWESpan(i, i, "COMPOUND", "HYPHENATED_COMPOUND", word));
            }
            // Detect noun compounds (German characteristic)
            else if (pos != null && pos.startsWith("NN") && word.length() > 8) {
                // Heuristic: long nouns are likely compounds in German
                compounds.add(new MWESpan(i, i, "COMPOUND", "NOUN_COMPOUND", word));
            }
        }
        
        return compounds;
    }

    /**
     * Detect named entities as MWEs
     */
    private List<MWESpan> detectNamedEntities(List<CoreLabel> tokens) {
        List<MWESpan> entities = new ArrayList<>();
        
        int start = -1;
        String currentEntity = null;
        StringBuilder entityText = new StringBuilder();
        
        for (int i = 0; i < tokens.size(); i++) {
            String ner = tokens.get(i).get(CoreAnnotations.NamedEntityTagAnnotation.class);
            String word = tokens.get(i).get(CoreAnnotations.TextAnnotation.class);
            
            if (ner != null && NAMED_ENTITY_TYPES.contains(ner)) {
                if (start == -1) {
                    start = i;
                    currentEntity = ner;
                    entityText = new StringBuilder(word);
                } else if (ner.equals(currentEntity)) {
                    entityText.append(" ").append(word);
                } else {
                    // Different entity type, finish previous and start new
                    if (start != i - 1) { // Multi-token entity
                        entities.add(new MWESpan(start, i - 1, "NAMED_ENTITY", currentEntity, entityText.toString()));
                    }
                    start = i;
                    currentEntity = ner;
                    entityText = new StringBuilder(word);
                }
            } else {
                if (start != -1 && start != i - 1) { // Finish multi-token entity
                    entities.add(new MWESpan(start, i - 1, "NAMED_ENTITY", currentEntity, entityText.toString()));
                }
                start = -1;
                currentEntity = null;
            }
        }
        
        // Handle entity at end of sentence
        if (start != -1 && start != tokens.size() - 1) {
            entities.add(new MWESpan(start, tokens.size() - 1, "NAMED_ENTITY", currentEntity, entityText.toString()));
        }
        
        return entities;
    }

    /**
     * Detect German phrasal verbs
     */
    private List<MWESpan> detectPhrasalVerbs(List<CoreLabel> tokens) {
        List<MWESpan> phrasalVerbs = new ArrayList<>();
        
        for (int i = 0; i < tokens.size() - 1; i++) {
            String word = tokens.get(i).get(CoreAnnotations.TextAnnotation.class);
            String pos = tokens.get(i).get(CoreAnnotations.PartOfSpeechAnnotation.class);
            
            if (pos != null && pos.startsWith("V") && PHRASAL_VERB_PATTERN.matcher(word).matches()) {
                // Look for particle in next few tokens
                for (int j = i + 1; j < Math.min(i + 4, tokens.size()); j++) {
                    String nextWord = tokens.get(j).get(CoreAnnotations.TextAnnotation.class);
                    String nextPos = tokens.get(j).get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    
                    if (nextPos != null && nextPos.equals("PTKVZ")) { // Particle
                        phrasalVerbs.add(new MWESpan(i, j, "PHRASAL_VERB", "SEPARABLE_VERB", 
                                word + " " + nextWord));
                        break;
                    }
                }
            }
        }
        
        return phrasalVerbs;
    }

    /**
     * Detect German separable verb constructions
     */
    private List<MWESpan> detectSeparableVerbs(List<CoreLabel> tokens) {
        List<MWESpan> separableVerbs = new ArrayList<>();
        
        // Look for verb + particle combinations typical in German
        for (int i = 0; i < tokens.size() - 1; i++) {
            String word = tokens.get(i).get(CoreAnnotations.TextAnnotation.class);
            String pos = tokens.get(i).get(CoreAnnotations.PartOfSpeechAnnotation.class);
            
            if (pos != null && pos.startsWith("V")) {
                // Common German separable verb prefixes
                String[] prefixes = {"ab", "an", "auf", "aus", "bei", "durch", "ein", "mit", 
                                   "nach", "über", "um", "unter", "vor", "weg", "zu"};
                
                for (String prefix : prefixes) {
                    if (word.startsWith(prefix)) {
                        separableVerbs.add(new MWESpan(i, i, "SEPARABLE_VERB", "PREFIX_VERB", word));
                        break;
                    }
                }
            }
        }
        
        return separableVerbs;
    }

    /**
     * Get MWE information for a specific token index
     */
    private MWEInfo getMWEInfo(int tokenIndex, List<MWESpan> mweSpans) {
        for (MWESpan span : mweSpans) {
            if (tokenIndex >= span.start && tokenIndex <= span.end) {
                return new MWEInfo(
                    span.type,
                    span.label,
                    tokenIndex == span.start,
                    tokenIndex == span.end,
                    span.fullForm
                );
            }
        }
        
        return new MWEInfo("NONE", "SINGLE_TOKEN", false, false, null);
    }

    /**
     * Search for tokens by MWE type
     */
    public List<LinguisticToken> searchByMWEType(List<LinguisticToken> tokens, String searchTerm) {
        List<LinguisticToken> results = new ArrayList<>();
        String upperSearchTerm = searchTerm.toUpperCase();
        
        for (LinguisticToken token : tokens) {
            if (token.getMweType() != null && token.getMweType().toUpperCase().contains(upperSearchTerm)) {
                results.add(token);
            }
        }
        
        return results;
    }

    /**
     * Search for tokens by MWE label
     */
    public List<LinguisticToken> searchByMWELabel(List<LinguisticToken> tokens, String searchTerm) {
        List<LinguisticToken> results = new ArrayList<>();
        String upperSearchTerm = searchTerm.toUpperCase();
        
        for (LinguisticToken token : tokens) {
            if (token.getMweLabel() != null && token.getMweLabel().toUpperCase().contains(upperSearchTerm)) {
                results.add(token);
            }
        }
        
        return results;
    }

    /**
     * Search for complete MWEs by their full form
     */
    public List<LinguisticToken> searchByMWEFullForm(List<LinguisticToken> tokens, String searchTerm) {
        List<LinguisticToken> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();
        
        for (LinguisticToken token : tokens) {
            if (token.getMweFullForm() != null && 
                token.getMweFullForm().toLowerCase().contains(lowerSearchTerm)) {
                results.add(token);
            }
        }
        
        return results;
    }

    /**
     * Search for tokens by word (original functionality preserved)
     */
    public List<LinguisticToken> searchByWord(List<LinguisticToken> tokens, String searchTerm) {
        List<LinguisticToken> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();

        for (LinguisticToken token : tokens) {
            if (token.getWord().toLowerCase().equals(lowerSearchTerm)) {
                results.add(token);
            }
        }

        return results;
    }

    // Helper classes
    private static class MWESpan {
        final int start;
        final int end;
        final String type;
        final String label;
        final String fullForm;
        
        MWESpan(int start, int end, String type, String label, String fullForm) {
            this.start = start;
            this.end = end;
            this.type = type;
            this.label = label;
            this.fullForm = fullForm;
        }
    }

    private static class MWEInfo {
        final String type;
        final String label;
        final boolean isStart;
        final boolean isEnd;
        final String fullForm;
        
        MWEInfo(String type, String label, boolean isStart, boolean isEnd, String fullForm) {
            this.type = type;
            this.label = label;
            this.isStart = isStart;
            this.isEnd = isEnd;
            this.fullForm = fullForm;
        }
    }
}