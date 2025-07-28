package group6.java.nlp;
import group6.java.model.LinguisticToken;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenNLPProcessor {
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private LemmatizerME lemmatizer;

    private static final String MODELS_PATH = "src/main/resources/";

        public OpenNLPProcessor() throws Exception {
            initializeModels();
        }

        /**
        * Initialize OpenNLP models from the resources directory
        */
        private void initializeModels() throws Exception {

            SentenceModel sentenceModel = ModelLoader.loadSentenceModel();
            sentenceDetector = new SentenceDetectorME(sentenceModel);


            TokenizerModel tokenizerModel = ModelLoader.loadTokenizerModel();
            tokenizer = new TokenizerME(tokenizerModel);


            POSModel posModel = ModelLoader.loadPOSModel();
            posTagger = new POSTaggerME(posModel);

            try {
                    LemmatizerModel lemmaModel = ModelLoader.loadLemmatizerModel();
                    lemmatizer = new LemmatizerME(lemmaModel);
                    System.out.println("LemmatizerModel loaded successfully");
            } catch (Exception e) {
                  System.out.println("Warning: Lemmatizer model not found. Lemmas will be set to original words.");
                  lemmatizer = null;
            }
        }

       /**
 +      * Process text and extract linguistic tokens
 +      * @param text Input text to process
 +      * @return List of LinguisticToken objects
 +      */
       public List<LinguisticToken> processText(String text) {
           List<LinguisticToken> tokens = new ArrayList<>();

           if (text == null || text.trim().isEmpty()) {
               return tokens;
           }

           String[] sentences = sentenceDetector.sentDetect(text);
           for (int sentenceIndex = 0; sentenceIndex < sentences.length; sentenceIndex++) {
             String sentence = sentences[sentenceIndex];

             String[] words = tokenizer.tokenize(sentence);
                if (words.length == 0) continue;

             String[] posTags = posTagger.tag(words);
             String[] lemmas;

             if (lemmatizer != null) {
                 lemmas = lemmatizer.lemmatize(words, posTags);
             } else {
                 lemmas = words.clone(); // Use original words as lemmas
             }

             for (int tokenIndex = 0; tokenIndex < words.length; tokenIndex++) {
                 String word = words[tokenIndex];
                 String posTag = posTags[tokenIndex];
                 String lemma = lemmas[tokenIndex];
                 LinguisticToken token = new LinguisticToken(word, lemma, posTag,
                         sentence, sentenceIndex, tokenIndex);
                tokens.add(token);
             }
           }
           return tokens;
       }

      /**
      * Search for tokens by word
      * @param tokens List of all tokens
      * @param searchTerm Word to search for
      * @return List of matching tokens
      */
      public List<LinguisticToken> searchByWord(List<LinguisticToken> tokens, String searchTerm) {
          List<LinguisticToken> results = new ArrayList<>();
          String lowerSearchTerm = searchTerm.toLowerCase();

          for (LinguisticToken token : tokens) {
              if (token.getWord().toLowerCase().equals(lowerSearchTerm)) {
                  results.add(token);
              }
          }

          return results;
      }


      /**
      * Search for tokens by lemma
      * @param tokens List of all tokens
      * @param searchTerm Lemma to search for
      * @return List of matching tokens
      */
      public List<LinguisticToken> searchByLemma(List<LinguisticToken> tokens, String searchTerm) {
          List<LinguisticToken> results = new ArrayList<>();
          String lowerSearchTerm = searchTerm.toLowerCase();

          for (LinguisticToken token : tokens) {
              if (token.getLemma().toLowerCase().equals(lowerSearchTerm)) {
                  results.add(token);
              }
          }

          return results;
      }


      /**
      * Search for tokens by POS tag
      * @param tokens List of all tokens
      * @param searchTerm POS tag to search for
      * @return List of matching tokens
      */
      public List<LinguisticToken> searchByPOS(List<LinguisticToken> tokens, String searchTerm) {
          List<LinguisticToken> results = new ArrayList<>();
          String upperSearchTerm = searchTerm.toUpperCase();

          for (LinguisticToken token : tokens) {
              if (token.getPosTag().toUpperCase().equals(upperSearchTerm)) {
                  results.add(token);
              }
          }

          return results;
      }
}