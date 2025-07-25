package group6.java.ui;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Helper class to save a list of SearchResult objects into an XML file.
 * This class does NOT show any dialogs or UI elements.
 * It only saves the XML content to the provided file.
 */
public class XmlSaver {

    /**
     * Saves the search history (list of SearchResult) into the specified XML file.
     *
     * @param searchHistory list of SearchResult to save
     * @param fileToSave    File object where the XML will be saved
     * @throws Exception if any error occurs during saving
     */
    public static void saveResultsToXML(List<SearchResult> searchHistory, File fileToSave) throws Exception {
        if (searchHistory == null || searchHistory.isEmpty()) {
            throw new IllegalArgumentException("No search results to save.");
        }
        if (fileToSave == null) {
            throw new IllegalArgumentException("File to save cannot be null.");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("searchResults");
        doc.appendChild(rootElement);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (SearchResult result : searchHistory) {
            Element resultElem = doc.createElement("searchResult");

            Element query = doc.createElement("query");
            query.appendChild(doc.createTextNode(result.getQuery() != null ? result.getQuery() : ""));
            resultElem.appendChild(query);

            Element type = doc.createElement("type");
            type.appendChild(doc.createTextNode(result.getSearchType() != null ? result.getSearchType() : ""));
            resultElem.appendChild(type);

            Element timestamp = doc.createElement("timestamp");
            timestamp.appendChild(doc.createTextNode(
                    result.getTimestamp() != null ? result.getTimestamp().format(formatter) : ""));
            resultElem.appendChild(timestamp);

            Element matches = doc.createElement("matches");
            if (result.getMatches() != null) {
                for (String match : result.getMatches()) {
                    Element matchElem = doc.createElement("match");
                    matchElem.appendChild(doc.createTextNode(match != null ? match : ""));
                    matches.appendChild(matchElem);
                }
            }
            resultElem.appendChild(matches);

            rootElement.appendChild(resultElem);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(fileToSave);

        transformer.transform(source, streamResult);
    }
}
