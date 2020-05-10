package fr.depp.drawme.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.CleanupService;
import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentWaitingRoomBinding;
import fr.depp.drawme.databinding.WaitingRoomItemBinding;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.OnCustomEventListener;
import fr.depp.drawme.models.Player;
import fr.depp.drawme.ui.viewHolders.WaitingRoomViewHolder;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.OnBackPressed;
import io.reactivex.rxjava3.disposables.Disposable;

public class WaitingRoomFragment extends Fragment implements OnBackPressed {

    private FragmentWaitingRoomBinding binding;
    private Disposable hasGameStartedSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // without this line, onCreateOptionsMenu() isn't called

        Intent intent = new Intent(requireActivity(), CleanupService.class);
        requireActivity().startService(intent);

        Game game = Game.getInstance();
        hasGameStartedSubscription = game.gameUpdatedSubject.subscribe(started -> {
            if (started) startGame();
        });
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
        } else if (!isGameAdmin) {
            Toasty.info(requireContext(), "Vous devez être le créateur de la partie pour la lancer").show();
        } else {
            Toasty.info(requireContext(), "Au moins 2 joueurs sont nécessaires pour lancer la partie").show();
        }
    }

    private void startGame() {
        Toasty.success(requireContext(), "La partie commence !").show();
        FragmentHelper.displayFragment(getParentFragmentManager(), new GameFragment(), false);
        hasGameStartedSubscription.dispose();
        Log.e("TAG", "startGame: " + Game.getInstance());
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
        hasGameStartedSubscription.dispose();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(HandleOnBackPressed callback) {
        Game.getInstance().destroyGame();
        callback.onBackPressed(true);
    }


    private static class WaitingRoomAdapter extends RecyclerView.Adapter<WaitingRoomViewHolder> {

        private Disposable usersSubscription;
        private List<Player> players;

        private WaitingRoomAdapter() {
            players = new ArrayList<>(6);
            usersSubscription = Game.getInstance().gameUpdatedSubject.subscribe(b -> updateData(Game.getInstance().getPlayers()));
            updateData(Game.getInstance().getPlayers()); // get the first value, because playersSubject didn't emit by now
        }

        private void updateData(List<Player> players) {
            this.players = players;
            notifyDataSetChanged();
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            usersSubscription.dispose();
            super.onDetachedFromRecyclerView(recyclerView);
        }

        @NonNull
        @Override
        public WaitingRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            WaitingRoomItemBinding binding = WaitingRoomItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new WaitingRoomViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull WaitingRoomViewHolder holder, int position) {
            holder.display(players.get(position));
        }

        @Override
        public int getItemCount() {
            return players.size();
        }
    }
}
