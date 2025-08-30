package group6.java.nlp;

import group6.java.model.LinguisticToken;
import group6.java.model.MultiWordExpression;
import group6.java.model.MultiWordExpression.MWEType;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * German Multiword Expression (MWE) Extractor
 * Implements various algorithms to identify and extract German MWEs from tokenized text
 */
public class GermanMWEExtractor {
    
    private GermanLanguageProcessor processor;
    private double minConfidence = 0.5;
    private int maxMWELength = 5;
    private int minMWELength = 2;
    
    // Pattern-based rules for German MWEs
    private static final Map<Pattern, MWEType> MWE_PATTERNS = new HashMap<>();
    
    static {
        // Compound nouns (very common in German)
        MWE_PATTERNS.put(Pattern.compile("^[A-ZÜÄÖ][a-züäöß]*[a-züäöß]{4,}$"), MWEType.COMPOUND_NOUN);
        
        // Prepositional phrases
        MWE_PATTERNS.put(Pattern.compile("(in|im|an|auf|unter|über|von|zu|mit|bei)\\s+.+"), MWEType.PREPOSITIONAL_PHRASE);
        
        // Named entities (capitalized sequences)
        MWE_PATTERNS.put(Pattern.compile("([A-ZÜÄÖ][a-züäöß]*\\s+){1,3}[A-ZÜÄÖ][a-züäöß]*"), MWEType.NAMED_ENTITY);
    }
    
    // Common German collocations and idioms
    private static final Map<String, MWEType> KNOWN_MWES = new HashMap<>();
    
    static {
        // Common German idioms
        KNOWN_MWES.put("ins gras beißen", MWEType.IDIOMATIC_EXPRESSION);
        KNOWN_MWES.put("die katze aus dem sack lassen", MWEType.IDIOMATIC_EXPRESSION);
        KNOWN_MWES.put("den nagel auf den kopf treffen", MWEType.IDIOMATIC_EXPRESSION);
        KNOWN_MWES.put("über den berg sein", MWEType.IDIOMATIC_EXPRESSION);
        
        // Light verb constructions
        KNOWN_MWES.put("eine entscheidung treffen", MWEType.LIGHT_VERB_CONSTRUCTION);
        KNOWN_MWES.put("eine frage stellen", MWEType.LIGHT_VERB_CONSTRUCTION);
        KNOWN_MWES.put("einen beschluss fassen", MWEType.LIGHT_VERB_CONSTRUCTION);
        KNOWN_MWES.put("eine rolle spielen", MWEType.LIGHT_VERB_CONSTRUCTION);
        
        // Common collocations
        KNOWN_MWES.put("schwere entscheidung", MWEType.COLLOCATION);
        KNOWN_MWES.put("große bedeutung", MWEType.COLLOCATION);
        KNOWN_MWES.put("wichtige rolle", MWEType.COLLOCATION);
        KNOWN_MWES.put("starker eindruck", MWEType.COLLOCATION);
        
        // Prepositional phrases
        KNOWN_MWES.put("im gegensatz zu", MWEType.PREPOSITIONAL_PHRASE);
        KNOWN_MWES.put("im hinblick auf", MWEType.PREPOSITIONAL_PHRASE);
        KNOWN_MWES.put("in bezug auf", MWEType.PREPOSITIONAL_PHRASE);
        KNOWN_MWES.put("auf grund von", MWEType.PREPOSITIONAL_PHRASE);
    }
    
    public GermanMWEExtractor(GermanLanguageProcessor processor) {
        this.processor = processor;
    }
    
    /**
     * Extract all types of MWEs from a list of tokens
     */
    public List<MultiWordExpression> extractMWEs(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        
        // Apply different extraction strategies
        mwes.addAll(extractByPatternMatching(tokens));
        mwes.addAll(extractByStatisticalMeasures(tokens));
        mwes.addAll(extractBySyntacticPatterns(tokens));
        mwes.addAll(extractCompoundNouns(tokens));
        mwes.addAll(extractPhraseVerbs(tokens));
        mwes.addAll(extractNamedEntities(tokens));
        
        // Remove duplicates and filter by confidence
        return deduplicateAndFilter(mwes);
    }
    
