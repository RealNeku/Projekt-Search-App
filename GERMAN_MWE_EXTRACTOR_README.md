# German Multiword Expression (MWE) Extractor

A comprehensive Java application for extracting German multiword expressions from Wikipedia articles using advanced NLP techniques and linguistic patterns.

## Features

- **German Language Support**: Specialized processing for German text with proper handling of umlauts, compound words, and German-specific linguistic patterns
- **Multiple MWE Types**: Identifies compound nouns, phrasal verbs, prepositional phrases, idioms, named entities, collocations, and light verb constructions
- **Wikipedia Integration**: Direct scraping and processing of German Wikipedia articles
- **Statistical Analysis**: Uses Pointwise Mutual Information (PMI) and frequency analysis for MWE identification
- **Pattern Matching**: Rule-based extraction using linguistic patterns specific to German
- **Multiple Output Formats**: Export results to JSON, CSV, and formatted text files
- **Comprehensive Statistics**: Detailed analysis of extracted MWEs with confidence scores

## Project Structure

```
group6LinguisticSearch/
├── src/main/java/group6/java/
│   ├── GermanMWEExtractorApp.java          # Main application
│   ├── model/
│   │   ├── LinguisticToken.java            # Token representation
│   │   ├── MultiWordExpression.java        # MWE data model
│   │   ├── SearchHistoryEntry.java         # Search history
│   │   └── SearchResult.java               # Search results
│   ├── nlp/
│   │   ├── GermanLanguageProcessor.java    # German text processing
│   │   ├── GermanMWEExtractor.java         # MWE extraction engine
│   │   ├── ModelLoader.java                # NLP model loading
│   │   └── OpenNLPProcessor.java           # Base NLP processing
│   ├── scraper/
│   │   ├── GermanWikipediaScraper.java     # German Wikipedia scraper
│   │   ├── WikipediaScraper.java           # Base Wikipedia scraper
│   │   ├── FileLoader.java                 # File operations
│   │   └── InputHandler.java               # Input handling
│   └── util/
│       └── MWEResultFormatter.java         # Output formatting
└── src/main/resources/
    └── models/                             # NLP models directory
```

## Requirements

- Java 11 or higher
- Maven 3.6+
- Internet connection (for Wikipedia scraping)

## Dependencies

- **OpenNLP**: Natural language processing
- **JSoup**: HTML parsing and web scraping
- **Gson**: JSON serialization
- **Apache Commons Lang**: Utility functions

## Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd group6LinguisticSearch
   ```

2. **Build the project**:
   ```bash
   mvn clean compile
   ```

3. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="group6.java.GermanMWEExtractorApp"
   ```

## Usage

### Command Line Interface

The application supports various command-line options:

#### Extract from Wikipedia URL
```bash
java -cp target/classes group6.java.GermanMWEExtractorApp url "https://de.wikipedia.org/wiki/Deutsche_Sprache" output_file
```

#### Extract from Wikipedia Article Title
```bash
java -cp target/classes group6.java.GermanMWEExtractorApp title "Bundeskanzler" output_file
```

#### Search Wikipedia and Extract
```bash
java -cp target/classes group6.java.GermanMWEExtractorApp search "Linguistik" 5 output_file
```

#### Extract from Random Articles
```bash
java -cp target/classes group6.java.GermanMWEExtractorApp random 3 output_file
```

#### Extract from Wikipedia Category
```bash
java -cp target/classes group6.java.GermanMWEExtractorApp category "Deutsche_Sprache" 5 output_file
```

### Programmatic Usage

```java
// Initialize the extractor
GermanMWEExtractorApp app = new GermanMWEExtractorApp();

// Configure extraction parameters
app.extractor.setMinConfidence(0.6);
app.extractor.setMaxMWELength(4);

// Extract from a specific article
List<MultiWordExpression> mwes = app.extractFromTitle("Deutsche_Sprache");

// Print statistics
app.printMWEStatistics(mwes);

// Export results
app.exportMWEs(mwes, "german_mwes", "json");
```

## MWE Types Supported

1. **COMPOUND_NOUN**: German compound words (e.g., "Bundeskanzler", "Wissenschaftler")
2. **PHRASAL_VERB**: Separable verbs and phrasal constructions (e.g., "aufhören", "einsteigen")
3. **PREPOSITIONAL_PHRASE**: Prepositional phrases (e.g., "im Gegensatz zu", "in Bezug auf")
4. **IDIOMATIC_EXPRESSION**: German idioms (e.g., "ins Gras beißen", "den Nagel auf den Kopf treffen")
5. **NAMED_ENTITY**: Named entities (e.g., "Vereinte Nationen", "Europäische Union")
6. **COLLOCATION**: Common word combinations (e.g., "schwere Entscheidung", "große Bedeutung")
7. **LIGHT_VERB_CONSTRUCTION**: Light verb constructions (e.g., "eine Entscheidung treffen")

