package fr.depp.drawme.utils.firebase;


import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
            .addOnSuccessListener((data) -> {
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

    public static Task<DocumentSnapshot> getGame(String name) {
        return getGamesReference().document(name).get();
    }
}
