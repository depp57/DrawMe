package fr.depp.drawme.utils.firebase;


import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import fr.depp.drawme.R;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.models.User;

public abstract class FirestoreHelper {

    private static final String GAME_COLLECTION_NAME = "games";

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
                    getGamesReference().document(name).set(game).addOnSuccessListener(command -> callback.onSuccess(name));
                }
                else {
                    callback.onFailure("Une partie du même nom existe déjà");
                }
            })
            .addOnFailureListener((error) -> callback.onFailure("Vérifiez votre connexion internet"));
    }

    public static void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        getGame(name)
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        Game game = new Game(name, deserializePlayersFromFirebase(data));

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
                        callback.onSuccess(name);
                    }
                    else {
                        callback.onFailure("La partie n'a pas été trouvée");
                    }
                })
                .addOnFailureListener((error) -> callback.onFailure("Vérifiez votre connexion internet"));
    }

    private static Task<DocumentSnapshot> getGame(String name) {
        return getGamesReference().document(name).get();
    }

    public static ListenerRegistration getRegistrationForGame(String gameName, EventListener<DocumentSnapshot> listener) {
        return getGamesReference().document(gameName).addSnapshotListener(listener);
    }

    public static void removePlayer(String gameName, User player) {

    }

    // this is because "(ArrayList<HashMap<String, Object>>)players" could throws classCastException
    // I can't handle it by a nice way because the method DocumentSnapshot.toObject isn't currently working (24 April 2020)
    @SuppressWarnings("unchecked")
    public static ArrayList<User> deserializePlayersFromFirebase(DocumentSnapshot data) {
        try {
            Object players = data.get("players");
            if (players != null) {
                ArrayList<HashMap<String, Object>> map = (ArrayList<HashMap<String, Object>>)players;
                ArrayList<User> listPlayers = new ArrayList<>();

                map.forEach(user -> {
                    String username = (String) user.get("username");
                    listPlayers.add(new User(username));
                });

                return listPlayers;
            }
            else {
                throw new Exception("La partie ne devrait pas être vide");
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error during deserialization, see FirebaseService -> deserializePlayersFromFirebase()"
            + e.getMessage());
        }
    }
}
