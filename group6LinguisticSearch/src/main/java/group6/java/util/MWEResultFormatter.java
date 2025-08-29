package group6.java.util;

import group6.java.model.MultiWordExpression;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for formatting and exporting MWE extraction results
 */
public class MWEResultFormatter {
    
    private Gson gson;
    
    public MWEResultFormatter() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * Export MWEs to JSON format
     */
    public void exportToJSON(List<MultiWordExpression> mwes, String filename) throws IOException {
        ExportData exportData = new ExportData();
        exportData.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        exportData.totalMWEs = mwes.size();
        exportData.mwes = mwes;
        exportData.statistics = generateStatistics(mwes);
        
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(exportData, writer);
        }
    }
    
    /**
     * Export MWEs to CSV format
     */
    public void exportToCSV(List<MultiWordExpression> mwes, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("Expression,Lemmatized_Form,Type,Confidence,Length,Source_URL\n");
            
            // Write data
            for (MultiWordExpression mwe : mwes) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",%.3f,%d,\"%s\"\n",
                    escapeCSV(mwe.getExpression()),
                    escapeCSV(mwe.getLemmatizedForm()),
                    mwe.getType().toString(),
                    mwe.getConfidence(),
                    mwe.getLength(),
                    escapeCSV(mwe.getSourceUrl() != null ? mwe.getSourceUrl() : "")
                ));
            }
        }
    }
    
    /**
     * Export MWEs to plain text format
     */
    public void exportToText(List<MultiWordExpression> mwes, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.write("German Multiword Expression Extraction Results\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
            writer.write("Total MWEs: " + mwes.size() + "\n");
            writer.write("=" + "=".repeat(50) + "\n\n");
            
            // Group by type
            Map<MultiWordExpression.MWEType, List<MultiWordExpression>> byType = 
                mwes.stream().collect(Collectors.groupingBy(MultiWordExpression::getType));
            
            for (MultiWordExpression.MWEType type : byType.keySet()) {
                writer.write(type.toString() + " (" + byType.get(type).size() + " expressions)\n");
                writer.write("-" + "-".repeat(40) + "\n");
                
                // Sort by confidence
                List<MultiWordExpression> sortedMWEs = byType.get(type).stream()
                    .sorted((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()))
                    .collect(Collectors.toList());
                
                for (MultiWordExpression mwe : sortedMWEs) {
                    writer.write(String.format("  %-30s (conf: %.2f)\n", 
                        mwe.getExpression(), mwe.getConfidence()));
                    if (!mwe.getExpression().equals(mwe.getLemmatizedForm())) {
                        writer.write(String.format("    Lemma: %s\n", mwe.getLemmatizedForm()));
                    }
                    if (mwe.getSourceUrl() != null && !mwe.getSourceUrl().isEmpty()) {
                        writer.write(String.format("    Source: %s\n", mwe.getSourceUrl()));
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }
            
            // Write statistics
            writer.write("\nSTATISTICS\n");
            writer.write("=" + "=".repeat(20) + "\n");
            Statistics stats = generateStatistics(mwes);
            writer.write("Average confidence: " + String.format("%.3f", stats.averageConfidence) + "\n");
            writer.write("Average length: " + String.format("%.1f", stats.averageLength) + "\n");
            writer.write("Most common type: " + stats.mostCommonType + "\n");
            
            writer.write("\nLength distribution:\n");
            for (Map.Entry<Integer, Long> entry : stats.lengthDistribution.entrySet()) {
                writer.write(String.format("  %d words: %d MWEs\n", entry.getKey(), entry.getValue()));
            }
        }
    }
    
    /**
     * Generate detailed statistics about the MWEs
     */
    private Statistics generateStatistics(List<MultiWordExpression> mwes) {
        Statistics stats = new Statistics();
        
        if (mwes.isEmpty()) {
            return stats;
        }
        
        // Basic statistics
        stats.totalMWEs = mwes.size();
        stats.averageConfidence = mwes.stream()
            .mapToDouble(MultiWordExpression::getConfidence)
            .average()
            .orElse(0.0);
        
        stats.averageLength = mwes.stream()
            .mapToInt(MultiWordExpression::getLength)
            .average()
            .orElse(0.0);
        
        // Type distribution
        stats.typeDistribution = mwes.stream()
            .collect(Collectors.groupingBy(
                MultiWordExpression::getType, 
                Collectors.counting()
            ));
        
        // Find most common type
        stats.mostCommonType = stats.typeDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey().toString())
            .orElse("UNKNOWN");
        
        // Length distribution
        stats.lengthDistribution = mwes.stream()
            .collect(Collectors.groupingBy(
                MultiWordExpression::getLength, 
                Collectors.counting()
            ));
        
        // Confidence distribution
        stats.highConfidenceMWEs = mwes.stream()
            .filter(mwe -> mwe.getConfidence() > 0.8)
            .count();
        
        stats.mediumConfidenceMWEs = mwes.stream()
            .filter(mwe -> mwe.getConfidence() > 0.5 && mwe.getConfidence() <= 0.8)
            .count();
        
        stats.lowConfidenceMWEs = mwes.stream()
            .filter(mwe -> mwe.getConfidence() <= 0.5)
            .count();
        
        return stats;
    }
    
    /**
     * Escape CSV special characters
     */
    private String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
    
    /**
     * Generate a detailed report string
     */
    public String generateReport(List<MultiWordExpression> mwes) {
        StringBuilder report = new StringBuilder();
        
        report.append("GERMAN MWE EXTRACTION REPORT\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        
        Statistics stats = generateStatistics(mwes);
        
        report.append("SUMMARY\n");
        report.append("-------\n");
        report.append("Total MWEs found: ").append(stats.totalMWEs).append("\n");
        report.append("Average confidence: ").append(String.format("%.3f", stats.averageConfidence)).append("\n");
        report.append("Average length: ").append(String.format("%.1f words", stats.averageLength)).append("\n");
        report.append("Most common type: ").append(stats.mostCommonType).append("\n\n");
        
        report.append("TYPE DISTRIBUTION\n");
        report.append("----------------\n");
        for (Map.Entry<MultiWordExpression.MWEType, Long> entry : stats.typeDistribution.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / stats.totalMWEs;
            report.append(String.format("%-25s: %3d (%5.1f%%)\n", 
                entry.getKey(), entry.getValue(), percentage));
        }
        
        report.append("\nCONFIDENCE DISTRIBUTION\n");
        report.append("----------------------\n");
        report.append(String.format("High (>0.8):   %3d (%5.1f%%)\n", 
            stats.highConfidenceMWEs, 
            (stats.highConfidenceMWEs * 100.0) / stats.totalMWEs));
        report.append(String.format("Medium (0.5-0.8): %3d (%5.1f%%)\n", 
            stats.mediumConfidenceMWEs, 
            (stats.mediumConfidenceMWEs * 100.0) / stats.totalMWEs));
        report.append(String.format("Low (≤0.5):   %3d (%5.1f%%)\n", 
            stats.lowConfidenceMWEs, 
            (stats.lowConfidenceMWEs * 100.0) / stats.totalMWEs));
        
        return report.toString();
    }
    
    // Data classes for JSON export
    private static class ExportData {
        String timestamp;
        int totalMWEs;
        List<MultiWordExpression> mwes;
        Statistics statistics;
    }
    
    private static class Statistics {
        int totalMWEs;
        double averageConfidence;
        double averageLength;
        String mostCommonType;
        Map<MultiWordExpression.MWEType, Long> typeDistribution;
        Map<Integer, Long> lengthDistribution;
        long highConfidenceMWEs;
        long mediumConfidenceMWEs;
        long lowConfidenceMWEs;
    }
}