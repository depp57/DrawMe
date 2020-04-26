package fr.depp.drawme.utils.firebase;

import android.app.Activity;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import fr.depp.drawme.R;
import fr.depp.drawme.models.OnCustomEventListener;

public abstract class FirebaseAuthHelper {

    private static final int RC_SIGN_IN = 100;

    public static void signIn(Activity activity) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), FirebaseAuthHelper.RC_SIGN_IN);
    }

    public static void signOut(Activity activity, OnCustomEventListener<String> callback) {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnSuccessListener(data -> callback.onSuccess("Vous êtes bien déconnecté"))
                .addOnFailureListener(error -> callback.onFailure(activity.getResources().getString(R.string.internet_error)));
    }

    public static void deleteAccount(Activity activity, OnCustomEventListener<String> callback) {
        AuthUI.getInstance()
                .delete(activity)
                .addOnSuccessListener(data -> callback.onSuccess("Votre compte a bien été supprimé"))
                .addOnFailureListener(error -> callback.onFailure(activity.getResources().getString(R.string.internet_error)));
    }
}
