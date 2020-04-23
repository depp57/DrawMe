package fr.depp.drawme.models;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class FirebaseService {

    private static final String GAME_COLLECTION_NAME = "games";
    private static final String TAG = "FirebaseService";


    private static CollectionReference getGamesReference() {
        return FirebaseFirestore.getInstance().collection(GAME_COLLECTION_NAME);
    }

    public static void createGame(String name, OnCustomEventListener<String> callback) {
        getGame(name)
            .addOnSuccessListener((data) -> {
                if (!data.exists()) {
                    getGamesReference().document(name).set(new Game(name));
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
