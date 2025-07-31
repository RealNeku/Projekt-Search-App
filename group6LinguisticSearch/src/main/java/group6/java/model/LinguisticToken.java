package group6.java.model;

public class LinguisticToken {
    private String word;
    private String mweType;     // Multi-Word Expression type (e.g., "COMPOUND", "PHRASAL_VERB", "NAMED_ENTITY")
    private String mweLabel;    // MWE label or category
    private boolean isMweStart; // Whether this token starts an MWE
    private boolean isMweEnd;   // Whether this token ends an MWE
    private String mweFullForm; // Full form of the MWE if this token is part of one
    private String sentence;
    private int sentenceIndex;
    private int tokenIndex;

    public LinguisticToken(String word, String mweType, String mweLabel, boolean isMweStart, 
                           boolean isMweEnd, String mweFullForm, String sentence,
                           int sentenceIndex, int tokenIndex) {
        this.word = word;
        this.mweType = mweType;
        this.mweLabel = mweLabel;
        this.isMweStart = isMweStart;
        this.isMweEnd = isMweEnd;
        this.mweFullForm = mweFullForm;
        this.sentence = sentence;
        this.sentenceIndex = sentenceIndex;
        this.tokenIndex = tokenIndex;
    }


    public String getWord() { return word; }
    public String getMweType() { return mweType; }
    public String getMweLabel() { return mweLabel; }
    public boolean isMweStart() { return isMweStart; }
    public boolean isMweEnd() { return isMweEnd; }
    public String getMweFullForm() { return mweFullForm; }
    public String getSentence() { return sentence; }
    public int getSentenceIndex() { return sentenceIndex; }
    public int getTokenIndex() { return tokenIndex; }

    public void setWord(String word) { this.word = word; }
    public void setMweType(String mweType) { this.mweType = mweType; }
    public void setMweLabel(String mweLabel) { this.mweLabel = mweLabel; }
    public void setMweStart(boolean mweStart) { this.isMweStart = mweStart; }
    public void setMweEnd(boolean mweEnd) { this.isMweEnd = mweEnd; }
    public void setMweFullForm(String mweFullForm) { this.mweFullForm = mweFullForm; }
    public void setSentence(String sentence) { this.sentence = sentence; }
    public void setSentenceIndex(int sentenceIndex) { this.sentenceIndex = sentenceIndex; }
    public void setTokenIndex(int tokenIndex) { this.tokenIndex = tokenIndex; }

    @Override
    public String toString() {
          return String.format("Token{word='%s', mweType='%s', mweLabel='%s', mweFullForm='%s', sentence=%d, token=%d}",
                                word, mweType, mweLabel, mweFullForm, sentenceIndex, tokenIndex);
    }
}