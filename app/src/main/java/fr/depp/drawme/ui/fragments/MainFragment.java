package fr.depp.drawme.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.databinding.FragmentMainBinding;
import fr.depp.drawme.utils.FragmentHelper;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);

        binding.btnPlayMultiplayer.setOnClickListener(view -> onPlayMultiplayer());
        binding.btnPlayAi.setOnClickListener(view -> onPlayAi());
        return binding.getRoot();
    }

    private void onPlayMultiplayer() {
        FragmentHelper.displayFragment(getParentFragmentManager(), new MultiplayerMenuFragment());
    }

    private void onPlayAi() {
        Toasty.info(requireContext(), "L'IA sera bientôt implémentée !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
