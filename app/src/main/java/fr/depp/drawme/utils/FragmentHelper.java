package fr.depp.drawme.utils;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import fr.depp.drawme.R;

public abstract class FragmentHelper {

    public static void displayFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, boolean withBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);

        if (withBackStack) transaction.addToBackStack(null);
        else fragmentManager.popBackStack();

        transaction.commit();
    }

    public static void displayFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        displayFragment(fragmentManager, fragment, true);
    }

    public static void displayPreviousFragment(Activity activity) {
        // simulate back button to return to the previous fragment
        activity.onBackPressed();
    }

    public static void hideKeyboard(Fragment fragment) {
        View view = fragment.requireView();
        InputMethodManager keyboard = (InputMethodManager) fragment.requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (keyboard != null) keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
