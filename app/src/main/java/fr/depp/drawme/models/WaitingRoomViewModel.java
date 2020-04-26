package fr.depp.drawme.models;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

import fr.depp.drawme.utils.firebase.FirebaseService;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class WaitingRoomViewModel extends ViewModel {

    final PublishSubject<ArrayList<User>> playersSubject = PublishSubject.create();
    private final ListenerRegistration registration;

    public WaitingRoomViewModel(String gameName) {
        registration = FirebaseService.getRegistrationForGame(gameName, (data, e) -> {
            if (e != null) {
                Log.w("WaitingRoomViewModel", "Listen failed.", e);
                return;
            }

            if (data != null && data.exists()) {
                playersSubject.onNext(FirebaseService.deserializePlayersFromFirebase(data));
            }
            else {
                Log.w("WaitingRoomViewModel", "No data.");
            }
        });
    }

    @Override
    protected void onCleared() {
        registration.remove();
        super.onCleared();
    }
}
