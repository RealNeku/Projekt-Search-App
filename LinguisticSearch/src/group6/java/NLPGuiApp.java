package group6.java;

import group6.java.scraper.WikipediaScraper;
import group6.java.ui.SearchResult;
import group6.java.ui.XmlSaver;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NLPGuiApp extends JFrame {

    private JTextArea inputTextArea;
    private JButton analyzeButton;
    private JButton saveToXmlButton;
    private JTextArea outputArea;

    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton;
    private JTextArea searchResultsArea;

    private JTextField urlField;
    private JButton loadUrlButton;

    private TextProcessor processor;
    private List<AnnotatedSentence> annotatedSentences;

    private List<SearchResult> searchHistory = new ArrayList<>();

    public NLPGuiApp() {
        super("Linguistic Analyzer GUI");

        try {
            processor = new TextProcessor(
                    "resources/models/en-sent.bin",
                    "resources/models/en-token.bin",
                    "resources/models/en-pos-maxent.bin",
                    "resources/models/en-lemmatizer.bin"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading NLP models: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        setLayout(new BorderLayout(5, 5));

        // URL input panel
        JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
        urlPanel.setBorder(BorderFactory.createTitledBorder("Load Text From URL"));

        urlField = new JTextField();
        loadUrlButton = new JButton("Load Text");

        urlPanel.add(new JLabel("URL: "), BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);
        urlPanel.add(loadUrlButton, BorderLayout.EAST);

        add(urlPanel, BorderLayout.NORTH);

        // input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Text"));
        inputTextArea = new JTextArea(6, 50);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        analyzeButton = new JButton("Analyze");
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        // output panel for annotation
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Annotated Sentences"));
        outputArea = new JTextArea(12, 50);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        searchTypeCombo = new JComboBox<>(new String[]{"word", "lemma", "pos"});
        searchField = new JTextField(15);
        searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search type:"));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(new JLabel("Query:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.SOUTH);

        // search results panel
        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        searchResultsArea = new JTextArea(8, 50);
        searchResultsArea.setEditable(false);
        searchResultsArea.setLineWrap(true);
        searchResultsArea.setWrapStyleWord(true);
        resultsPanel.add(new JScrollPane(searchResultsArea), BorderLayout.CENTER);

        // add left and right panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(inputPanel), new JScrollPane(outputPanel));
        splitPane.setResizeWeight(0.5);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(splitPane, BorderLayout.CENTER);
        centerPanel.add(resultsPanel, BorderLayout.WEST);

        add(centerPanel, BorderLayout.CENTER);

        // XML save button
        saveToXmlButton = new JButton("Save to XML");
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.add(saveToXmlButton);
        add(savePanel, BorderLayout.EAST);

        // action listeners
        analyzeButton.addActionListener(e -> analyzeText());
        searchButton.addActionListener(e -> searchText());
        loadUrlButton.addActionListener(e -> loadTextFromUrl());
        saveToXmlButton.addActionListener(e -> saveResultsToXml());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadTextFromUrl() {
        String urlString = urlField.getText().trim();
        if (urlString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a URL.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!urlString.contains("wikipedia.org")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Wikipedia URL.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String cleanedText = WikipediaScraper.scrape(urlString);

            if (cleanedText == null || cleanedText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No content found or error scraping Wikipedia page.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            inputTextArea.setText(cleanedText);

            // Clear previous outputs and search history
            outputArea.setText("");
            searchResultsArea.setText("");
            annotatedSentences = null;
            searchHistory.clear();

            JOptionPane.showMessageDialog(this, "Text loaded and cleaned from Wikipedia successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading URL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void analyzeText() {
        String inputText = inputTextArea.getText().trim();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to analyze.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            annotatedSentences = processor.processText(inputText);

            StringBuilder sb = new StringBuilder();
            for (AnnotatedSentence ann : annotatedSentences) {
                sb.append("Original Sentence: ").append(ann.syntax.sentence).append("\n");
                sb.append("Tokens:   ").append(String.join(" ", ann.syntax.tokens)).append("\n");
                sb.append("POS Tags: ").append(String.join(" ", ann.pos.posTags)).append("\n");
                sb.append("Lemmas:   ").append(String.join(" ", ann.lemma.lemmas)).append("\n\n");
            }

            outputArea.setText(sb.toString());
            searchResultsArea.setText("");
            searchHistory.clear();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during analysis: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void searchText() {
        if (annotatedSentences == null || annotatedSentences.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please analyze text first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search query.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String type = (String) searchTypeCombo.getSelectedItem();
        List<AnnotatedSentence> results = TextSearcher.search(annotatedSentences, query, type);

        if (results.isEmpty()) {
            searchResultsArea.setText("No matching sentences found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (AnnotatedSentence ann : results) {
                sb.append(ann.syntax.sentence).append("\n");
            }
            searchResultsArea.setText(sb.toString());
        }

        // Add search result to history
        List<String> matchedSentences = new ArrayList<>();
        for (AnnotatedSentence ann : results) {
            matchedSentences.add(ann.syntax.sentence);
        }

        SearchResult searchResult = new SearchResult(query, matchedSentences, type);
        searchHistory.add(searchResult);
    }

    private void saveResultsToXml() {
        if (searchHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to save. Please perform some searches first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Results as XML");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure .xml extension
            if (!fileToSave.getName().toLowerCase().endsWith(".xml")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xml");
            }
            // Confirm overwrite if file exists
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "The file exists, overwrite?", "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                XmlSaver.saveResultsToXML(searchHistory, fileToSave);
                JOptionPane.showMessageDialog(this, "Results saved to XML successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving XML: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NLPGuiApp::new);
    }
}
