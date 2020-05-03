package fr.depp.drawme.models;

public class Player {

    private String username;
    private int score;

    public Player() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // Check https://firebase.google.com/docs/database/android/read-and-write?authuser=0#basic_write
    }

    public Player(String username) {
        this.username = username;
        this.score = 0;
    }

    public Player(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
