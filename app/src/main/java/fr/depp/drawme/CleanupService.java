package fr.depp.drawme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import fr.depp.drawme.models.Game;


public class CleanupService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Game.getInstance().destroyGame();
        stopSelf();
    }
}
