//import java.util.List;
//import java.util.Scanner;
//
//public class Main {
//
//    public static void main(String[] args) {
//        try {
//            // Initialize NLP processor (loads models)
//            TextProcessor processor = new TextProcessor();
//
//            // Sample input text (you can replace this with GUI input or scraped content)
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Enter text to analyze (type 'q' on a new line to finish):");
//            StringBuilder inputBuilder = new StringBuilder();
//            while (true) {
//                String line = scanner.nextLine();
//                if (line.equalsIgnoreCase("q")) break;
//                inputBuilder.append(line).append(" ");
//            }
//            String inputText = inputBuilder.toString().trim();
//
//
//
//            // Process the text
//            List<AnnotatedSentence> annotatedSentences = processor.processText(inputText);
//
//            // Print full linguistic annotations
//            System.out.println("\n=== Annotated Sentences ===");
//            for (AnnotatedSentence sentence : annotatedSentences) {
//                System.out.println("Original Sentence: " + sentence.sentence);
//                System.out.println("Tokens:   " + String.join(" ", sentence.tokens));
//                System.out.println("POS Tags: " + String.join(" ", sentence.posTags));
//                System.out.println("Lemmas:   " + String.join(" ", sentence.lemmas));
//                System.out.println();
//            }
//
//            // Allow search functionality
//            Scanner input = new Scanner(System.in);
//            System.out.print("Search type (word / lemma / pos): ");
//            String type = input.nextLine().trim();
//
//            System.out.print("Search query: ");
//            String query = input.nextLine().trim();
//
//            List<AnnotatedSentence> searchResults = TextSearcher.search(annotatedSentences, query, type);
//
//            System.out.println("\n=== Search Results ===");
//            for (AnnotatedSentence match : searchResults) {
//                System.out.println(match.sentence);
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error during processing: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}

/**
 * the demo version, not connected with GUI
 */

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            TextProcessor processor = new TextProcessor();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter text to analyze (type 'q' on a new line to finish):");
            StringBuilder inputBuilder = new StringBuilder();
            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("q")) break;
                inputBuilder.append(line).append(" ");
            }
            String inputText = inputBuilder.toString().trim();

            List<AnnotatedSentence> annotatedSentences = processor.processText(inputText);

            System.out.println("\n=== Annotated Sentences ===");
            for (AnnotatedSentence annSent : annotatedSentences) {
                System.out.println("Original Sentence: " + annSent.syntax.sentence);
                System.out.println("Tokens:   " + String.join(" ", annSent.syntax.tokens));
                System.out.println("POS Tags: " + String.join(" ", annSent.pos.posTags));
                System.out.println("Lemmas:   " + String.join(" ", annSent.lemma.lemmas));
                System.out.println();
            }

            Scanner input = new Scanner(System.in);
            System.out.print("Search type (word / lemma / pos): ");
            String type = input.nextLine().trim();

            System.out.print("Search query: ");
            String query = input.nextLine().trim();

            List<AnnotatedSentence> searchResults = TextSearcher.search(annotatedSentences, query, type);

            System.out.println("\n=== Search Results ===");
            for (AnnotatedSentence match : searchResults) {
                System.out.println(match.syntax.sentence);
            }

        } catch (Exception e) {
            System.err.println("Error during processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
