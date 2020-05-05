package fr.depp.drawme.models;

import fr.depp.drawme.ui.customViews.DrawingCanvas;

public class InGameInfoWrapper {

    private String currentPlayer;
    private String wordToGuess;
    private String lastGuessedWord;
    private DrawingCanvas.ColoredPath lastPath;

    public InGameInfoWrapper(String currentPlayer, String wordToGuess, String lastGuessedWord) {
        this.currentPlayer = currentPlayer;
        this.wordToGuess = wordToGuess;
        this.lastGuessedWord = lastGuessedWord;
    }

    public InGameInfoWrapper(String currentPlayer, String wordToGuess, String lastGuessedWord, DrawingCanvas.ColoredPath lastPath) {
        this(currentPlayer, wordToGuess, lastGuessedWord);
        this.lastPath = lastPath;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public String getLastGuessedWord() {
        return lastGuessedWord;
    }

    public DrawingCanvas.ColoredPath getLastPath() {
        return lastPath;
    }
}
