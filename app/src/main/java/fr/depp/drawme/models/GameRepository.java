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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import fr.depp.drawme.R;
import fr.depp.drawme.utils.WordsToGuess;

abstract class GameRepository {

    private static final String GAME_COLLECTION_NAME = "games";
    private static final String FAILURE_ERROR_MSG = "Vérifiez votre connexion internet";


    static void createGame(Context context, String name, OnCustomEventListener<String> callback) {
        getGame(name)
                .addOnSuccessListener(data -> {
                    if (!data.exists()) {
                        Game game = Game.getInstance();
                        game.setName(name);

                        String username;
                        // if the user is connected, pick his username, else pick a random username
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        else {
                            String[] usernames = context.getResources().getStringArray(R.array.random_usernames);
                            username = usernames[new Random().nextInt(usernames.length)];
                        }

                        game.addPlayer(new Player(username));
                        game.setFirebaseRegistration(listenerForGameChange());
                        game.setLocalPlayerName(username);

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

        getGame(game.getName())
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        game.setPlayers(deserializePlayersFromFirebaseToList(data));

                        if (game.isFull()) {
                            callback.onFailure("La partie est déjà pleine");
                            return;
                        }
                        game.setName(null);

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

                        game.addPlayer(new Player(username)) ;
                        getGamesReference().document(name).set(game);
                        game.setFirebaseRegistration(listenerForGameChange());
                        game.setLocalPlayerName(username);
                        callback.onSuccess(username);
                    }
                    else {
                        callback.onFailure("La partie n'a pas été trouvée");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    static void removePlayer(String playerName) {
        DocumentReference docRef = getGamesReference().document(Game.getInstance().getName());
        docRef.update("players." + playerName, FieldValue.delete());
    }

    static void startGame(String firstPlayerName, OnCustomEventListener<String> callback) {
        Game game = Game.getInstance();

        getGame(game.getName())
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        getGamesReference().document(game.getName())
                                .update("started", true, "currentPlayer", firstPlayerName, "wordToGuess", WordsToGuess.getRandomWord())
                                .addOnSuccessListener(success -> callback.onSuccess(null));
                    }
                    else {
                        // The game should already exists in DB
                        callback.onFailure("Error when starting game, see GameRepository -> startGame()");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    private static ListenerRegistration listenerForGameChange() {
        Game game = Game.getInstance();
        return getGamesReference().document(game.getName()).addSnapshotListener(game.getFirebaseListener());
    }

    private static CollectionReference getGamesReference() {
        return FirebaseFirestore.getInstance().collection(GAME_COLLECTION_NAME);
    }

    private static Task<DocumentSnapshot> getGame(String name) {
        return getGamesReference().document(name).get();
    }

    static HashMap<String, Integer> deserializePlayersFromFirebaseToMap(DocumentSnapshot data) {
        try {
            //noinspection unchecked I can't do it by a nice way, because Documentsnapshot.toObject isn't working (27/04/2020)
            Map<String, Long> players = (Map<String, Long>)data.get("players");
            if (players != null) {
                HashMap<String, Integer> mapPlayers = new LinkedHashMap<>(6);

                players.forEach((username, score) -> mapPlayers.put(username, score.intValue()));

                return mapPlayers;
            }
            else {
                throw new Exception("La partie ne devrait pas être vide");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during deserialization, see GameRepository -> deserializePlayersFromFirebase()"
                    + e.getMessage());
        }
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
                throw new Exception("La partie ne devrait pas être vide");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error during deserialization, see GameRepository -> deserializePlayersFromFirebaseToList()"
                    + e.getMessage());
        }
    }
}