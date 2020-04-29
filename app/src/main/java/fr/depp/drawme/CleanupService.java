package fr.depp.drawme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import fr.depp.drawme.utils.firebase.FirestoreHelper;


public class CleanupService extends Service {

    private String gameName, playerName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String gameName = intent.getStringExtra("gameName");
        if (gameName != null) {
            this.gameName = gameName;
            this.playerName = intent.getStringExtra("playerName");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (gameName != null) {
            FirestoreHelper.removePlayer(gameName, playerName);
        }
    }
}
