package fr.depp.drawme.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class Game {

    private static final int MAX_USERS = 4;

    @Exclude private String name;
    private ArrayList<User> users;

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // Check https://firebase.google.com/docs/database/android/read-and-write?authuser=0#basic_write
    }

    public Game(String name) {
        this.name = name;
        this.users = new ArrayList<>(MAX_USERS);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getUsers() {
        return users;
    }

    public void setUsers(ArrayList users) {
        this.users = users;
    }
}
