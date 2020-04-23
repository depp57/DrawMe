package fr.depp.drawme.utils;

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

        transaction.commit();
    }

    public static void displayFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        displayFragment(fragmentManager, fragment, true);
    }
}
