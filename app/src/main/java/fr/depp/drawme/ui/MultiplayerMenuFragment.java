package fr.depp.drawme.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import es.dmoral.toasty.Toasty;
import fr.depp.drawme.databinding.FragmentMultiplayerMenuBinding;
import fr.depp.drawme.utils.firebase.FirebaseService;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.utils.FragmentHelper;

public class MultiplayerMenuFragment extends Fragment {

    private FragmentMultiplayerMenuBinding binding;

    public static MultiplayerMenuFragment newInstance() {
        return new MultiplayerMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMultiplayerMenuBinding.inflate(inflater, container, false);

        binding.btnCreateServer.setOnClickListener((view) -> onCreateServer());
        binding.btnJoinServer.setOnClickListener((view) -> onJoinServer());
        binding.btnCancel.setOnClickListener((view) -> onCancel());
        return binding.getRoot();
    }

    private void onCancel() {
        FragmentHelper.displayFragment(getParentFragmentManager(), new MainFragment(), false);
    }

    private void onJoinServer() {

    }

    private void onCreateServer() {
        String serverName = binding.inputServerName.getText().toString();
        if (!serverName.equals("")) {
            FirebaseService.createGame(getContext(), serverName.trim(), new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(String param) {

                }

                @Override
                public void onFailure(String error) {
                    Toasty.error(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toasty.warning(requireContext(), "Le nom du serveur doit être renseigné", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
