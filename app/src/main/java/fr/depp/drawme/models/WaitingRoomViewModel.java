package fr.depp.drawme.models;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import fr.depp.drawme.utils.firebase.FirestoreHelper;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class WaitingRoomViewModel extends ViewModel {

    final PublishSubject<ArrayList<User>> playersSubject = PublishSubject.create();
    private final ListenerRegistration registration;
    private final String gameName;
    private final String playerName;

    public WaitingRoomViewModel(String gameName, String playerName) {
        this.gameName = gameName;
        this.playerName = playerName;

        registration = FirestoreHelper.listenerForGameChange(gameName, (data, e) -> {
            if (e != null) {
                return;
            }

            if (data != null && data.exists()) {
                playersSubject.onNext(FirestoreHelper.deserializePlayersFromFirebaseToList(data));
            }
        });
    }

    @Override
    protected void onCleared() {
        registration.remove();
        super.onCleared();
    }

    public String getGameName() {
        return gameName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
