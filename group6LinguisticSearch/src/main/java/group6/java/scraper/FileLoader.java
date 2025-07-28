package group6.java.scraper;
import java.io.*;

public class FileLoader {
   public static String readTextFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return sb.toString();
   }
}