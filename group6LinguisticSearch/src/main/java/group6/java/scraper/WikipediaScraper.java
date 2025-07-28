package group6.java.scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaScraper {
     public static String scrape(String url) {
        StringBuilder text = new StringBuilder();
        try {
             Document doc = Jsoup.connect(url).get();
             Element content = doc.selectFirst("#mw-content-text .mw-parser-output");
             if (content == null) {
                 return "";
             }
             // Remove irrelevant elements
             content.select("sup.reference, .reflist, table, .infobox, .navbox, .metadata, style, script").remove();
             // Extract paragraphs
            Elements paragraphs = content.select("p");
            for (Element p : paragraphs) {
                String para = p.text().trim();
                // Filter out meaningless contents
                if (para.length() > 20 && !para.matches("^[\\s\\p{P}\\d]*$")) {
                    text.append(para);
                     text.append("\n\n");
                     }
            }
            } catch (Exception e) {
                System.err.println("Error fetching Wikipedia: " + e.getMessage());
            }

        return text.toString();
     }
}