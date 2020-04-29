package fr.depp.drawme.models;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import fr.depp.drawme.utils.firebase.FirestoreHelper;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class WaitingRoomViewModel extends ViewModel {

    final PublishSubject<List<User>> playersSubject = PublishSubject.create();
    private final PublishSubject<Boolean> hasGameStartedSubject = PublishSubject.create();
    private final ListenerRegistration playerRegistration;

    private List<User> cachedPlayers;
    private final String gameName;
    private final String playerName;

    public WaitingRoomViewModel(String gameName, String playerName) {
        this.gameName = gameName;
        this.playerName = playerName;

        playerRegistration = FirestoreHelper.listenerForGameChange(gameName, (data, e) -> {
            if (e != null) {
                return;
            }

            if (data != null && data.exists()) {
                if (data.get("started") != null) {
                    hasGameStartedSubject.onNext(true);
                }

                cachedPlayers = FirestoreHelper.deserializePlayersFromFirebaseToList(data);
                playersSubject.onNext(cachedPlayers);
            }
        });
    }

    public boolean isGameCreator() {
        return cachedPlayers.get(cachedPlayers.size() - 1).getUsername().equals(playerName);
    }

    @Override
    protected void onCleared() {
        playerRegistration.remove();
        super.onCleared();
    }

    public String getGameName() {
        return gameName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public PublishSubject<Boolean> getHasGameStartedSubject() {
        return hasGameStartedSubject;
    }
}
