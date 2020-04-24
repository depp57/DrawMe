package fr.depp.drawme.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        userSubscription = new AuthViewModel().userSubject.subscribe(this::updateUi);

        binding = FragmentAuthBinding.inflate(inflater, container, false);
        binding.btnCancel.setOnClickListener((view) -> onCancel());
        binding.btnSign.setOnClickListener((view) -> onSign());

        return binding.getRoot();
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

    private void onCancel() {
        FragmentHelper.displayFragment(getParentFragmentManager(), new MainFragment(), false);
    }
}
