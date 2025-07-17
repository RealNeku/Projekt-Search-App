import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class TextProcessor {

    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private LemmatizerME lemmatizer;

    /**
     * constructor that initializes OpenNLP components with binary models.
     */
    public TextProcessor() throws Exception {
        // update these paths to your actual model files
        try (InputStream sentModel = new FileInputStream("resources/models/en-sent.bin");
             InputStream tokenModel = new FileInputStream("resources/models/en-token.bin");
             InputStream posModel = new FileInputStream("resources/models/en-pos-maxent.bin");
             InputStream lemmaModel = new FileInputStream("resources/models/en-lemmatizer.bin")) {

            sentenceDetector = new SentenceDetectorME(new SentenceModel(sentModel));
            tokenizer = new TokenizerME(new TokenizerModel(tokenModel));
            posTagger = new POSTaggerME(new POSModel(posModel));
            lemmatizer = new LemmatizerME(new LemmatizerModel(lemmaModel));
        }
    }

    /**
     * processes text to extract sentences, tokens, POS tags, lemmas, and semantic placeholders
     */
    public List<AnnotatedSentence> processText(String text) {
        List<AnnotatedSentence> results = new ArrayList<>();
        String[] sentences = sentenceDetector.sentDetect(text);

        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence);
            String[] posTags = posTagger.tag(tokens);
            String[] lemmas = lemmatizer.lemmatize(tokens, posTags);

            SyntaxData syntaxData = new SyntaxData(sentence, tokens);
            POSData posData = new POSData(posTags);
            LemmaData lemmaData = new LemmaData(lemmas);
            SemanticData semanticData = new SemanticData();

            results.add(new AnnotatedSentence(syntaxData, posData, lemmaData, semanticData));
        }

        return results;
    }
}
