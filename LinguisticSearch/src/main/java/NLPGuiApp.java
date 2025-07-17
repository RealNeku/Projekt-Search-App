import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class NLPGuiApp extends JFrame {

    private JTextArea inputTextArea;
    private JButton analyzeButton;
    private JTextArea outputArea;

    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton;
    private JTextArea searchResultsArea;

    private JTextField urlField;
    private JButton loadUrlButton;

    private TextProcessor processor;
    private List<AnnotatedSentence> annotatedSentences;

    public NLPGuiApp() {
        super("Linguistic Analyzer GUI");

        try {
            processor = new TextProcessor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading NLP models: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }

        setLayout(new BorderLayout(5,5));

        // URL input panel
        JPanel urlPanel = new JPanel(new BorderLayout(5,5));
        urlPanel.setBorder(BorderFactory.createTitledBorder("Load Text From URL"));

        urlField = new JTextField();
        loadUrlButton = new JButton("Load Text");

        urlPanel.add(new JLabel("URL: "), BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);
        urlPanel.add(loadUrlButton, BorderLayout.EAST);

        add(urlPanel, BorderLayout.NORTH);

        // input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5,5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Text"));
        inputTextArea = new JTextArea(6, 50);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        analyzeButton = new JButton("Analyze");
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        // output panel for annotation
        JPanel outputPanel = new JPanel(new BorderLayout(5,5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Annotated Sentences"));
        outputArea = new JTextArea(12, 50);
        outputArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(inputPanel), new JScrollPane(outputPanel));
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);


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
        JPanel resultsPanel = new JPanel(new BorderLayout(5,5));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        searchResultsArea = new JTextArea(8, 50);
        searchResultsArea.setEditable(false);
        resultsPanel.add(new JScrollPane(searchResultsArea), BorderLayout.CENTER);

        add(resultsPanel, BorderLayout.WEST);

        // action listeners
        analyzeButton.addActionListener(e -> analyzeText());
        searchButton.addActionListener(e -> searchText());
        loadUrlButton.addActionListener(e -> loadTextFromUrl());

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

        try {
            URL url = new URL(urlString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append(" ");
            }
            in.close();

            inputTextArea.setText(content.toString().trim());
            JOptionPane.showMessageDialog(this, "Text loaded from URL successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            outputArea.setText("");
            searchResultsArea.setText("");
            annotatedSentences = null;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading URL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void analyzeText() {
        String inputText = inputTextArea.getText().trim();
        System.out.println("Analyzing: " + inputText);
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to analyze.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            annotatedSentences = processor.processText(inputText);
            System.out.println("Annotated sentences: " + annotatedSentences.size());

            StringBuilder sb = new StringBuilder();
            for (AnnotatedSentence ann : annotatedSentences) {
                sb.append("Original Sentence: ").append(ann.syntax.sentence).append("\n");
                sb.append("Tokens:   ").append(String.join(" ", ann.syntax.tokens)).append("\n");
                sb.append("POS Tags: ").append(String.join(" ", ann.pos.posTags)).append("\n");
                sb.append("Lemmas:   ").append(String.join(" ", ann.lemma.lemmas)).append("\n");
//                sb.append("Semantic: ").append(String.join(" ", ann.semantic.labels)).append("\n\n");
            }

            outputArea.setText(sb.toString());
            searchResultsArea.setText("");

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
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(NLPGuiApp::new);
    }
}
