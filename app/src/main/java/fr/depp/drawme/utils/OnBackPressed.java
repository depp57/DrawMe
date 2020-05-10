package fr.depp.drawme.utils;

public interface OnBackPressed {
    void onBackPressed(HandleOnBackPressed callback);

    interface HandleOnBackPressed {
        void onBackPressed(boolean goBack);
    }
}
