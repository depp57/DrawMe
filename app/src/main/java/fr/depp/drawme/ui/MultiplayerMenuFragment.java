package fr.depp.drawme.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import javax.annotation.Nonnull;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.databinding.FragmentMultiplayerMenuBinding;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.firebase.FirestoreHelper;

public class MultiplayerMenuFragment extends Fragment {

    private FragmentMultiplayerMenuBinding binding;

    public static MultiplayerMenuFragment newInstance() {
        return new MultiplayerMenuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMultiplayerMenuBinding.inflate(inflater, container, false);

        binding.btnCreateServer.setOnClickListener(view -> onCreateServer());
        binding.btnJoinServer.setOnClickListener(view -> onJoinServer());
        binding.btnCancel.setOnClickListener(view -> FragmentHelper.displayPreviousFragment(requireActivity()));
        return binding.getRoot();
    }

    private void onJoinServer() {
        String serverName = binding.inputServerName.getText().toString();
        if (!serverName.equals("")) {
            FirestoreHelper.joinGame(getContext(), serverName.trim(), new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(String playerName) {
                    FragmentHelper.displayFragment(getParentFragmentManager(), WaitingRoomFragment.newInstance(serverName.trim(), playerName), true);
                }

                @Override
                public void onFailure(@Nonnull String error) {
                    Toasty.info(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toasty.info(requireContext(), "Le nom du serveur doit être renseigné", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCreateServer() {
        String serverName = binding.inputServerName.getText().toString();
        if (!serverName.equals("")) {
            FirestoreHelper.createGame(getContext(), serverName.trim(), new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(String playerName) {
                    FragmentHelper.displayFragment(getParentFragmentManager(), WaitingRoomFragment.newInstance(serverName.trim(), playerName), true);
                }

                @Override
                public void onFailure(@Nonnull String error) {
                    Toasty.error(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toasty.info(requireContext(), "Le nom du serveur doit être renseigné", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
