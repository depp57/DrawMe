package fr.depp.drawme.models;

import androidx.lifecycle.ViewModel;


public class GameViewModel extends ViewModel {

    private final String gameName;
    private final String playerName;

    public GameViewModel(String gameName, String playerName) {
        this.gameName = gameName;
        this.playerName = playerName;
    }
}
