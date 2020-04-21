package fr.depp.drawme.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.depp.drawme.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {

    private MainFragmentBinding binding;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);

        binding.btnPlayMultiplayer.setOnClickListener((view) -> handlePlayMultiplayer());
        binding.btnPlayAi.setOnClickListener((view) -> handlePlayAi());
        return binding.getRoot();
    }

    private void handlePlayMultiplayer() {
        
    }

    private void handlePlayAi() {
        Toast.makeText(getContext(), "L'IA sera bientôt implémentée !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
