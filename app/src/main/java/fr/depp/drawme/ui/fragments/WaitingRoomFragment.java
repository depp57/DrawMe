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
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.models.WaitingRoomAdapter;
import fr.depp.drawme.utils.FragmentHelper;
import io.reactivex.rxjava3.disposables.Disposable;

public class WaitingRoomFragment extends Fragment {

    private FragmentWaitingRoomBinding binding;
    private Disposable hasGameStartedSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // without this line, onCreateOptionsMenu() isn't called

        Intent intent = new Intent(requireActivity(), CleanupService.class);
        requireActivity().startService(intent);

        Game game = Game.getInstance();
        hasGameStartedSubscription = game.hasGameStartedSubject.subscribe(b -> startGame());
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

        binding.textViewGameName.setText(getString(R.string.game_name, Game.getInstance().getName()));
        binding.btnLeave.setOnClickListener(view -> FragmentHelper.displayPreviousFragment(requireActivity()));
        binding.btnStartGame.setOnClickListener(view -> onStartGame());

        initRecyclerView();
        return binding.getRoot();
    }

    private void onStartGame() {
        Game game = Game.getInstance();
        boolean isGameAdmin = game.isAdmin();

        if (isGameAdmin && game.getPlayers().size() > 1) {
            Game.getInstance().startGame(game.getLocalPlayerName(), new OnCustomEventListener<String>() {
                @Override
                public void onSuccess(@javax.annotation.Nullable String success) {}

                @Override
                public void onFailure(@Nonnull String error) {
                    Toasty.info(requireContext(), error).show();
                }
            });
        }
        else if (!isGameAdmin){
            Toasty.info(requireContext(), "Vous devez être le créateur de la partie pour la lancer").show();
        }
        else {
            Toasty.info(requireContext(), "Au moins 2 joueurs sont nécessaires pour lancer la partie").show();
        }
    }

    private void startGame() {
        Toasty.success(requireContext(), "La partie commence !").show();
        FragmentHelper.displayFragment(getParentFragmentManager(), new GameFragment());
        hasGameStartedSubscription.dispose();
    }

    private void initRecyclerView() {
        RecyclerView listPlayers = binding.listPlayers;
        // improve performance
        listPlayers.setHasFixedSize(true);

        listPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        listPlayers.setAdapter(new WaitingRoomAdapter());
    }

    @Override
    public void onDestroy() {
        // remove the player from the game in the database
        Game.getInstance().removeLocalPlayer();

        hasGameStartedSubscription.dispose();
        super.onDestroy();
    }
}
