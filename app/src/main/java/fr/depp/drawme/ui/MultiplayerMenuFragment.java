package fr.depp.drawme.ui;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import fr.depp.drawme.databinding.FragmentMultiplayerMenuBinding;
import fr.depp.drawme.models.FirebaseService;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.UiHelper;

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

        binding.btnCreateServer.setOnClickListener((view) -> handleCreateServer());
        binding.btnJoinServer.setOnClickListener((view) -> handleJoinServer());
        binding.btnCancel.setOnClickListener((view) -> handleCancel());
        return binding.getRoot();
    }

    private void handleCancel() {
        FragmentHelper.displayFragment(getParentFragmentManager(), new MainFragment(), false);
    }

    private void handleJoinServer() {

    }

    private void handleCreateServer() {
        String serverName = binding.inputServerName.getText().toString();
        if (!serverName.equals("")) {
            FirebaseService.createGame(serverName.trim(), new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(String param) {
                    UiHelper.showToast(getContext(), "good", Toast.LENGTH_SHORT, Color.GREEN);
                }

                @Override
                public void onFailure(String error) {
                    UiHelper.showToast(getContext(), error, Toast.LENGTH_SHORT, Color.RED);
                }
            });
        }
        else {
            UiHelper.showToast(getContext(), "Le nom du serveur doit être renseigné", Toast.LENGTH_SHORT, Color.YELLOW);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
