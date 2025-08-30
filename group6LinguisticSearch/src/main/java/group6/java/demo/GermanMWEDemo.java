package group6.java.demo;

import group6.java.GermanMWEExtractorApp;
import group6.java.model.MultiWordExpression;

import java.util.List;

/**
 * Demonstration class showing German MWE extraction capabilities
 */
public class GermanMWEDemo {
    
    public static void main(String[] args) {
        System.out.println("=== German MWE Extractor Demo ===\n");
        
        try {
            // Initialize the application
            GermanMWEExtractorApp app = new GermanMWEExtractorApp();
            
            // Configure for demo (faster processing)
            app.extractor.setMinConfidence(0.5);
            app.extractor.setMaxMWELength(4);
            app.scraper.setRequestDelay(1000);
            
            runDemoExtractions(app);
            
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void runDemoExtractions(GermanMWEExtractorApp app) {
        System.out.println("Running demonstration extractions...\n");
        
        // Demo 1: Extract from a well-known German article
        demo1_GermanLanguageArticle(app);
        
        // Demo 2: Search for political terms
        demo2_PoliticalTerms(app);
        
        // Demo 3: Process sample German text
        demo3_SampleText(app);
        
        System.out.println("Demo completed successfully!");
    }
    
    private static void demo1_GermanLanguageArticle(GermanMWEExtractorApp app) {
        System.out.println("=== DEMO 1: German Language Article ===");
        System.out.println("Extracting MWEs from 'Deutsche Sprache' Wikipedia article...\n");
        
        List<MultiWordExpression> mwes = app.extractFromTitle("Deutsche_Sprache");
        
        if (mwes.isEmpty()) {
            System.out.println("No MWEs found. This might be due to network issues or model limitations.");
            return;
        }
        
        app.printMWEStatistics(mwes);
        
        System.out.println("\nTop 5 MWEs found:");
        mwes.stream()
            .sorted((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()))
            .limit(5)
            .forEach(mwe -> System.out.println("  \"" + mwe.getExpression() + "\" (" + 
                    mwe.getType() + ", confidence: " + String.format("%.2f", mwe.getConfidence()) + ")"));
        
        // Export results
        try {
            app.exportMWEs(mwes, "demo1_deutsche_sprache", "json");
            System.out.println("\nResults exported to demo1_deutsche_sprache.json");
        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
        }
        
        System.out.println("\n" + "=".repeat(50) + "\n");
    }
    
    private static void demo2_PoliticalTerms(GermanMWEExtractorApp app) {
        System.out.println("=== DEMO 2: Political Terms Search ===");
        System.out.println("Searching for articles about 'Bundestag' and extracting MWEs...\n");
        
        List<MultiWordExpression> mwes = app.extractFromSearch("Bundestag", 2);
        
        if (mwes.isEmpty()) {
            System.out.println("No MWEs found from search results.");
            return;
        }
        
        app.printMWEStatistics(mwes);
        
        System.out.println("\nPolitical MWEs found:");
        mwes.stream()
            .filter(mwe -> mwe.getExpression().toLowerCase().contains("bundes") ||
                          mwe.getExpression().toLowerCase().contains("politik") ||
                          mwe.getExpression().toLowerCase().contains("parlament"))
            .forEach(mwe -> System.out.println("  \"" + mwe.getExpression() + "\" (" + 
                    mwe.getType() + ")"));
        
        System.out.println("\n" + "=".repeat(50) + "\n");
    }
    
    private static void demo3_SampleText(GermanMWEExtractorApp app) {
        System.out.println("=== DEMO 3: Sample German Text ===");
        System.out.println("Processing a sample German text with known MWEs...\n");
        
        String sampleText = "Der Bundeskanzler trifft eine wichtige Entscheidung. " +
                           "Die Europäische Union spielt eine große Rolle in der deutschen Politik. " +
                           "Im Gegensatz zu anderen Ländern hat Deutschland eine föderale Struktur. " +
                           "Der Ministerpräsident und die Bundesregierung arbeiten eng zusammen. " +
                           "Eine schwere Entscheidung steht bevor, wenn es um die Klimapolitik geht. " +
                           "Die Vereinten Nationen haben ihren Sitz in New York.";
        
        System.out.println("Sample text:");
        System.out.println("\"" + sampleText + "\"\n");
        
        List<MultiWordExpression> mwes = app.extractFromText(sampleText, "Sample Text");
        
        if (mwes.isEmpty()) {
            System.out.println("No MWEs found in sample text. This indicates an issue with the processing pipeline.");
            return;
        }
        
        app.printMWEStatistics(mwes);
        
        System.out.println("\nAll MWEs found:");
        mwes.forEach(mwe -> {
            System.out.println("  \"" + mwe.getExpression() + "\"");
            System.out.println("    Type: " + mwe.getType());
            System.out.println("    Confidence: " + String.format("%.2f", mwe.getConfidence()));
            if (!mwe.getExpression().equals(mwe.getLemmatizedForm())) {
                System.out.println("    Lemma: " + mwe.getLemmatizedForm());
            }
            System.out.println();
        });
        
        // Export results
        try {
            app.exportMWEs(mwes, "demo3_sample_text", "txt");
            System.out.println("Results exported to demo3_sample_text.txt");
        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
        }
        
        System.out.println("\n" + "=".repeat(50) + "\n");
    }
}