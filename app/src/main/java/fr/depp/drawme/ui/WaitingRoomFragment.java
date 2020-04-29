package fr.depp.drawme.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import fr.depp.drawme.CleanupService;
import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentWaitingRoomBinding;
import fr.depp.drawme.models.WaitingRoomAdapter;
import fr.depp.drawme.models.WaitingRoomViewModel;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.firebase.FirestoreHelper;

public class WaitingRoomFragment extends Fragment {

    private FragmentWaitingRoomBinding binding;
    private WaitingRoomViewModel viewModel;

    static WaitingRoomFragment newInstance(String gameName, String playerName) {
        WaitingRoomFragment fragment = new WaitingRoomFragment();
        Bundle args = new Bundle(1);
        args.putString("gameName", gameName);
        args.putString("playerName", playerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = requireArguments();
        String gameName = args.getString("gameName");
        String playerName = args.getString("playerName");

        Intent intent = new Intent(requireActivity(), CleanupService.class);
        intent.putExtras(args);
        requireActivity().startService(intent);

        viewModel = new WaitingRoomViewModel(gameName, playerName);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);

        binding.textViewGameName.setText(getString(R.string.game_name, viewModel.getGameName()));
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
        listPlayers.setAdapter(new WaitingRoomAdapter(viewModel));
    }

    @Override
    public void onDestroy() {
        // remove the player from the game in the database
        FirestoreHelper.removePlayer(viewModel.getGameName(), viewModel.getPlayerName());

        super.onDestroy();
    }
}
