package fr.depp.drawme.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentAuthBinding;
import fr.depp.drawme.models.AuthViewModel;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.firebase.FirebaseAuthWrapper;
import io.reactivex.rxjava3.disposables.Disposable;

public class AuthFragment extends Fragment {

    private FragmentAuthBinding binding;
    private Disposable userSubscription;
    private boolean isAuth;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userSubscription = new AuthViewModel().userSubject.subscribe(this::updateUi);

        binding = FragmentAuthBinding.inflate(inflater, container, false);
        binding.btnCancel.setOnClickListener(view -> FragmentHelper.displayPreviousFragment(requireActivity()));
        binding.btnSign.setOnClickListener(view -> onSign());

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        userSubscription.dispose();
        super.onDestroy();
    }

    private void updateUi(FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        // check whether the user is disconnected
        if (user == null) {
            isAuth = false;
            binding.accountStats.setVisibility(View.GONE);
            binding.btnSign.setText(R.string.sign_in);
        }
        else {
            isAuth = true;
            binding.accountStats.setVisibility(View.VISIBLE);
            binding.btnSign.setText(R.string.sign_out);

            binding.textViewUsername.setText(getString(R.string.hi_username, user.getDisplayName()));

            // TODO mettre à jour les autres stats en faisant un lien FirebaseUser -> Database
        }
    }

    private void onSign() {
        // check whether the user is disconnected
        if (isAuth) {
            FirebaseAuth.getInstance().signOut();
            Toasty.success(requireContext(), "Vous êtes bien déconnecté", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseAuthWrapper.signIn(requireActivity());
        }
    }
}
