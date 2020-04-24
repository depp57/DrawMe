package fr.depp.drawme.models;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class AuthViewModel extends ViewModel {

    public final PublishSubject<FirebaseAuth> userSubject = PublishSubject.create();

    public AuthViewModel() {
        FirebaseAuth.getInstance().addAuthStateListener(userSubject::onNext);
        // emit the first value
        userSubject.onNext(FirebaseAuth.getInstance());
    }
}
