package group6.java.scraper;

import java.io.File;

public class InputHandler {

    /**
     *get text from a Wikipedia URL or a local txt file
     *@param fromWeb     true = scrape from Wikipedia, false = read from a local file
     *@param source      URL or file path
     *@return            cleaned plain text for further processing
     */
    public static String getText(boolean fromWeb,String source){
        if(fromWeb){
            return WikipediaScraper.scrape(source);
        }
        else{
            return FileLoader.readTextFile(new File(source));
        }
    }
}