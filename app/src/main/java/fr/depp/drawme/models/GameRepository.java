package fr.depp.drawme.models;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.depp.drawme.R;
import fr.depp.drawme.ui.customViews.DrawingCanvas;

abstract class GameRepository {

    private static final String GAME_COLLECTION_NAME = "games";
    private static final String FAILURE_ERROR_MSG = "Vérifiez votre connexion internet";


    static void createGame(Context context, String name, OnCustomEventListener<String> callback) {
        getGame(name)
                .addOnSuccessListener(data -> {
                    if (!data.exists()) {
                        Game game = Game.getInstance();

                        final String username;
                        // if the user is connected, pick his username, else pick a random username
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        else {
                            String[] usernames = context.getResources().getStringArray(R.array.random_usernames);
                            username = usernames[new Random().nextInt(usernames.length)];
                        }

                        game.init(name, username);

                        getGamesReference().document(name).set(game.asPojo()).addOnSuccessListener(command -> callback.onSuccess(username));
                    }
                    else {
                        callback.onFailure("Une partie du même nom existe déjà");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    static void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        Game game = Game.getInstance();

        getGame(name)
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        game.setPlayers(deserializePlayersFromFirebaseToList(data));

                        if (game.isFull()) {
                            callback.onFailure("La partie est déjà pleine");
                            return;
                        }

                        String username;
                        // if the user is connected, pick his username, else pick a random username
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            if (game.alreadySameUsernameInGame(username)) {
                                callback.onFailure("Un joueur avec le même pseudo est déjà dans la partie");
                                return;
                            }
                        }

                        else {
                            String[] usernames = context.getResources().getStringArray(R.array.random_usernames);
                            do {
                                username = usernames[new Random().nextInt(usernames.length)];
                            }
                            while (game.alreadySameUsernameInGame(username));
                        }

                        game.init(name, username);

                        String finalUsername = username;
                        getGamesReference().document(name).set(game.asPojo()).addOnSuccessListener(command -> callback.onSuccess(finalUsername));
                    }
                    else {
                        callback.onFailure("La partie n'a pas été trouvée");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    static void removePlayer(String playerName) {
        Log.e("TAG", "removePlayer: " + playerName );
        DocumentReference docRef = getGamesReference().document(Game.getInstance().getName());
        docRef.update("players." + playerName, FieldValue.delete());
    }

    static void startGame(String firstPlayerName, String wordToGuess, OnCustomEventListener<String> callback) {
        Game game = Game.getInstance();

        getGame(game.getName())
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        getGamesReference().document(game.getName())
                                .update("started", true, "currentPlayer", firstPlayerName, "wordToGuess", wordToGuess)
                                .addOnSuccessListener(success -> callback.onSuccess(""));
                    }
                    else {
                        // The game should already exists in DB
                        callback.onFailure("Error when starting game, see GameRepository -> startGame()");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    static ListenerRegistration listenerForGameChange() {
        Game game = Game.getInstance();
        return getGamesReference().document(game.getName()).addSnapshotListener(game.getFirebaseListener());
    }

    private static CollectionReference getGamesReference() {
        return FirebaseFirestore.getInstance().collection(GAME_COLLECTION_NAME);
    }

    private static Task<DocumentSnapshot> getGame(String name) {
        return getGamesReference().document(name).get();
    }

    static ArrayList<Player> deserializePlayersFromFirebaseToList(DocumentSnapshot data) {
        try {
            //noinspection unchecked I can't do it by a nice way, because Documentsnapshot.toObject isn't working (27/04/2020)
            Map<String, Long> dataFetched = (Map<String, Long>)data.get("players");
            if (dataFetched != null) {
                ArrayList<Player> players = new ArrayList<>(6);

                dataFetched.forEach((username, score) -> players.add(new Player(username, score.intValue())));

                return players;
            }
            else {
                throw new Exception("The game shouldn't be empty");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error during deserialization, see GameRepository -> deserializePlayersFromFirebaseToList()"
                    + e.getMessage());
        }
    }

    static void newTurn(String currentPlayer, String wordToGuess, String lastGuessedWord) {
        DocumentReference docRef = getGamesReference().document(Game.getInstance().getName());
        docRef.update("currentPlayer", currentPlayer, "lastPathDrawn", FieldValue.delete(), "wordToGuess", wordToGuess, "lastGuessedWord", lastGuessedWord,
        "players." + currentPlayer, FieldValue.increment(1));
    }

    static void updateLastGuessedWord(String lastGuessedWord) {
        DocumentReference docRef = getGamesReference().document(Game.getInstance().getName());
        docRef.update("lastGuessedWord", lastGuessedWord);
    }

    static void updateDrawing(DrawingCanvas.ColoredPath currentPath) {
        DocumentReference docRef = getGamesReference().document(Game.getInstance().getName());
        docRef.update("lastPathDrawn", currentPath);
    }

    @SuppressWarnings("unchecked") // I can't do it by a nice way, because Documentsnapshot.toObject isn't working (04/05/2020)
    static DrawingCanvas.ColoredPath deserializeColoredPathFromFirebase(DocumentSnapshot data) {
        Map<String, Object> dataFetched = (Map<String, Object>)data.get("lastPathDrawn");
        if (dataFetched != null) {
            int color= (int)((long) dataFetched.get("color"));
            ArrayList<HashMap<String, Double>> pathFetched = (ArrayList<HashMap<String, Double>>) dataFetched.get("path");
            DrawingCanvas.ColoredPath path = new DrawingCanvas.ColoredPath(color);

            if (pathFetched != null) {
                pathFetched.forEach(map -> {
                    Double x = map.get("x"), y = map.get("y");
                    if (x != null && y != null) path.addPoint(x.longValue(), y.longValue());
                });
            }

            return path;
        }
        else {
            return null;
        }
    }
}