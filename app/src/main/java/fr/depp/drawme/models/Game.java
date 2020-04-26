package fr.depp.drawme.models;


import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private static final int MAX_USERS = 6;
    private static final String TAG = "Game";

    @Exclude private String name;
    private ArrayList<User> players;

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // Check https://firebase.google.com/docs/database/android/read-and-write?authuser=0#basic_write
    }

    public Game(String name) {
        this.name = name;
        this.players = new ArrayList<>(MAX_USERS);
    }

    public Game(String name, ArrayList<User> players) {
        this.name = name;
        this.players = players;
    }

    public boolean addUser(User user) {
        if (this.players.size() < MAX_USERS) {
            this.players.add(user);
            return true;
        }
        return false;
    }

    @Exclude public String getName() {
        return name;
    }

    @Exclude public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList players) {
        this.players = players;
    }
}
