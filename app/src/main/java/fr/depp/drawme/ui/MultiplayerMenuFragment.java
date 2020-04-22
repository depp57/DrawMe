package fr.depp.drawme.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fr.depp.drawme.databinding.FragmentMultiplayerMenuBinding;
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

        binding.btnCreateServer.setOnClickListener((view) -> handleCreateServer());
        binding.btnJoinServer.setOnClickListener((view) -> handleJoinServer());
        binding.btnCancel.setOnClickListener((view) -> handleCancel());
        return binding.getRoot();
    }

    private void handleCancel() {
        FragmentHelper.displayFragment(getParentFragmentManager(), new MainFragment());
    }

    private void handleJoinServer() {

    }

    private void handleCreateServer() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