## Extraction Methods

### 1. Pattern-Based Extraction
- Uses predefined linguistic patterns for German
- Recognizes known MWEs from curated lists
- High precision for well-known expressions

### 2. Statistical Extraction
- Calculates Pointwise Mutual Information (PMI)
- Analyzes word co-occurrence frequencies
- Identifies statistically significant word combinations

### 3. Syntactic Pattern Extraction
- Uses POS tags to identify linguistic patterns
- Recognizes adjective-noun, verb-preposition combinations
- Handles German-specific constructions

### 4. German-Specific Rules
- Compound noun detection using morphological patterns
- Separable verb identification
- Named entity recognition for German proper nouns

## Output Formats

### JSON Format
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "totalMWEs": 45,
  "mwes": [
    {
      "expression": "schwere Entscheidung",
      "lemmatizedForm": "schwer Entscheidung",
      "type": "COLLOCATION",
      "confidence": 0.75,
      "sourceUrl": "https://de.wikipedia.org/wiki/..."
    }
  ],
  "statistics": { ... }
}
```

### CSV Format
```csv
Expression,Lemmatized_Form,Type,Confidence,Length,Source_URL
"schwere Entscheidung","schwer Entscheidung","COLLOCATION",0.750,2,"https://de.wikipedia.org/wiki/..."
```

### Text Format
```
COMPOUND_NOUN (12 expressions)
----------------------------------------
  Bundeskanzler                    (conf: 0.85)
  Wissenschaftler                  (conf: 0.82)

COLLOCATION (8 expressions)
----------------------------------------
  schwere Entscheidung             (conf: 0.75)
    Lemma: schwer Entscheidung
```

## Configuration

### Extraction Parameters
```java
// Minimum confidence threshold (0.0 - 1.0)
extractor.setMinConfidence(0.5);

// Maximum MWE length in words
extractor.setMaxMWELength(5);

// Minimum MWE length in words
extractor.setMinMWELength(2);
```

### Scraping Parameters
```java
// Delay between Wikipedia requests (milliseconds)
scraper.setRequestDelay(1500);

// Maximum retry attempts
scraper.setMaxRetries(3);
```

## German Language Features

### Preprocessing
- Handles German umlauts (ä, ö, ü, ß)
- Normalizes quotation marks and special characters
- Processes hyphenated compounds

### Linguistic Patterns
- German preposition recognition
- Article identification (der, die, das, etc.)
- Modal verb detection
- Separable prefix recognition

### Compound Word Detection
- Identifies potential German compounds using morphological patterns
- Minimum length thresholds for compound detection
- Capitalization-based recognition

## Examples

### Example 1: Basic Usage
```java
GermanMWEExtractorApp app = new GermanMWEExtractorApp();
List<MultiWordExpression> mwes = app.extractFromTitle("Bundestag");
app.printMWEStatistics(mwes);
```

### Example 2: Batch Processing
```java
List<String> articles = Arrays.asList("Politik", "Wirtschaft", "Kultur");
List<MultiWordExpression> allMWEs = new ArrayList<>();

for (String article : articles) {
    allMWEs.addAll(app.extractFromTitle(article));
}

app.exportMWEs(allMWEs, "batch_results", "json");
```

### Example 3: Custom Configuration
```java
app.extractor.setMinConfidence(0.7);        // Higher precision
app.extractor.setMaxMWELength(3);           // Shorter expressions only
app.scraper.setRequestDelay(2000);          // Slower scraping

List<MultiWordExpression> mwes = app.extractFromSearch("Deutsche Sprache", 10);
```

## Performance Considerations

- **Rate Limiting**: Respects Wikipedia's rate limits with configurable delays
- **Memory Usage**: Processes articles one at a time to manage memory
- **Network**: Handles connection errors with retry mechanisms
- **Caching**: Consider implementing local caching for repeated article access

## Troubleshooting

### Common Issues

1. **Missing Models**: The application falls back to English models if German models aren't available
2. **Network Errors**: Check internet connection and Wikipedia availability
3. **Memory Issues**: Reduce batch sizes for large-scale extraction
4. **Rate Limiting**: Increase delay between requests if getting blocked

### Debug Mode
Enable verbose logging by setting system property:
```bash
java -Djava.util.logging.level=FINE ...
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

## License

This project is part of an academic research effort and is available for educational and research purposes.

## Acknowledgments

- OpenNLP project for NLP tools
- Wikipedia for providing accessible German language content
- Research community for German MWE extraction methodologies

## Contact

For questions or issues, please contact the project team:
- Marco Weber
- Agnessa Fomina  
- Jue Huang
- Ruonan Mi