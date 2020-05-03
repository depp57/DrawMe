package fr.depp.drawme.models;


import android.content.Context;



import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class Game {

    private ArrayList<Player> players;

    private String name;
    private ListenerRegistration firebaseRegistration;
    private String localPlayerName;
    public final PublishSubject<Boolean> hasGameStartedSubject;
    final PublishSubject<List<Player>> playersSubject;

    private static final int MAX_USERS = 6;
    private static final Game instance = new Game();


    public Game() {
        players = new ArrayList<>(MAX_USERS);
        hasGameStartedSubject = PublishSubject.create();
        playersSubject = PublishSubject.create();
    }

    public boolean isAdmin() {
        return players.get(players.size() - 1).getUsername().equals(localPlayerName);
    }

    public static synchronized Game getInstance() {
        return instance;
    }

    public void removePlayer(String playerName) {
        if (players.removeIf(player -> player.getUsername().equals(playerName))) {
            GameRepository.removePlayer(playerName);
        }
    }

    public void createGame(Context context, String name, OnCustomEventListener<String> callback) {
        GameRepository.createGame(context, name, callback);
    }

    public void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        GameRepository.joinGame(context, name, callback);
    }

    public void startGame(String firstPlayerName, OnCustomEventListener<String> callback) {
        GameRepository.startGame(firstPlayerName, callback);
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
            }
        };
    }

    public void removeLocalPlayer() {
        removePlayer(localPlayerName);
    }

    GamePojo asPojo() {
        GamePojo gamePojo = new GamePojo();
        players.forEach(player -> gamePojo.players.put(player.getUsername(), player.getScore()));
        return gamePojo;
    }

    public static class GamePojo {
        public HashMap<String, Integer> players = new HashMap<>(MAX_USERS);
    }
}
