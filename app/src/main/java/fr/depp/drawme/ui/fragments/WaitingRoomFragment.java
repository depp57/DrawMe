package fr.depp.drawme.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import javax.annotation.Nonnull;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.CleanupService;
import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentWaitingRoomBinding;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.models.WaitingRoomAdapter;
import fr.depp.drawme.models.WaitingRoomViewModel;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.firebase.FirestoreHelper;
import io.reactivex.rxjava3.disposables.Disposable;

public class WaitingRoomFragment extends Fragment {

    private FragmentWaitingRoomBinding binding;
    private WaitingRoomViewModel viewModel;
    private Disposable hasGameStartedSubscription;

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
        setHasOptionsMenu(true); // without this line, onCreateOptionsMenu() isn't called

        Bundle args = requireArguments();
        String gameName = args.getString("gameName");
        String playerName = args.getString("playerName");

        Intent intent = new Intent(requireActivity(), CleanupService.class);
        intent.putExtras(args);
        requireActivity().startService(intent);

        viewModel = new WaitingRoomViewModel(gameName, playerName);
        hasGameStartedSubscription = viewModel.getHasGameStartedSubject().subscribe(b -> startGame());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_account).setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWaitingRoomBinding.inflate(inflater, container, false);

        binding.textViewGameName.setText(getString(R.string.game_name, viewModel.getGameName()));
        binding.btnLeave.setOnClickListener(view -> FragmentHelper.displayPreviousFragment(requireActivity()));
        binding.btnStartGame.setOnClickListener(view -> onStartGame());

        initRecyclerView();
        return binding.getRoot();
    }

    private void onStartGame() {
        if (viewModel.isGameCreator()) {
            FirestoreHelper.startGame(viewModel.getGameName(), new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(@javax.annotation.Nullable String success) {}

                @Override
                public void onFailure(@Nonnull String error) {
                    Toasty.info(requireContext(), error).show();
                }
            });
        }
        else {
            Toasty.info(requireContext(), "Vous devez être le créateur de la partie pour la lancer").show();
        }
    }

    private void startGame() {
        Toasty.success(requireContext(), "La partie commence !").show();
        FragmentHelper.displayFragment(getParentFragmentManager(), GameFragment.newInstance());
    }

    private void initRecyclerView() {
        RecyclerView listPlayers = binding.listPlayers;
        // improve performance
        listPlayers.setHasFixedSize(true);

        listPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        listPlayers.setAdapter(new WaitingRoomAdapter(viewModel));
    }

    @Override
    public void onDestroy() {
        // remove the player from the game in the database
        FirestoreHelper.removePlayer(viewModel.getGameName(), viewModel.getPlayerName());

        hasGameStartedSubscription.dispose();
        super.onDestroy();
    }
}
