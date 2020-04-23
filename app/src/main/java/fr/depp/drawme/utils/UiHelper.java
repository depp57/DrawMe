package fr.depp.drawme.utils;

import android.content.Context;
import android.widget.Toast;

public abstract class UiHelper {

    public static void showToast(Context context, String message, int duration, int backgroundColor) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.getView().setBackgroundColor(backgroundColor);
        toast.show();
    }
}
