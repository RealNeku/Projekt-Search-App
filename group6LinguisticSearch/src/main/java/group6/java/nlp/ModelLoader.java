package group6.java.nlp;

import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.IOException;
import java.io.InputStream;

public class ModelLoader {

    public static SentenceModel loadSentenceModel() throws IOException {
        String[] fileNames = {
                "/models/en-sent.bin",
                "/models/opennlp-en-ud-ewt-sentence-1.3-2.5.4.bin",
                "/models/sentence-model.bin"
        };

        for (String path : fileNames) {
            try (InputStream modelIn = ModelLoader.class.getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new SentenceModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }

        throw new IOException("Sentence detection model not found in resources/models/.");
    }

    public static TokenizerModel loadTokenizerModel() throws IOException {
        String[] fileNames = {
                "/models/en-token.bin",
                "/models/opennlp-en-ud-ewt-tokens-1.3-2.5.4.bin",
                "/models/tokenizer-model.bin"
        };

        for (String path : fileNames) {
            try (InputStream modelIn = ModelLoader.class.getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new TokenizerModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }

        throw new IOException("Tokenizer model not found in resources/models/.");
    }

    public static POSModel loadPOSModel() throws IOException {
        String[] fileNames = {
                "/models/en-pos-maxent.bin",
                "/models/opennlp-en-ud-ewt-pos-1.3-2.5.4.bin",
                "/models/pos-model.bin"
        };

        for (String path : fileNames) {
            try (InputStream modelIn = ModelLoader.class.getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new POSModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }

        throw new IOException("POS model not found in resources/models/.");
    }

    public static LemmatizerModel loadLemmatizerModel() throws IOException {
        String[] fileNames = {
                "/models/en-lemmatizer.bin",
                "/models/opennlp-en-ud-ewt-lemmas-1.3-2.5.4.bin",
                "/models/lemmatizer-model.bin"
        };

        for (String path : fileNames) {
            try (InputStream modelIn = ModelLoader.class.getResourceAsStream(path)) {
                if (modelIn != null) {
                    return new LemmatizerModel(modelIn);
                }
            } catch (Exception e) {
                // Try next
            }
        }

        throw new IOException("Lemmatizer model not found in resources/models/.");
    }
}