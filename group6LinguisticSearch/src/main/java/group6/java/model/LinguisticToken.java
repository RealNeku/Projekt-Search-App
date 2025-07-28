package group6.java.model;

public class LinguisticToken {
    private String word;
    private String lemma;
    private String posTag;
    private String sentence;
    private int sentenceIndex;
    private int tokenIndex;

    public LinguisticToken(String word, String lemma, String posTag, String sentence,
                           int sentenceIndex, int tokenIndex) {
        this.word = word;
        this.lemma = lemma;
        this.posTag = posTag;
        this.sentence = sentence;
        this.sentenceIndex = sentenceIndex;
        this.tokenIndex = tokenIndex;
    }


    public String getWord() { return word; }
    public String getLemma() { return lemma; }
    public String getPosTag() { return posTag; }
    public String getSentence() { return sentence; }
    public int getSentenceIndex() { return sentenceIndex; }
    public int getTokenIndex() { return tokenIndex; }


    public void setWord(String word) { this.word = word; }
    public void setLemma(String lemma) { this.lemma = lemma; }
    public void setPosTag(String posTag) { this.posTag = posTag; }
    public void setSentence(String sentence) { this.sentence = sentence; }
    public void setSentenceIndex(int sentenceIndex) { this.sentenceIndex = sentenceIndex; }
    public void setTokenIndex(int tokenIndex) { this.tokenIndex = tokenIndex; }

    @Override
    public String toString() {
          return String.format("Token{word='%s', lemma='%s', pos='%s', sentence=%d, token=%d}",
                                word, lemma, posTag, sentenceIndex, tokenIndex);
    }
}