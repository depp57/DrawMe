package fr.depp.drawme.models;


import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class Game {

    private static final int MAX_USERS = 6;

    @Exclude private String name;
    private HashMap<String, Integer> players;

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // Check https://firebase.google.com/docs/database/android/read-and-write?authuser=0#basic_write
    }

    public Game(String name) {
        this.name = name;
        this.players = new HashMap<>(MAX_USERS);
    }

    public Game(String name, HashMap<String, Integer> players) {
        this.name = name;
        this.players = players;
    }

    public boolean addUser(User user) {
        if (this.players.size() < MAX_USERS) {
            this.players.put(user.getUsername(), user.getScore());
            return true;
        }
        return false;
    }

    public boolean alreadySameUsernameInGame(String username) {
        return this.players.containsKey(username);
    }

    @Exclude public String getName() {
        return name;
    }

    @Exclude public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, Integer> players) {
        this.players = players;
    }

    @Exclude public boolean isFull() {
        return players.size() == MAX_USERS;
    }
}
