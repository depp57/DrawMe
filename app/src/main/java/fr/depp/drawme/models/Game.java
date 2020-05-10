package fr.depp.drawme.models;


import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import fr.depp.drawme.ui.customViews.DrawingCanvas;
import fr.depp.drawme.utils.Dictionary;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class Game {

    private ArrayList<Player> players;
    private String name;
    private String localPlayerName;
    private String wordToGuess;
    private String currentPlayer;
    private String endMessage;
    private boolean started;
    private boolean isGameAdmin;
    private ListenerRegistration firebaseRegistration;


    public final PublishSubject<Boolean> gameUpdatedSubject;
    public final PublishSubject<InGameInfoWrapper> inGameInfoSubject;

    private static final int MAX_USERS = 6;
    private static final Game instance = new Game();


    private Game() {
        players = new ArrayList<>(MAX_USERS);
        gameUpdatedSubject = PublishSubject.create();
        inGameInfoSubject = PublishSubject.create();
    }

    public boolean isAdmin() {
        return isGameAdmin;
    }

    public boolean isCurrentPlayer() {
        return currentPlayer.equals(localPlayerName);
    }

    public static synchronized Game getInstance() {
        return instance;
    }

    public void removePlayer(String playerName) {
        if (players.removeIf(player -> player.getUsername().equals(playerName))) {
            GameRepository.removePlayer(playerName);
        }
    }

    void init(String gameName, String localPlayerName) {
        this.name = gameName;
        this.localPlayerName = localPlayerName;
        this.firebaseRegistration = GameRepository.listenerForGameChange();
        addPlayer(new Player(localPlayerName));
    }

    public void createGame(Context context, String name, OnCustomEventListener<String> callback) {
        GameRepository.createGame(context, name, callback);
        Dictionary.setAssetManager(context);
        isGameAdmin = true;
    }

    public void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        GameRepository.joinGame(context, name, callback);
        Dictionary.setAssetManager(context);
        isGameAdmin = false;
    }

    public void startGame(String firstPlayerName, OnCustomEventListener<String> callback) {
        wordToGuess = Dictionary.getRandomWord();
        GameRepository.startGame(firstPlayerName, wordToGuess, callback);
    }

    private void addPlayer(Player player) {
        if (this.players.size() < MAX_USERS) {
            this.players.add(player);
        }
    }

    boolean alreadySameUsernameInGame(String username) {
        return this.players.stream().anyMatch(player -> player.getUsername().equals(username));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void destroyGame() {
        destroyGame(null);
    }

    public void destroyGame(String endMessage) {
        Log.e("TAG", "destroyGame: " + this);
        removeLocalPlayer();
        name = null;
        localPlayerName = null;
        currentPlayer = null;
        wordToGuess = null;
        this.endMessage = endMessage;
        isGameAdmin = false;
        started = false;
        players.clear();
        firebaseRegistration.remove();
    }

    boolean isFull() {
        return players.size() == MAX_USERS;
    }

    EventListener<DocumentSnapshot> getFirebaseListener() {
        return (data, error) -> {
            if (error != null) {
                return;
            }

            if (data != null && data.exists()) {
                players = GameRepository.deserializePlayersFromFirebaseToList(data);
                Log.e("TAG", "getFirebaseListener: " + this );
                // check if there is only one player left
                if (started && players.size() == 1) {
                    destroyGame("Tous les joueurs ont quitté la partie");
                    inGameInfoSubject.onNext(InGameInfoWrapper.onDestroyGame());
                    return;
                }

                String currentPlayer = data.getString("currentPlayer");
                if (currentPlayer != null) {
                    started = true;
                    this.currentPlayer = currentPlayer;
                    wordToGuess = data.getString("wordToGuess");
                    String lastGuessedWord = data.getString("lastGuessedWord");
                    if (lastGuessedWord == null) lastGuessedWord = "";
                    DrawingCanvas.ColoredPath lastPath = GameRepository.deserializeColoredPathFromFirebase(data);

                    InGameInfoWrapper gameInfo = new InGameInfoWrapper(currentPlayer, wordToGuess, lastGuessedWord, lastPath);
                    inGameInfoSubject.onNext(gameInfo);
                }
            }

            gameUpdatedSubject.onNext(started);
        };
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getEndMessage() {
        return endMessage;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public void removeLocalPlayer() {
        removePlayer(localPlayerName);
    }

    GamePojo asPojo() {
        GamePojo gamePojo = new GamePojo();
        players.forEach(player -> gamePojo.players.put(player.getUsername(), player.getScore()));
        return gamePojo;
    }

    public void updateDrawing(DrawingCanvas.ColoredPath currentPath) {
        GameRepository.updateDrawing(currentPath);
    }

    public void updateGuessedWord(String word) {
        if (Dictionary.isEquals(wordToGuess, word)) {
            String newWordToGuess = Dictionary.getRandomWord();
            GameRepository.newTurn(localPlayerName, newWordToGuess, localPlayerName + " : " + wordToGuess);
            wordToGuess = newWordToGuess;
        } else {
            GameRepository.updateLastGuessedWord(localPlayerName + " : " + word);
        }
    }

    @NotNull
    @Override
    public String toString() {
        // for debug purpose
        return "Game{" +
                "players=" + players +
                ", name='" + name + '\'' +
                ", localPlayerName='" + localPlayerName + '\'' +
                ", wordToGuess='" + wordToGuess + '\'' +
                ", currentPlayer='" + currentPlayer + '\'' +
                ", endMessage='" + endMessage + '\'' +
                ", started=" + started +
                ", isGameAdmin=" + isGameAdmin +
                ", firebaseRegistration=" + firebaseRegistration +
                ", gameUpdatedSubject=" + gameUpdatedSubject +
                ", inGameInfoSubject=" + inGameInfoSubject +
                '}';
    }

    public static class GamePojo {
        public HashMap<String, Integer> players = new HashMap<>(MAX_USERS);
    }
}
