package group6.java.xml;

import group6.java.model.LinguisticToken;
import group6.java.model.SearchResult;
import group6.java.model.SearchHistoryEntry;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XMLExporter {

    /**
     * Export all linguistic tokens to XML
     * @param tokens List of linguistic tokens
     * @param originalText Original source text
     * @param source Source information (file path or URL)
     * @param outputPath Output XML file path
     * @throws IOException If file writing fails
     */
    public static void exportTokensToXML(List<LinguisticToken> tokens, String originalText,
                                         String source, String outputPath) throws IOException {

        Document doc = new Document();
        Element root = new Element("linguistic-analysis");
        doc.setRootElement(root);

        // Add metadata
        Element metadata = new Element("metadata");
        metadata.addContent(new Element("timestamp").setText(getCurrentTimestamp()));
        metadata.addContent(new Element("source").setText(source));
        metadata.addContent(new Element("total-tokens").setText(String.valueOf(tokens.size())));
        root.addContent(metadata);

        // Add original text (truncated if too long)
        Element originalTextElement = new Element("original-text");
        String textToSave = originalText.length() > 1000 ? originalText.substring(0, 1000) + "..." : originalText;
        originalTextElement.setText(textToSave);
        root.addContent(originalTextElement);

        // Add tokens
        Element tokensElement = new Element("tokens");
        for (LinguisticToken token : tokens) {
            Element tokenElement = new Element("token");
            tokenElement.setAttribute("sentence-index", String.valueOf(token.getSentenceIndex()));
            tokenElement.setAttribute("token-index", String.valueOf(token.getTokenIndex()));
            tokenElement.addContent(new Element("word").setText(token.getWord()));
            tokenElement.addContent(new Element("lemma").setText(token.getLemma()));
            tokenElement.addContent(new Element("pos-tag").setText(token.getPosTag()));
            tokenElement.addContent(new Element("sentence").setText(token.getSentence()));
            tokensElement.addContent(tokenElement);
        }
        root.addContent(tokensElement);

        // Write to file
        writeXMLToFile(doc, outputPath);
    }

    /**
     * Export search results to XML
     * @param searchResult Search result object
     * @param outputPath Output XML file path
     * @throws IOException If file writing fails
     */
    public static void exportSearchResultToXML(SearchResult searchResult, String outputPath) throws IOException {
        Document doc = new Document();
        Element root = new Element("search-results");
        doc.setRootElement(root);

        // Add search metadata
        Element metadata = new Element("search-metadata");
        metadata.addContent(new Element("timestamp").setText(getCurrentTimestamp()));
        metadata.addContent(new Element("search-term").setText(searchResult.getSearchTerm()));
        metadata.addContent(new Element("search-type").setText(searchResult.getSearchType()));
        metadata.addContent(new Element("total-matches").setText(String.valueOf(searchResult.getTotalMatches())));
        root.addContent(metadata);

        // Add matches
        Element matchesElement = new Element("matches");
        for (LinguisticToken token : searchResult.getMatches()) {
            Element matchElement = new Element("match");
            matchElement.setAttribute("sentence-index", String.valueOf(token.getSentenceIndex()));
            matchElement.setAttribute("token-index", String.valueOf(token.getTokenIndex()));
            matchElement.addContent(new Element("word").setText(token.getWord()));
            matchElement.addContent(new Element("lemma").setText(token.getLemma()));
            matchElement.addContent(new Element("pos-tag").setText(token.getPosTag()));
            matchElement.addContent(new Element("sentence").setText(token.getSentence()));
            matchesElement.addContent(matchElement);
        }
        root.addContent(matchesElement);

        // Write to file
        writeXMLToFile(doc, outputPath);
    }

    /**
     * Export search history to XML
     * @param searchHistory List of search history entries
     * @param sourceInfo Information about the source document
     * @param outputPath Output XML file path
     * @throws IOException If file writing fails
     */
    public static void exportSearchHistoryToXML(List<SearchHistoryEntry> searchHistory,
                                                String sourceInfo, String outputPath) throws IOException {
        Element root = new Element("search-history");
        Document doc = new Document(root);

        // Add metadata
        Element metadata = new Element("metadata");
        metadata.addContent(new Element("export-timestamp").setText(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        metadata.addContent(new Element("source-document").setText(sourceInfo != null ? sourceInfo : "Unknown"));
        metadata.addContent(new Element("total-searches").setText(String.valueOf(searchHistory.size())));
        root.addContent(metadata);

        // Add search entries
        Element searchesElement = new Element("searches");
        for (SearchHistoryEntry entry : searchHistory) {
            Element searchElement = new Element("search");

            searchElement.addContent(new Element("search-term").setText(entry.getSearchTerm()));
            searchElement.addContent(new Element("search-type").setText(entry.getSearchType()));
            searchElement.addContent(new Element("result-count").setText(String.valueOf(entry.getResultCount())));
            searchElement.addContent(new Element("timestamp").setText(entry.getFormattedTimestamp()));
            searchElement.addContent(new Element("source-info").setText(entry.getSourceInfo()));

            searchesElement.addContent(searchElement);
        }

        root.addContent(searchesElement);

        // Add statistics
        Element statistics = new Element("statistics");

        // Count searches by type
        long wordSearches = searchHistory.stream().filter(e -> "Word".equals(e.getSearchType())).count();
        long lemmaSearches = searchHistory.stream().filter(e -> "Lemma".equals(e.getSearchType())).count();
        long posSearches = searchHistory.stream().filter(e -> "POS Tag".equals(e.getSearchType())).count();

        statistics.addContent(new Element("word-searches").setText(String.valueOf(wordSearches)));
        statistics.addContent(new Element("lemma-searches").setText(String.valueOf(lemmaSearches)));
        statistics.addContent(new Element("pos-searches").setText(String.valueOf(posSearches)));

        // Total matches found
        int totalMatches = searchHistory.stream().mapToInt(SearchHistoryEntry::getResultCount).sum();
        statistics.addContent(new Element("total-matches-found").setText(String.valueOf(totalMatches)));

        root.addContent(statistics);

        // Write to file
        writeXMLToFile(doc, outputPath);
    }

    /**
     * Write XML document to file
     * @param doc XML document
     * @param outputPath Output file path
     * @throws IOException If file writing fails
     */
    private static void writeXMLToFile(Document doc, String outputPath) throws IOException {
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());

        try (FileWriter writer = new FileWriter(outputPath)) {
            outputter.output(doc, writer);
        }
    }

    /**
     * Get current timestamp as string
     * @return Formatted timestamp
     */
    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * Generate default output filename based on search type and term
     * @param searchTerm Search term
     * @param searchType Search type
     * @return Generated filename
     */
    public static String generateOutputFileName(String searchTerm, String searchType) {
        String sanitizedTerm = searchTerm.replaceAll("[^a-zA-Z0-9]", "_");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return String.format("search_%s_%s_%s.xml", searchType, sanitizedTerm, timestamp);
    }

    /**
     * Generate default output filename for full analysis
     * @param source Source information
     * @return Generated filename
     */
    public static String generateAnalysisFileName(String source) {
        String sanitizedSource = source.replaceAll("[^a-zA-Z0-9]", "_");
        if (sanitizedSource.length() > 20) {
            sanitizedSource = sanitizedSource.substring(0, 20);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        return String.format("analysis_%s_%s.xml", sanitizedSource, timestamp);
    }
}