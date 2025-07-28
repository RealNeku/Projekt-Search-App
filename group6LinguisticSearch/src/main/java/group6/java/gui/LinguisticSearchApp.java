package group6.java.gui;
import group6.java.model.LinguisticToken;
import group6.java.model.SearchResult;
import group6.java.model.SearchHistoryEntry;
import group6.java.nlp.OpenNLPProcessor;
import group6.java.scraper.InputHandler;
import group6.java.xml.XMLExporter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI application for linguistic search
 */
public class LinguisticSearchApp extends JFrame {
    private OpenNLPProcessor nlpProcessor;
    private List<LinguisticToken> currentTokens;
    private String currentSource;
    private String currentText;

// GUI Components
    private JRadioButton webRadio, fileRadio;
    private JTextField sourceField;
    private JButton browseButton, loadButton;
    private JTextPane sourceTextPane;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton, clearHighlightsButton, exportAnalysisButton, exportSearchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private Highlighter.HighlightPainter highlightPainter;

    // Search History Components
    private List<SearchHistoryEntry> searchHistory;
    private JList<SearchHistoryEntry> historyList;
    private DefaultListModel<SearchHistoryEntry> historyListModel;
    private JButton clearHistoryButton, saveHistoryButton;

       // Match count display
    private JLabel matchCountLabel;

    public LinguisticSearchApp() {
        try {
            nlpProcessor = new OpenNLPProcessor();
            highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
            searchHistory = new ArrayList<>();
            initializeGUI();
            } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error initializing OpenNLP: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            }

