package fr.depp.drawme.ui.fragments;


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
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.utils.FragmentHelper;

public class MultiplayerMenuFragment extends Fragment {

    private FragmentMultiplayerMenuBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMultiplayerMenuBinding.inflate(inflater, container, false);

        binding.btnCreateServer.setOnClickListener(view -> onServer("create"));
        binding.btnJoinServer.setOnClickListener(view -> onServer("join"));
        binding.btnCancel.setOnClickListener(view -> FragmentHelper.displayPreviousFragment(requireActivity()));
        return binding.getRoot();
    }

    private void onServer(String create_or_join) {
        String gameName = binding.inputServerName.getText().toString().trim();
        if (!gameName.equals("")) {
            Game game = Game.getInstance();
            binding.progressBar.setVisibility(View.VISIBLE);

            OnCustomEventListener<String> callback = new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(String playerName) {
                    // if the user switch to another fragment before the game was created/joined cancel the action
                    if (!isVisible()) {
                        game.removePlayer(playerName);
                        return;
                    }

                    FragmentHelper.displayFragment(getParentFragmentManager(), new WaitingRoomFragment(), true);
                }

                @Override
                public void onFailure(@Nonnull String error) {
                    if (!isVisible()) return;

                    binding.progressBar.setVisibility(View.GONE);
                    Toasty.info(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            };

            if (create_or_join.equals("create")) game.createGame(getContext(), gameName, callback);
            else game.joinGame(getContext(), gameName, callback);
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
