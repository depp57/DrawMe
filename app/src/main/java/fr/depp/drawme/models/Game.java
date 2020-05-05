package fr.depp.drawme.models;


import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.depp.drawme.ui.customViews.DrawingCanvas;
import fr.depp.drawme.utils.WordsToGuess;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class Game {

    private ArrayList<Player> players;

    private String name;
    private ListenerRegistration firebaseRegistration;
    private String localPlayerName;
    private String wordToGuess;
    private String currentPlayer;
    public final PublishSubject<Boolean> hasGameStartedSubject;
    public final PublishSubject<InGameInfoWrapper> inGameInfoSubject;
    final PublishSubject<List<Player>> playersSubject;

    private static final int MAX_USERS = 6;
    private static final Game instance = new Game();


    private Game() {
        players = new ArrayList<>(MAX_USERS);
        hasGameStartedSubject = PublishSubject.create();
        playersSubject = PublishSubject.create();
        inGameInfoSubject = PublishSubject.create();
    }

    public boolean isAdmin() {
        return players.get(0).getUsername().equals(localPlayerName);
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
    }

    public void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        GameRepository.joinGame(context, name, callback);
    }

    public void startGame(String firstPlayerName, OnCustomEventListener<String> callback) {
        wordToGuess = WordsToGuess.getRandomWord();
        GameRepository.startGame(firstPlayerName, wordToGuess, callback);
    }

    void addPlayer(Player player) {
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

    public void setLocalPlayerName(String name) {
        localPlayerName = name;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    void setFirebaseRegistration(ListenerRegistration listenerRegistration) {
        firebaseRegistration = listenerRegistration;
    }

    public void destroyGame() {
        name = null;
        players.clear();
        firebaseRegistration.remove();
    }

    @Exclude
    public boolean isFull() {
        return players.size() == MAX_USERS;
    }

    public EventListener<DocumentSnapshot> getFirebaseListener() {
        return (data, error) -> {
            if (error != null) {
                return;
            }

            if (data != null && data.exists()) {
                if (data.get("started") != null) {
                    hasGameStartedSubject.onNext(true);
                }

                players = GameRepository.deserializePlayersFromFirebaseToList(data);
                playersSubject.onNext(players);


                String currentPlayer = data.getString("currentPlayer");
                if (currentPlayer != null) {
                    this.currentPlayer = currentPlayer;
                    wordToGuess = data.getString("wordToGuess");
                    String lastGuessedWord = data.getString("lastGuessedWord");
                    if (lastGuessedWord == null) lastGuessedWord = "";
                    DrawingCanvas.ColoredPath lastPath = GameRepository.deserializeColoredPathFromFirebase(data);

                    InGameInfoWrapper gameInfo = new InGameInfoWrapper(currentPlayer, wordToGuess, lastGuessedWord, lastPath);
                    inGameInfoSubject.onNext(gameInfo);
                }
            }
        };
    }

    public String getCurrentPlayer() {
        return currentPlayer;
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
        if (isWordMatchingSecretWord(word)) {
            String newWordToGuess = WordsToGuess.getRandomWord();
            GameRepository.newTurn(localPlayerName, newWordToGuess, localPlayerName + " : " + wordToGuess);
            wordToGuess = newWordToGuess;
        }
        else {
            GameRepository.updateLastGuessedWord(localPlayerName + " : " + word);
        }
    }

    // compare strings with up to one fault
    private boolean isWordMatchingSecretWord(String word) {
        if (Math.abs(word.length() - wordToGuess.length()) > 1) return false;

        char[] longestWord, smallestWord;

        if (word.length() > wordToGuess.length()) {
            longestWord = word.toCharArray();
            smallestWord = wordToGuess.toCharArray();
        }
        else {
            longestWord = wordToGuess.toCharArray();
            smallestWord = word.toCharArray();
        }

        int faults = 0;

        for (int i = 0; i < smallestWord.length; i++) {
            if (Character.toLowerCase(smallestWord[i]) != Character.toLowerCase(longestWord[i])) faults++;
        }


        Log.e("TAG", "isWordMatchingSecretWord: " + wordToGuess );
        Log.e("TAG", "isWordMatchingSecretWord: "+ word );
        Log.e("TAG", "isWordMatchingSecretWord: " + faults );
        return faults < 2;
    }

    public static class GamePojo {
        public HashMap<String, Integer> players = new HashMap<>(MAX_USERS);
    }
}