        setupEventHandlers();
    }

    private void initializeGUI() {
        setTitle("Linguistic Search Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels
        JPanel topPanel = createSourcePanel();
        JPanel centerPanel = createMainPanel();
        JPanel bottomPanel = createStatusPanel();

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1000, 700));
        pack();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int centerWidth = getWidth() - 300;
                resizeSearchPanelFonts(centerWidth);
            }
        });

        setLocationRelativeTo(null);
    }

    private JPanel createSourcePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Source Selection"));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Radio buttons for source type
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        webRadio = new JRadioButton("Wikipedia URL", true);
        fileRadio = new JRadioButton("Local File");
        ButtonGroup group = new ButtonGroup();
        group.add(webRadio);
        group.add(fileRadio);
        radioPanel.add(webRadio);
        radioPanel.add(fileRadio);

        // Source input
        JPanel inputPanel = new JPanel(new BorderLayout());
        sourceField = new JTextField();
        sourceField.setPreferredSize(new Dimension(400, 25));
        browseButton = new JButton("Browse");
        browseButton.setEnabled(false);
        loadButton = new JButton("Load & Analyze");
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(browseButton);
        buttonPanel.add(loadButton);

        inputPanel.add(sourceField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(radioPanel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Initialize the table model
        String[] columnNames = {"Word", "Lemma", "POS Tag", "Sentence Context"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Left panel: Source Text
        sourceTextPane = new JTextPane();
        sourceTextPane.setEditable(false);
        JScrollPane sourceScrollPane = new JScrollPane(sourceTextPane);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Source Text"));
        leftPanel.add(sourceScrollPane, BorderLayout.CENTER);
        leftPanel.setMinimumSize(new Dimension(100, 400));
        leftPanel.setMaximumSize(new Dimension(200, 400));

        // Center panel: Search & Results
        resultsTable = new JTable(tableModel);
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Search & Results"));
        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(resultsScrollPane, BorderLayout.CENTER);
        centerPanel.add(createExportPanel(), BorderLayout.SOUTH);
        centerPanel.setMinimumSize(new Dimension(550, 400));
        centerPanel.setPreferredSize(new Dimension(600, 400));

        // Right panel: History
        JPanel historyPanel = createSearchHistoryPanel();
        historyPanel.setMinimumSize(new Dimension(100, 400));
        historyPanel.setMaximumSize(new Dimension(250, 400));

        // Split panes
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1, historyPanel);

        // Compression behavior
        splitPane1.setResizeWeight(0.0); // Prefer compressing left
        splitPane2.setResizeWeight(1.0); // Prefer preserving center

        // Initial divider locations
        splitPane1.setDividerLocation(150);  // initial left panel width
        splitPane2.setDividerLocation(800);  // initial center+left width

        panel.setLayout(new BorderLayout());
        panel.add(splitPane2, BorderLayout.CENTER);

        return panel;
    }



    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 15, 5));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label: "Search for:"
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel searchForLabel = new JLabel("Search for:");
        searchForLabel.setMinimumSize(new Dimension(70, 20));
        inputPanel.add(searchForLabel, gbc);

        // TextField: searchField
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchField = new JTextField(15);
        //searchField.setMinimumSize(new Dimension(80, 25));
        inputPanel.add(searchField, gbc);

        // Label: "by:"
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel byLabel = new JLabel("by:");
        byLabel.setMinimumSize(new Dimension(20, 20));
        inputPanel.add(byLabel, gbc);

        // ComboBox: searchTypeCombo
        gbc.gridx = 3;
        gbc.weightx = 0;
        searchTypeCombo = new JComboBox<>(new String[]{"Word", "Lemma", "POS Tag"});
        //searchTypeCombo.setMinimumSize(new Dimension(90, 25));
        inputPanel.add(searchTypeCombo, gbc);

        // Button: Search
        gbc.gridx = 4;
        searchButton = new JButton("Search");
        searchButton.setEnabled(false);
        //searchButton.setMinimumSize(new Dimension(80, 25));
        inputPanel.add(searchButton, gbc);

        // Button: Clear Highlights
        gbc.gridx = 5;
        clearHighlightsButton = new JButton("Clear Highlights");
        clearHighlightsButton.setEnabled(false);
        //clearHighlightsButton.setMinimumSize(new Dimension(130, 25));
        inputPanel.add(clearHighlightsButton, gbc);

        // Label: Match Count
        gbc.gridx = 6;
        matchCountLabel = new JLabel("Matches: 0");
        matchCountLabel.setFont(matchCountLabel.getFont().deriveFont(Font.BOLD));
        matchCountLabel.setForeground(Color.BLUE);
        //matchCountLabel.setMinimumSize(new Dimension(80, 25));
        inputPanel.add(matchCountLabel, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }






    private void resizeSearchPanelFonts(int panelWidth) {
        int fontSize = Math.max(10, Math.min(18, panelWidth / 60));
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);

        // 应用字体
        searchField.setFont(font);
        searchTypeCombo.setFont(font);
        searchButton.setFont(font);
        clearHighlightsButton.setFont(font);
        matchCountLabel.setFont(font);

        // 设置组件大小
        int height = fontSize + 12;
        int padding = fontSize / 2;
        int buttonWidth = fontSize * 8;
        int textFieldWidth = fontSize * 10;
        int comboWidth = fontSize * 7;

        searchField.setPreferredSize(new Dimension(textFieldWidth, height));
        searchTypeCombo.setPreferredSize(new Dimension(comboWidth, height));
        searchButton.setPreferredSize(new Dimension(buttonWidth, height));
        clearHighlightsButton.setPreferredSize(new Dimension(buttonWidth + 30, height));
        matchCountLabel.setPreferredSize(new Dimension(fontSize * 6, height));

        // 重新布局
        searchField.revalidate();
        searchTypeCombo.revalidate();
        searchButton.revalidate();
        clearHighlightsButton.revalidate();
        matchCountLabel.revalidate();
    }






    private JPanel createExportPanel() {
            JPanel panel = new JPanel(new FlowLayout());

            exportAnalysisButton = new JButton("Export All History Analysis");
            exportAnalysisButton.setEnabled(false);

            exportSearchButton = new JButton("Export Search Results");
            exportSearchButton.setEnabled(false);

            panel.add(exportAnalysisButton);
            panel.add(exportSearchButton);

            return panel;
        }

        private JPanel createStatusPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusLabel = new JLabel("Ready");
            panel.add(statusLabel);
            return panel;
        }

        private JPanel createSearchHistoryPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Search History"));
            panel.setPreferredSize(new Dimension(280, 500));

            // History list
            historyListModel = new DefaultListModel<>();
            historyList = new JList<>(historyListModel);
            historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            historyList.setCellRenderer(new SearchHistoryListCellRenderer());

            // Add mouse listener for double-click to repeat search
            historyList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        repeatSearchFromHistory();
                    }
                }
            });

            JScrollPane historyScrollPane = new JScrollPane(historyList);
            historyScrollPane.setPreferredSize(new Dimension(260, 350));
            panel.add(historyScrollPane, BorderLayout.CENTER);

            // Control buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());

            JButton repeatButton = new JButton("Repeat Search");
            repeatButton.addActionListener(e -> repeatSearchFromHistory());
            buttonPanel.add(repeatButton);

            clearHistoryButton = new JButton("Clear History");
            clearHistoryButton.addActionListener(e -> clearSearchHistory());
            buttonPanel.add(clearHistoryButton);

            saveHistoryButton = new JButton("Save History XML");
            saveHistoryButton.addActionListener(e -> saveSearchHistoryToXML());
            buttonPanel.add(saveHistoryButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        }

        private void setupEventHandlers() {
        // Radio button handlers
            webRadio.addActionListener(e -> {
                browseButton.setEnabled(false);
                sourceField.setEnabled(true);
            });

            fileRadio.addActionListener(e -> {
                browseButton.setEnabled(true);
                sourceField.setEnabled(true);
            });

            // Browse button handler
            browseButton.addActionListener(e -> browseForFile());

            // Load button handler
            loadButton.addActionListener(e -> loadAndAnalyzeText());

            // Search button handler
            searchButton.addActionListener(e -> performSearch());

            // Clear highlights button handler
            clearHighlightsButton.addActionListener(e -> {
                sourceTextPane.getHighlighter().removeAllHighlights();
                updateMatchCount(0);
                statusLabel.setText("Highlights cleared.");
            });

        // Export buttons handlers
            exportAnalysisButton.addActionListener(e -> exportFullAnalysis());
            exportSearchButton.addActionListener(e -> exportSearchResults());

        // Enter key handlers
            sourceField.addActionListener(e -> loadAndAnalyzeText());
            searchField.addActionListener(e -> performSearch());
        }

        private void browseForFile() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
                }

                @Override
                public String getDescription() {
                    return "Text files (*.txt)";}
            });

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sourceField.setText(selectedFile.getAbsolutePath());
            }
        }

        private void loadAndAnalyzeText() {
            String source = sourceField.getText().trim();
            if (source.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a source (URL or file path).");
                return;
            }

            statusLabel.setText("Loading and analyzing text...");

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                  try {
                        currentSource = source;
                        boolean fromWeb = webRadio.isSelected();

                         // Get text
                        currentText = InputHandler.getText(fromWeb, source);

                        if (currentText.trim().isEmpty()) {
                             throw new Exception("No text could be extracted from the source.");
                        }

                        // Process with OpenNLP
                        currentTokens = nlpProcessor.processText(currentText);
                  } catch (Exception e) {
                      throw e;
                  }
                   return null;
                }

                @Override
                protected void done() {
                    try {
                           get(); // Check for exceptions

                           // Update GUI
                            sourceTextPane.setText(currentText.length() > 2000 ? currentText.substring(0, 2000) + "..." : currentText);

                            tableModel.setRowCount(0);
                            for (LinguisticToken token : currentTokens) {

                                Object[] row = {
                                        token.getWord(),
                                        token.getLemma(),
                                        token.getPosTag(),
                                        token.getSentence()
                                };

                                tableModel.addRow(row);
                            }

                            searchButton.setEnabled(true);
                            clearHighlightsButton.setEnabled(true);
                            exportAnalysisButton.setEnabled(true);

                            statusLabel.setText(String.format("Analysis complete. %d tokens processed.",
                                                            currentTokens.size()));

                    } catch (Exception e) {
                            JOptionPane.showMessageDialog(LinguisticSearchApp.this,
                             "Error loading text: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                            statusLabel.setText("Error loading text.");
                    }
                }
            };

            worker.execute();
        }

        private void performSearch() {
            if (currentTokens == null) {
                JOptionPane.showMessageDialog(this, "Please load and analyze text first.");
                return;
            }

            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter a search term.");
                        return;
                    }

            String searchType = (String) searchTypeCombo.getSelectedItem();
            List<LinguisticToken> results;
            switch (searchType) {
                case "Word":
                    results = nlpProcessor.searchByWord(currentTokens, searchTerm);
                    break;
                case "Lemma":
                    results = nlpProcessor.searchByLemma(currentTokens, searchTerm);
                    break;
                case "POS Tag":
                    results = nlpProcessor.searchByPOS(currentTokens, searchTerm);
                    break;
                default:
                    results = nlpProcessor.searchByWord(currentTokens, searchTerm);
            }

            // Update table with search results
            tableModel.setRowCount(0);
            for (LinguisticToken token : results) {

                Object[] row = {
                        token.getWord(),
                        token.getLemma(),
                        token.getPosTag(),
                        token.getSentence()
                };
                tableModel.addRow(row);
            }

            // Highlight matches in the source text
            highlightMatches(results);

            // Update match count display
            updateMatchCount(results.size());

            // Add to search history
            addToSearchHistory(searchTerm, searchType, results.size());

            exportSearchButton.setEnabled(!results.isEmpty());
            statusLabel.setText(String.format("Search complete. %d matches found.", results.size()));
        }

        private void exportFullAnalysis() {
            if (currentTokens == null) {
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(XMLExporter.generateAnalysisFileName(currentSource)));

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                        String outputPath = fileChooser.getSelectedFile().getAbsolutePath();
                        if (!outputPath.endsWith(".xml")) {
                            outputPath += ".xml";
                        }

                        XMLExporter.exportTokensToXML(currentTokens, currentText, currentSource, outputPath);

                        JOptionPane.showMessageDialog(this,
                                 "Analysis exported successfully to: " + outputPath,
                                "Export Complete", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error exporting analysis: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void exportSearchResults() {
            String searchTerm = searchField.getText().trim();
            String searchType = ((String) searchTypeCombo.getSelectedItem()).toLowerCase();

            if (searchTerm.isEmpty() || tableModel.getRowCount() == 0) {
                return;
            }

            // Get current search results from table
            List<LinguisticToken> results;
            switch (searchType) {
                case "word":
                    results = nlpProcessor.searchByWord(currentTokens, searchTerm);
                    break;
                case "lemma":
                    results = nlpProcessor.searchByLemma(currentTokens, searchTerm);
                    break;
                case "pos tag":
                    results = nlpProcessor.searchByPOS(currentTokens, searchTerm);
                    break;
                default:
                    results = nlpProcessor.searchByWord(currentTokens, searchTerm);
            }

            SearchResult searchResult = new SearchResult(searchTerm, searchType, results);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(XMLExporter.generateOutputFileName(searchTerm, searchType)));

            int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                            String outputPath = fileChooser.getSelectedFile().getAbsolutePath();
                            if (!outputPath.endsWith(".xml")) {
                                outputPath += ".xml";
                            }

                            XMLExporter.exportSearchResultToXML(searchResult, outputPath);
                            JOptionPane.showMessageDialog(this,
                                 "Search results exported successfully to: " + outputPath,
                                 "Export Complete", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                         "Error exporting search results: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        }

        /**
         * Highlight matches in the source text
         */
        private void highlightMatches(List<LinguisticToken> matches) {
        // Clear previous highlights
            sourceTextPane.getHighlighter().removeAllHighlights();

            if (matches.isEmpty()) {
                        return;
            }

            String fullText = currentText;
            String displayedText = sourceTextPane.getText();

            // If text is truncated, we need to work with the displayed text
            boolean isTextTruncated = fullText.length() > 2000;
            String searchText = isTextTruncated ? displayedText : fullText;

            Highlighter highlighter = sourceTextPane.getHighlighter();

            for (LinguisticToken token : matches) {
                String wordToHighlight = token.getWord();

                // Find all occurrences of the word in the displayed text
                int index = 0;
                while ((index = searchText.indexOf(wordToHighlight, index)) != -1) {
                    try {
                        // Check if this is a whole word match (not part of another word)
                        boolean isWordBoundary = true;

                        // Check character before
                        if (index > 0) {
                            char prevChar = searchText.charAt(index - 1);
                            isWordBoundary = !Character.isLetterOrDigit(prevChar);
                        }

                        // Check character after
                        if (isWordBoundary && index + wordToHighlight.length() < searchText.length()) {
                            char nextChar = searchText.charAt(index + wordToHighlight.length());
                            isWordBoundary = !Character.isLetterOrDigit(nextChar);
                        }

                        if (isWordBoundary) {
                            highlighter.addHighlight(index, index + wordToHighlight.length(), highlightPainter);
                        }

                    } catch (BadLocationException e) {
                        // Skip this highlight if position is invalid
                        System.err.println("Error highlighting at position " + index + ": " + e.getMessage());
                    }

                    index += wordToHighlight.length();
                }
            }

            // Scroll to first highlight if any matches found
            if (!matches.isEmpty()) {
                try {
                    String firstWord = matches.get(0).getWord();
                    int firstIndex = searchText.indexOf(firstWord);
                    if (firstIndex != -1) {
                        sourceTextPane.setCaretPosition(firstIndex);
                    }
                } catch (Exception e) {
                    // Ignore scrolling errors
                }
            }
        }

        /**
         * Custom list cell renderer for search history
         */
        private static class SearchHistoryListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                        if (value instanceof SearchHistoryEntry) {
                                SearchHistoryEntry entry = (SearchHistoryEntry) value;
                                setText("<html><b>" + entry.getSearchTerm() + "</b> (" + entry.getSearchType() + ")<br>" +
                                               "<small>" + entry.getFormattedTimestamp() + " - " + entry.getResultCount() + " matches</small></html>");
                        }

                        return this;
            }
        }

        /**
         * Update match count display
         */
        private void updateMatchCount(int count) {
            matchCountLabel.setText("Matches: " + count);
            if (count == 0) {
                matchCountLabel.setForeground(Color.RED);
            } else {
                matchCountLabel.setForeground(Color.BLUE);
            }
        }

        /**
         * Add search to history
         */
        private void addToSearchHistory(String searchTerm, String searchType, int resultCount) {
            String sourceInfo = currentSource != null ? currentSource : "Unknown source";
            SearchHistoryEntry entry = new SearchHistoryEntry(searchTerm, searchType, resultCount, sourceInfo);

            // Avoid duplicate entries
            if (!searchHistory.contains(entry)) {
                searchHistory.add(entry);
                historyListModel.addElement(entry);

                // Limit history to 50 entries
                if (searchHistory.size() > 50) {
                    searchHistory.remove(0);
                    historyListModel.remove(0);
                }
            }

            // Enable buttons if history is not empty
            clearHistoryButton.setEnabled(!searchHistory.isEmpty());
            saveHistoryButton.setEnabled(!searchHistory.isEmpty());
        }

    /**
     * Repeat search from selected history entry
     */
        private void repeatSearchFromHistory() {
            SearchHistoryEntry selected = historyList.getSelectedValue();
            if (selected != null) {
                searchField.setText(selected.getSearchTerm());
                searchTypeCombo.setSelectedItem(selected.getSearchType());
                performSearch();
            }
        }

     /**
      * Clear search history
      */
        private void clearSearchHistory() {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all search history?",
                 "Clear History",
                JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                searchHistory.clear();
                historyListModel.clear();
                clearHistoryButton.setEnabled(false);
                saveHistoryButton.setEnabled(false);
                statusLabel.setText("Search history cleared.");
            }
        }

        /**
         * Save search history to XML
         */
        private void saveSearchHistoryToXML() {
            if (searchHistory.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No search history to save.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("search_history_" +
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xml"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String outputPath = fileChooser.getSelectedFile().getAbsolutePath();
                    XMLExporter.exportSearchHistoryToXML(searchHistory, currentSource, outputPath);
                    statusLabel.setText("Search history saved to: " + outputPath);
                    JOptionPane.showMessageDialog(this, "Search history saved successfully!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error saving search history: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                 // Use default look and feel
                }

                new LinguisticSearchApp().setVisible(true);
            });
        }
}