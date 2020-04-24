package fr.depp.drawme.utils.firebase;


import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

import fr.depp.drawme.R;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.models.User;

public abstract class FirebaseService {

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
                    getGamesReference().document(name).set(game);

                    callback.onSuccess(null);
                }
                else {
                    callback.onFailure("Une partie du même nom existe déjà");
                }
            })
            .addOnFailureListener((error) -> callback.onFailure("Vérifiez votre connexion internet"));
    }

    // this is because "new Game(name, (ArrayList<User>)(data.get("players")));" could throws classCastException
    // I can't handle it by a nice way because the method DocumentSnapshot.toObject isn't currently working (24 April 2020)
    @SuppressWarnings("unchecked")
    public static void joinGame(Context context, String name, OnCustomEventListener<String> callback) {
        getGame(name)
                .addOnSuccessListener(data -> {
                    if (data.exists()) {
                        Game game;
                        try {
                            game = new Game(name, (ArrayList<User>)(data.get("players")));
                        }
                        catch (ClassCastException e) {
                            callback.onFailure("Erreur lors de la récupération de la partie, veuillez signaler le bug svp");
                            return;
                        }

                        String username;
                        // if the user is connected, pick his username, else pick a random username
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        else {
                            String[] usernames = context.getResources().getStringArray(R.array.random_usernames);
                            username = usernames[new Random().nextInt(usernames.length)];
                        }

                        // game.addUser return false if the game is already full
                        if (game.addUser(new User(username))) {
                            getGamesReference().document(name).set(game);
                            callback.onSuccess("Vous avez rejoint la partie");
                        }
                        else {
                            callback.onFailure("La partie est déjà pleine");
                        }
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
}
