package group6.java.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Specialized scraper for German Wikipedia articles
 * Handles German-specific content and provides methods for systematic article collection
 */
public class GermanWikipediaScraper {
    
    private static final String GERMAN_WIKIPEDIA_BASE = "https://de.wikipedia.org";
    private static final String GERMAN_WIKIPEDIA_API = "https://de.wikipedia.org/w/api.php";
    private static final String USER_AGENT = "Mozilla/5.0 (German MWE Extractor Bot 1.0)";
    
    // German-specific content filters
    private static final Pattern GERMAN_TEXT_PATTERN = Pattern.compile("[a-züäöß]", Pattern.CASE_INSENSITIVE);
    private static final Set<String> GERMAN_STOP_CATEGORIES = Set.of(
        "Disambiguierung", "Begriffsklärung", "Weiterleitung", "Liste", 
        "Kategorie", "Portal", "Vorlage", "Projekt"
    );
    
    private int requestDelay = 1000; // Delay between requests in milliseconds
    private int maxRetries = 3;
    
    /**
     * Scrape a single German Wikipedia article by URL
     */
    public String scrapeArticle(String url) {
        StringBuilder text = new StringBuilder();
        
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            
            // Check if this is a valid German Wikipedia article
            if (!isValidGermanArticle(doc)) {
                System.out.println("Skipping non-German or invalid article: " + url);
                return "";
            }
            
            Element content = doc.selectFirst("#mw-content-text .mw-parser-output");
            if (content == null) {
                return "";
            }
            
            // Remove unwanted elements specific to German Wikipedia
            removeUnwantedElements(content);
            
            // Extract main content paragraphs
            Elements paragraphs = content.select("p");
            for (Element p : paragraphs) {
                String para = p.text().trim();
                
                // Filter paragraphs - must contain German text and be substantive
                if (isValidGermanParagraph(para)) {
                    text.append(para);
                    text.append("\n\n");
                }
            }
            
            // Also extract content from some lists and tables that might contain relevant text
            Elements lists = content.select("ul li, ol li");
            for (Element li : lists) {
                String listItem = li.text().trim();
                if (isValidGermanParagraph(listItem) && listItem.length() > 30) {
                    text.append(listItem);
                    text.append("\n");
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error fetching German Wikipedia article: " + url + " - " + e.getMessage());
        }
        
        return text.toString();
    }
    
    /**
     * Scrape a German Wikipedia article by title
     */
    public String scrapeArticleByTitle(String title) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = GERMAN_WIKIPEDIA_BASE + "/wiki/" + encodedTitle;
            return scrapeArticle(url);
        } catch (Exception e) {
            System.err.println("Error creating URL for title: " + title + " - " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Get random German Wikipedia articles
     */
    public List<String> getRandomGermanArticles(int count) {
        List<String> articles = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            try {
                String url = GERMAN_WIKIPEDIA_BASE + "/wiki/Spezial:Zufällige_Seite";
                String content = scrapeArticle(url);
                
                if (!content.trim().isEmpty()) {
                    articles.add(content);
                }
                
                // Be respectful to Wikipedia servers
                Thread.sleep(requestDelay);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error getting random article: " + e.getMessage());
            }
        }
        
        return articles;
    }
    
    /**
     * Search for German Wikipedia articles by topic
     */
    public List<String> searchGermanArticles(String searchTerm, int maxResults) {
        List<String> articleTitles = searchArticleTitles(searchTerm, maxResults);
        List<String> articles = new ArrayList<>();
        
        for (String title : articleTitles) {
            String content = scrapeArticleByTitle(title);
            if (!content.trim().isEmpty()) {
                articles.add(content);
            }
            
            try {
                Thread.sleep(requestDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return articles;
    }
    
    /**
     * Search for article titles using Wikipedia API
     */
    private List<String> searchArticleTitles(String searchTerm, int maxResults) {
        List<String> titles = new ArrayList<>();
        
        try {
            String encodedTerm = URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
            String apiUrl = GERMAN_WIKIPEDIA_API + 
                "?action=query&format=json&list=search&srsearch=" + encodedTerm + 
                "&srlimit=" + Math.min(maxResults, 50); // Limit to prevent abuse
            
            Document doc = Jsoup.connect(apiUrl)
                    .userAgent(USER_AGENT)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .get();
            
            String jsonResponse = doc.text();
            // Simple JSON parsing for titles (could be improved with proper JSON library)
            String[] parts = jsonResponse.split("\"title\":\"");
            for (int i = 1; i < parts.length && titles.size() < maxResults; i++) {
                String title = parts[i].split("\"")[0];
                if (isValidArticleTitle(title)) {
                    titles.add(title);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error searching Wikipedia articles: " + e.getMessage());
        }
        
        return titles;
    }
    
    /**
     * Get articles from a specific German Wikipedia category
     */
    public List<String> getArticlesFromCategory(String categoryName, int maxArticles) {
        List<String> articles = new ArrayList<>();
        
        try {
            String encodedCategory = URLEncoder.encode(categoryName, StandardCharsets.UTF_8);
            String categoryUrl = GERMAN_WIKIPEDIA_BASE + "/wiki/Kategorie:" + encodedCategory;
            
            Document doc = Jsoup.connect(categoryUrl)
                    .userAgent(USER_AGENT)
                    .timeout(10000)
                    .get();
            
            Elements links = doc.select("#mw-pages .mw-category-group ul li a");
            
            for (Element link : links) {
                if (articles.size() >= maxArticles) break;
                
                String title = link.attr("title");
                String content = scrapeArticleByTitle(title);
                
                if (!content.trim().isEmpty()) {
                    articles.add(content);
                }
                
                try {
                    Thread.sleep(requestDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error getting articles from category: " + categoryName + " - " + e.getMessage());
        }
        
        return articles;
    }
    
    /**
     * Check if the document represents a valid German article
     */
    private boolean isValidGermanArticle(Document doc) {
        // Check if it's actually German Wikipedia
        if (!doc.location().contains("de.wikipedia.org")) {
            return false;
        }
        
        // Check if it's a content article (not a special page)
        Elements categories = doc.select(".mw-normal-catlinks ul li a");
        for (Element category : categories) {
            String categoryText = category.text();
            for (String stopCategory : GERMAN_STOP_CATEGORIES) {
                if (categoryText.contains(stopCategory)) {
                    return false;
                }
            }
        }
        
        // Check if main content contains German text
        Element content = doc.selectFirst("#mw-content-text .mw-parser-output");
        if (content != null) {
            String text = content.text();
            return GERMAN_TEXT_PATTERN.matcher(text).find() && text.length() > 500;
        }
        
        return false;
    }
    
    /**
     * Check if a paragraph is valid German content
     */
    private boolean isValidGermanParagraph(String paragraph) {
        if (paragraph.length() < 20) return false;
        
        // Must contain German characters
        if (!GERMAN_TEXT_PATTERN.matcher(paragraph).find()) return false;
        
        // Filter out meaningless content
        if (paragraph.matches("^[\\s\\p{P}\\d]*$")) return false;
        
        // Filter out common non-content patterns
        if (paragraph.startsWith("→ ") || 
            paragraph.startsWith("Siehe auch") ||
            paragraph.startsWith("Literatur") ||
            paragraph.startsWith("Weblinks") ||
            paragraph.contains("(Begriffsklärung)")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if an article title is valid for content extraction
     */
    private boolean isValidArticleTitle(String title) {
        for (String stopCategory : GERMAN_STOP_CATEGORIES) {
            if (title.contains(stopCategory)) {
                return false;
            }
        }
        
        return !title.startsWith("Liste ") && 
               !title.startsWith("Portal:") && 
               !title.startsWith("Kategorie:") &&
               !title.startsWith("Vorlage:") &&
               !title.contains(":");
    }
    
    /**
     * Remove unwanted elements from Wikipedia content
     */
    private void removeUnwantedElements(Element content) {
        // Remove standard unwanted elements
        content.select("sup.reference, .reflist, .infobox, .navbox, .metadata, " +
                      "style, script, .dablink, .hatnote").remove();
        
        // Remove German Wikipedia specific elements
        content.select(".toc, .toccolours, .navbox-inner, .sidebar, " +
                      ".vertical-navbox, .wikitable").remove();
        
        // Remove coordinate and other metadata
        content.select(".geo, .coordinates, .plainlinks").remove();
        
        // Remove edit links and other interface elements
        content.select(".mw-editsection, .edit-link").remove();
    }
    
    // Getters and setters
    public int getRequestDelay() { return requestDelay; }
    public void setRequestDelay(int requestDelay) { this.requestDelay = requestDelay; }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
}