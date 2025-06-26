package ui;

import org.w3c.dom.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

/**
 * Helper class to save a list of SearchResult objects into an XML file.
 */
public class XmlSaver {

    /**
     * Saves the search history (list of SearchResult) into an XML file.
     * Opens a file dialog for the user to pick where to save.
     * If there’s nothing to save or user cancels, it just exits.
     *
     * @param searchHistory list of SearchResult to save
     */
    public static void saveResultsToXML(List<SearchResult> searchHistory) {
        // Nothing to save? Show a message and stop
        if (searchHistory == null || searchHistory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No search results to save.");
            return;
        }

        // Let the user pick a file location
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save XML File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Make sure the filename ends with .xml
            if (!fileToSave.getAbsolutePath().endsWith(".xml")) {
                fileToSave = new File(fileToSave + ".xml");
            }

            try {
                // Prepare to build an XML document
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();

                // Root element for all results
                Element rootElement = doc.createElement("searchResults");
                doc.appendChild(rootElement);

                // Add each SearchResult as a separate XML element
                for (SearchResult result : searchHistory) {
                    Element resultElem = doc.createElement("searchResult");

                    // Add the query string
                    Element query = doc.createElement("query");
                    query.appendChild(doc.createTextNode(result.getQuery()));
                    resultElem.appendChild(query);

                    // Add the type of search
                    Element type = doc.createElement("type");
                    type.appendChild(doc.createTextNode(result.getSearchType()));
                    resultElem.appendChild(type);

                    // Add the timestamp as a string
                    Element timestamp = doc.createElement("timestamp");
                    timestamp.appendChild(doc.createTextNode(result.getTimestamp().toString()));
                    resultElem.appendChild(timestamp);

                    // Container for all matches
                    Element matches = doc.createElement("matches");

                    // Add each match inside the container
                    for (String match : result.getMatches()) {
                        Element matchElem = doc.createElement("match");
                        matchElem.appendChild(doc.createTextNode(match));
                        matches.appendChild(matchElem);
                    }

                    // Put matches inside the searchResult element
                    resultElem.appendChild(matches);

                    // Add the whole searchResult element to root
                    rootElement.appendChild(resultElem);
                }

                // Set up transformer to write XML to file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(fileToSave);

                // Write the XML content to file
                transformer.transform(source, result);

                // Let the user know the save was successful
                JOptionPane.showMessageDialog(null, "Results saved to: " + fileToSave.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving XML:\n" + e.getMessage());
            }
        }
    }
}