    /**
     * Extract MWEs using predefined patterns
     */
    private List<MultiWordExpression> extractByPatternMatching(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        
        // Check against known MWEs
        for (int i = 0; i < tokens.size(); i++) {
            for (int j = i + minMWELength; j <= Math.min(i + maxMWELength, tokens.size()); j++) {
                List<LinguisticToken> candidate = tokens.subList(i, j);
                String candidateText = candidate.stream()
                    .map(t -> t.getLemma().toLowerCase())
                    .collect(Collectors.joining(" "));
                
                if (KNOWN_MWES.containsKey(candidateText)) {
                    MultiWordExpression mwe = new MultiWordExpression(candidate, KNOWN_MWES.get(candidateText));
                    mwe.setConfidence(0.9); // High confidence for known MWEs
                    mwes.add(mwe);
                }
            }
        }
        
        return mwes;
    }
    
    /**
     * Extract MWEs using statistical measures (PMI, frequency, etc.)
     */
    private List<MultiWordExpression> extractByStatisticalMeasures(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        Map<String, Integer> bigramCounts = new HashMap<>();
        Map<String, Integer> wordCounts = new HashMap<>();
        
        // Count unigrams and bigrams
        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i).getLemma().toLowerCase();
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            
            if (i < tokens.size() - 1) {
                String bigram = word + " " + tokens.get(i + 1).getLemma().toLowerCase();
                bigramCounts.put(bigram, bigramCounts.getOrDefault(bigram, 0) + 1);
            }
        }
        
        // Calculate PMI for bigrams
        int totalBigrams = bigramCounts.values().stream().mapToInt(Integer::intValue).sum();
        int totalWords = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
        
        for (int i = 0; i < tokens.size() - 1; i++) {
            String word1 = tokens.get(i).getLemma().toLowerCase();
            String word2 = tokens.get(i + 1).getLemma().toLowerCase();
            String bigram = word1 + " " + word2;
            
            if (bigramCounts.containsKey(bigram) && bigramCounts.get(bigram) > 1) {
                double pmi = calculatePMI(
                    bigramCounts.get(bigram), totalBigrams,
                    wordCounts.get(word1), wordCounts.get(word2), totalWords
                );
                
                if (pmi > 2.0) { // Threshold for significant association
                    List<LinguisticToken> candidateTokens = Arrays.asList(tokens.get(i), tokens.get(i + 1));
                    MultiWordExpression mwe = new MultiWordExpression(candidateTokens, MWEType.COLLOCATION);
                    mwe.setConfidence(Math.min(pmi / 10.0, 1.0)); // Normalize PMI to confidence
                    mwes.add(mwe);
                }
            }
        }
        
        return mwes;
    }
    
    /**
     * Calculate Pointwise Mutual Information
     */
    private double calculatePMI(int bigramCount, int totalBigrams, int word1Count, int word2Count, int totalWords) {
        double p_bigram = (double) bigramCount / totalBigrams;
        double p_word1 = (double) word1Count / totalWords;
        double p_word2 = (double) word2Count / totalWords;
        
        return Math.log(p_bigram / (p_word1 * p_word2)) / Math.log(2);
    }
    
    /**
     * Extract MWEs using syntactic patterns (POS-based rules)
     */
    private List<MultiWordExpression> extractBySyntacticPatterns(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        
        for (int i = 0; i < tokens.size() - 1; i++) {
            LinguisticToken current = tokens.get(i);
            LinguisticToken next = tokens.get(i + 1);
            
            // Adjective + Noun pattern (common German collocations)
            if (isAdjective(current.getPosTag()) && isNoun(next.getPosTag())) {
                List<LinguisticToken> candidateTokens = Arrays.asList(current, next);
                MultiWordExpression mwe = new MultiWordExpression(candidateTokens, MWEType.COLLOCATION);
                mwe.setConfidence(0.6);
                mwes.add(mwe);
            }
            
            // Verb + Preposition pattern (phrasal verbs)
            if (isVerb(current.getPosTag()) && processor.isGermanPreposition(next.getWord())) {
                List<LinguisticToken> candidateTokens = Arrays.asList(current, next);
                MultiWordExpression mwe = new MultiWordExpression(candidateTokens, MWEType.PHRASAL_VERB);
                mwe.setConfidence(0.7);
                mwes.add(mwe);
            }
            
            // Article + Adjective + Noun pattern
            if (i < tokens.size() - 2) {
                LinguisticToken third = tokens.get(i + 2);
                if (processor.isGermanArticle(current.getWord()) && 
                    isAdjective(next.getPosTag()) && 
                    isNoun(third.getPosTag())) {
                    List<LinguisticToken> candidateTokens = Arrays.asList(current, next, third);
                    MultiWordExpression mwe = new MultiWordExpression(candidateTokens, MWEType.COLLOCATION);
                    mwe.setConfidence(0.65);
                    mwes.add(mwe);
                }
            }
        }
        
        return mwes;
    }
    
    /**
     * Extract German compound nouns
     */
    private List<MultiWordExpression> extractCompoundNouns(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        
        for (LinguisticToken token : tokens) {
            if (processor.isPotentialCompound(token.getWord()) && isNoun(token.getPosTag())) {
                List<LinguisticToken> candidateTokens = Arrays.asList(token);
                MultiWordExpression mwe = new MultiWordExpression(candidateTokens, MWEType.COMPOUND_NOUN);
                mwe.setConfidence(0.8);
                mwes.add(mwe);
            }
        }
        
        return mwes;
    }
    
    /**
     * Extract German separable verbs and phrasal verbs
     */
    private List<MultiWordExpression> extractPhraseVerbs(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        
        // Look for separable verb patterns
        for (int i = 0; i < tokens.size(); i++) {
            LinguisticToken token = tokens.get(i);
            
            if (processor.isSeparablePrefix(token.getWord())) {
                // Look for corresponding verb in the same sentence
                for (int j = i + 1; j < tokens.size() && 
                     tokens.get(j).getSentenceIndex() == token.getSentenceIndex(); j++) {
                    LinguisticToken potentialVerb = tokens.get(j);
                    if (isVerb(potentialVerb.getPosTag())) {
                        List<LinguisticToken> candidateTokens = Arrays.asList(token, potentialVerb);
                        MultiWordExpression mwe = new MultiWordExpression(candidateTokens, MWEType.PHRASAL_VERB);
                        mwe.setConfidence(0.75);
                        mwes.add(mwe);
                        break;
                    }
                }
            }
        }
        
        return mwes;
    }
    
    /**
     * Extract named entities (proper nouns sequences)
     */
    private List<MultiWordExpression> extractNamedEntities(List<LinguisticToken> tokens) {
        List<MultiWordExpression> mwes = new ArrayList<>();
        List<LinguisticToken> currentEntity = new ArrayList<>();
        
        for (LinguisticToken token : tokens) {
            if (isProperNoun(token.getPosTag()) || 
                (Character.isUpperCase(token.getWord().charAt(0)) && isNoun(token.getPosTag()))) {
                currentEntity.add(token);
            } else {
                if (currentEntity.size() >= 2) {
                    MultiWordExpression mwe = new MultiWordExpression(
                        new ArrayList<>(currentEntity), MWEType.NAMED_ENTITY);
                    mwe.setConfidence(0.85);
                    mwes.add(mwe);
                }
                currentEntity.clear();
            }
        }
        
        // Handle entity at end of text
        if (currentEntity.size() >= 2) {
            MultiWordExpression mwe = new MultiWordExpression(
                new ArrayList<>(currentEntity), MWEType.NAMED_ENTITY);
            mwe.setConfidence(0.85);
            mwes.add(mwe);
        }
        
        return mwes;
    }
    
    /**
     * Remove duplicates and filter by confidence threshold
     */
    private List<MultiWordExpression> deduplicateAndFilter(List<MultiWordExpression> mwes) {
        Map<String, MultiWordExpression> uniqueMWEs = new HashMap<>();
        
        for (MultiWordExpression mwe : mwes) {
            String key = mwe.getExpression().toLowerCase();
            
            if (mwe.getConfidence() >= minConfidence) {
                // Keep the MWE with highest confidence
                if (!uniqueMWEs.containsKey(key) || 
                    uniqueMWEs.get(key).getConfidence() < mwe.getConfidence()) {
                    uniqueMWEs.put(key, mwe);
                }
            }
        }
        
        return new ArrayList<>(uniqueMWEs.values());
    }
    
    // POS tag helper methods
    private boolean isNoun(String posTag) {
        return posTag.startsWith("NN") || posTag.startsWith("N");
    }
    
    private boolean isProperNoun(String posTag) {
        return posTag.equals("NNP") || posTag.equals("NNPS") || posTag.startsWith("NE");
    }
    
    private boolean isVerb(String posTag) {
        return posTag.startsWith("VV") || posTag.startsWith("V");
    }
    
    private boolean isAdjective(String posTag) {
        return posTag.startsWith("ADJ") || posTag.startsWith("JJ");
    }
    
    // Getters and setters
    public double getMinConfidence() { return minConfidence; }
    public void setMinConfidence(double minConfidence) { this.minConfidence = minConfidence; }
    
    public int getMaxMWELength() { return maxMWELength; }
    public void setMaxMWELength(int maxMWELength) { this.maxMWELength = maxMWELength; }
    
    public int getMinMWELength() { return minMWELength; }
    public void setMinMWELength(int minMWELength) { this.minMWELength = minMWELength; }
}