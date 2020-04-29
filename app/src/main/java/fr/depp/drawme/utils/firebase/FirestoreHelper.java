package fr.depp.drawme.utils.firebase;


import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.depp.drawme.R;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.models.User;

public abstract class FirestoreHelper {

    private static final String GAME_COLLECTION_NAME = "games";
    private static final String FAILURE_ERROR_MSG = "Vérifiez votre connexion internet";

    private static CollectionReference getGamesReference() {
        return FirebaseFirestore.getInstance().collection(GAME_COLLECTION_NAME);
    }

    public static void createGame(Context context, String name, OnCustomEventListener<String> callback) {
        getGame(name)
            .addOnSuccessListener(data -> {
                if (!data.exists()) {
                    Game game = new Game(name);
                    String username;
                    // if the user is connected, pick his username, else pick a random username
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    else {
                        String[] usernames = context.getResources().getStringArray(R.array.random_usernames);
                        username = usernames[new Random().nextInt(usernames.length)];
                    }

                    game.addUser(new User(username));
                    getGamesReference().document(name).set(game).addOnSuccessListener(command -> callback.onSuccess(username));
                }
                else {
                    callback.onFailure("Une partie du même nom existe déjà");
                }
            })
            .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    public static void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        getGame(name)
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        Game game = new Game(name, deserializePlayersFromFirebaseToMap(data));

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

                        game.addUser(new User(username)) ;
                        getGamesReference().document(name).set(game);
                        callback.onSuccess(username);
                    }
                    else {
                        callback.onFailure("La partie n'a pas été trouvée");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }

    private static Task<DocumentSnapshot> getGame(String name) {
        return getGamesReference().document(name).get();
    }

    public static ListenerRegistration listenerForGameChange(String gameName, EventListener<DocumentSnapshot> listener) {
        return getGamesReference().document(gameName).addSnapshotListener(listener);
    }

    public static void removePlayer(String gameName, String playerName) {
        DocumentReference docRef = getGamesReference().document(gameName);
        docRef.update("players." + playerName, FieldValue.delete());
    }

    private static HashMap<String, Integer> deserializePlayersFromFirebaseToMap(DocumentSnapshot data) {
        try {
            //noinspection unchecked I can't do it by a nice way, because Documentsnapshot.toObject isn't working (27/04/2020)
            Map<String, Long> players = (Map<String, Long>)data.get("players");
            if (players != null) {
                HashMap<String, Integer> mapPlayers = new HashMap<>(6);

                players.forEach((username, score) -> mapPlayers.put(username, score.intValue()));

                return mapPlayers;
            }
            else {
                throw new Exception("La partie ne devrait pas être vide");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during deserialization, see FirebaseHelper -> deserializePlayersFromFirebase()"
            + e.getMessage());
        }
    }

    public static ArrayList<User> deserializePlayersFromFirebaseToList(DocumentSnapshot data) {
        try {
            //noinspection unchecked I can't do it by a nice way, because Documentsnapshot.toObject isn't working (27/04/2020)
            Map<String, Object> players = (Map<String, Object>)data.get("players");
            if (players != null) {
                ArrayList<User> listUsers = new ArrayList<>(6);

                players.forEach((username, score) -> listUsers.add(new User(username)));

                return listUsers;
            }
            else {
                throw new Exception("La partie ne devrait pas être vide");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error during deserialization, see FirebaseHelper -> deserializePlayersFromFirebaseToList()"
                    + e.getMessage());
        }
    }

    public static void startGame(String gameName, OnCustomEventListener<String> callback) {
        getGame(gameName)
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        getGamesReference().document(gameName)
                                .update("started", true)
                                .addOnSuccessListener(success -> callback.onSuccess(null));
                    }
                    else {
                        // La partie devrait exister
                        callback.onFailure("Erreur interne, signalez le svp : FireHelper -> startGame()");
                    }
                })
                .addOnFailureListener(error -> callback.onFailure(FAILURE_ERROR_MSG));
    }
}
