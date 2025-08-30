package group6.java;

import group6.java.model.LinguisticToken;
import group6.java.model.MultiWordExpression;
import group6.java.nlp.GermanLanguageProcessor;
import group6.java.nlp.GermanMWEExtractor;
import group6.java.scraper.GermanWikipediaScraper;
import group6.java.util.MWEResultFormatter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main application for extracting German Multiword Expressions from Wikipedia
 */
public class GermanMWEExtractorApp {
    
    public GermanLanguageProcessor processor;
    public GermanMWEExtractor extractor;
    public GermanWikipediaScraper scraper;
    public MWEResultFormatter formatter;
    
    public GermanMWEExtractorApp() throws Exception {
        this.processor = new GermanLanguageProcessor();
        this.extractor = new GermanMWEExtractor(processor);
        this.scraper = new GermanWikipediaScraper();
        this.formatter = new MWEResultFormatter();
        
        System.out.println("German MWE Extractor initialized successfully!");
    }
    
    /**
     * Extract MWEs from a Wikipedia article by URL
     */
    public List<MultiWordExpression> extractFromUrl(String url) {
        System.out.println("Scraping article from: " + url);
        String text = scraper.scrapeArticle(url);
        
        if (text.trim().isEmpty()) {
            System.out.println("No content extracted from URL");
            return new ArrayList<>();
        }
        
        return extractFromText(text, url);
    }
    
    /**
     * Extract MWEs from a Wikipedia article by title
     */
    public List<MultiWordExpression> extractFromTitle(String title) {
        System.out.println("Scraping article: " + title);
        String text = scraper.scrapeArticleByTitle(title);
        
        if (text.trim().isEmpty()) {
            System.out.println("No content extracted for title: " + title);
            return new ArrayList<>();
        }
        
        String url = "https://de.wikipedia.org/wiki/" + title;
        return extractFromText(text, url);
    }
    
    /**
     * Extract MWEs from plain text
     */
    public List<MultiWordExpression> extractFromText(String text, String sourceUrl) {
        System.out.println("Processing text (" + text.length() + " characters)...");
        
        // Process text to get linguistic tokens
        List<LinguisticToken> tokens = processor.processText(text);
        System.out.println("Extracted " + tokens.size() + " tokens");
        
        // Extract MWEs
        List<MultiWordExpression> mwes = extractor.extractMWEs(tokens);
        
        // Set source URL for all MWEs
        for (MultiWordExpression mwe : mwes) {
            mwe.setSourceUrl(sourceUrl);
        }
        
        System.out.println("Found " + mwes.size() + " MWEs");
        return mwes;
    }
    
    /**
     * Extract MWEs from multiple random German Wikipedia articles
     */
    public List<MultiWordExpression> extractFromRandomArticles(int articleCount) {
        System.out.println("Extracting MWEs from " + articleCount + " random German Wikipedia articles...");
        
        List<String> articles = scraper.getRandomGermanArticles(articleCount);
        List<MultiWordExpression> allMWEs = new ArrayList<>();
        
        for (int i = 0; i < articles.size(); i++) {
            System.out.println("Processing article " + (i + 1) + "/" + articles.size());
            String url = "https://de.wikipedia.org/wiki/Random_Article_" + i;
            List<MultiWordExpression> mwes = extractFromText(articles.get(i), url);
            allMWEs.addAll(mwes);
        }
        
        return allMWEs;
    }
    
    /**
     * Search for articles and extract MWEs
     */
    public List<MultiWordExpression> extractFromSearch(String searchTerm, int maxArticles) {
        System.out.println("Searching for articles containing: " + searchTerm);
        
        List<String> articles = scraper.searchGermanArticles(searchTerm, maxArticles);
        List<MultiWordExpression> allMWEs = new ArrayList<>();
        
        for (int i = 0; i < articles.size(); i++) {
            System.out.println("Processing search result " + (i + 1) + "/" + articles.size());
            String url = "https://de.wikipedia.org/wiki/Search_Result_" + i;
            List<MultiWordExpression> mwes = extractFromText(articles.get(i), url);
            allMWEs.addAll(mwes);
        }
        
        return allMWEs;
    }
    
    /**
     * Extract MWEs from a specific Wikipedia category
     */
    public List<MultiWordExpression> extractFromCategory(String categoryName, int maxArticles) {
        System.out.println("Extracting MWEs from category: " + categoryName);
        
        List<String> articles = scraper.getArticlesFromCategory(categoryName, maxArticles);
        List<MultiWordExpression> allMWEs = new ArrayList<>();
        
        for (int i = 0; i < articles.size(); i++) {
            System.out.println("Processing category article " + (i + 1) + "/" + articles.size());
            String url = "https://de.wikipedia.org/wiki/Category_" + categoryName + "_" + i;
            List<MultiWordExpression> mwes = extractFromText(articles.get(i), url);
            allMWEs.addAll(mwes);
        }
        
        return allMWEs;
    }
    
