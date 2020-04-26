package fr.depp.drawme.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentWaitingRoomBinding;
import fr.depp.drawme.models.WaitingRoomAdapter;
import fr.depp.drawme.models.WaitingRoomViewModel;
import fr.depp.drawme.utils.FragmentHelper;

public class WaitingRoomFragment extends Fragment {

    private FragmentWaitingRoomBinding binding;
    private String gameName;

    static WaitingRoomFragment newInstance(String gameName) {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle(1);
        args.putString("gameName", gameName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameName = requireArguments().getString("gameName");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);

        binding.textViewGameName.setText(getString(R.string.game_name, gameName));
        binding.btnLeave.setOnClickListener(view -> FragmentHelper.displayPreviousFragment(requireActivity()));

        initRecyclerView();
        return binding.getRoot();
    }

    private void initRecyclerView() {
        RecyclerView listPlayers = binding.listPlayers;

        // improve performance
        listPlayers.setHasFixedSize(true);

        // use a linear layout
        listPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));

        // specify an adapter
        listPlayers.setAdapter(new WaitingRoomAdapter(new WaitingRoomViewModel(gameName)));
    }
}