    /**
     * Analyze and print statistics about extracted MWEs
     */
    public void printMWEStatistics(List<MultiWordExpression> mwes) {
        if (mwes.isEmpty()) {
            System.out.println("No MWEs found.");
            return;
        }
        
        System.out.println("\n=== MWE EXTRACTION STATISTICS ===");
        System.out.println("Total MWEs found: " + mwes.size());
        
        // Group by type
        Map<MultiWordExpression.MWEType, List<MultiWordExpression>> byType = 
            mwes.stream().collect(Collectors.groupingBy(MultiWordExpression::getType));
        
        System.out.println("\nBy Type:");
        for (MultiWordExpression.MWEType type : byType.keySet()) {
            System.out.println("  " + type + ": " + byType.get(type).size());
        }
        
        // Average confidence
        double avgConfidence = mwes.stream()
            .mapToDouble(MultiWordExpression::getConfidence)
            .average()
            .orElse(0.0);
        System.out.println("\nAverage confidence: " + String.format("%.2f", avgConfidence));
        
        // Length distribution
        Map<Integer, Long> lengthDistribution = mwes.stream()
            .collect(Collectors.groupingBy(MultiWordExpression::getLength, Collectors.counting()));
        
        System.out.println("\nLength distribution:");
        for (Map.Entry<Integer, Long> entry : lengthDistribution.entrySet()) {
            System.out.println("  " + entry.getKey() + " words: " + entry.getValue() + " MWEs");
        }
        
        // Top MWEs by confidence
        System.out.println("\nTop 10 MWEs by confidence:");
        mwes.stream()
            .sorted((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()))
            .limit(10)
            .forEach(mwe -> System.out.println("  " + mwe.getExpression() + 
                " (" + mwe.getType() + ", conf: " + String.format("%.2f", mwe.getConfidence()) + ")"));
    }
    
    /**
     * Export MWEs to various formats
     */
    public void exportMWEs(List<MultiWordExpression> mwes, String filename, String format) {
        try {
            switch (format.toLowerCase()) {
                case "json":
                    formatter.exportToJSON(mwes, filename + ".json");
                    break;
                case "csv":
                    formatter.exportToCSV(mwes, filename + ".csv");
                    break;
                case "txt":
                    formatter.exportToText(mwes, filename + ".txt");
                    break;
                default:
                    System.err.println("Unsupported format: " + format);
                    return;
            }
            System.out.println("MWEs exported to " + filename + "." + format);
        } catch (Exception e) {
            System.err.println("Error exporting MWEs: " + e.getMessage());
        }
    }
    
    /**
     * Main method with example usage
     */
    public static void main(String[] args) {
        try {
            GermanMWEExtractorApp app = new GermanMWEExtractorApp();
            
            // Configure extraction parameters
            app.extractor.setMinConfidence(0.5);
            app.extractor.setMaxMWELength(4);
            app.scraper.setRequestDelay(1500); // Be respectful to Wikipedia
            
            if (args.length > 0) {
                // Command line usage
                handleCommandLineArgs(app, args);
            } else {
                // Example usage
                runExamples(app);
            }
            
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void handleCommandLineArgs(GermanMWEExtractorApp app, String[] args) {
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "url":
                if (args.length > 1) {
                    List<MultiWordExpression> urlMwes = app.extractFromUrl(args[1]);
                    app.printMWEStatistics(urlMwes);
                    if (args.length > 2) {
                        app.exportMWEs(urlMwes, args[2], "json");
                    }
                }
                break;
            case "title":
                if (args.length > 1) {
                    List<MultiWordExpression> titleMwes = app.extractFromTitle(args[1]);
                    app.printMWEStatistics(titleMwes);
                    if (args.length > 2) {
                        app.exportMWEs(titleMwes, args[2], "json");
                    }
                }
                break;
            case "search":
                if (args.length > 1) {
                    int maxArticles = args.length > 2 ? Integer.parseInt(args[2]) : 5;
                    List<MultiWordExpression> searchMwes = app.extractFromSearch(args[1], maxArticles);
                    app.printMWEStatistics(searchMwes);
                    if (args.length > 3) {
                        app.exportMWEs(searchMwes, args[3], "json");
                    }
                }
                break;
            case "random":
                int count = args.length > 1 ? Integer.parseInt(args[1]) : 3;
                List<MultiWordExpression> randomMwes = app.extractFromRandomArticles(count);
                app.printMWEStatistics(randomMwes);
                if (args.length > 2) {
                    app.exportMWEs(randomMwes, args[2], "json");
                }
                break;
            case "category":
                if (args.length > 1) {
                    int maxArticles = args.length > 2 ? Integer.parseInt(args[2]) : 5;
                    List<MultiWordExpression> categoryMwes = app.extractFromCategory(args[1], maxArticles);
                    app.printMWEStatistics(categoryMwes);
                    if (args.length > 3) {
                        app.exportMWEs(categoryMwes, args[3], "json");
                    }
                }
                break;
            default:
                printUsage();
        }
    }
    
    private static void runExamples(GermanMWEExtractorApp app) {
        System.out.println("Running example extractions...\n");
        
        // Example 1: Extract from a specific article
        System.out.println("=== Example 1: Extract from specific article ===");
        List<MultiWordExpression> mwes1 = app.extractFromTitle("Deutsche_Sprache");
        app.printMWEStatistics(mwes1);
        
        // Example 2: Extract from search results
        System.out.println("\n=== Example 2: Extract from search results ===");
        List<MultiWordExpression> mwes2 = app.extractFromSearch("Bundeskanzler", 2);
        app.printMWEStatistics(mwes2);
        
        // Combine results and export
        List<MultiWordExpression> allMWEs = new ArrayList<>();
        allMWEs.addAll(mwes1);
        allMWEs.addAll(mwes2);
        
        app.exportMWEs(allMWEs, "german_mwes_sample", "json");
        app.exportMWEs(allMWEs, "german_mwes_sample", "csv");
    }
    
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java GermanMWEExtractorApp url <wikipedia_url> [output_file]");
        System.out.println("  java GermanMWEExtractorApp title <article_title> [output_file]");
        System.out.println("  java GermanMWEExtractorApp search <search_term> [max_articles] [output_file]");
        System.out.println("  java GermanMWEExtractorApp random [article_count] [output_file]");
        System.out.println("  java GermanMWEExtractorApp category <category_name> [max_articles] [output_file]");
    }
}