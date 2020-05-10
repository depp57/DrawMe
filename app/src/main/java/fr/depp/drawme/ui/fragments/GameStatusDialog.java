package fr.depp.drawme.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import fr.depp.drawme.R;
import fr.depp.drawme.databinding.GameStatusItemBinding;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.Player;
import fr.depp.drawme.ui.viewHolders.GameStatusDialogViewHolder;

public class GameStatusDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate and set the layout for the dialog
        View view = View.inflate(context, R.layout.fragment_game_status, null);
        updateViewWithGameStatus(view);

        builder.setView(view)
                .setNeutralButton(R.string.back_to_game, (dialog, id) -> {

                });

        Dialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setWindowAnimations(R.style.dialog_animation);
        return dialog;
    }

    private void updateViewWithGameStatus(View view) {
        Game game = Game.getInstance();
        TextView textViewSecretWord = view.findViewById(R.id.textView_secret_word);

        if (game.isCurrentPlayer()) {
            textViewSecretWord.setText(game.getWordToGuess());
        }
        else {
            textViewSecretWord.setVisibility(View.GONE);
        }

        RecyclerView listPlayers = view.findViewById(R.id.list_players);
        // improve performance
        listPlayers.setHasFixedSize(true);

        listPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        listPlayers.setAdapter(new GameStatusDialogAdapter());
    }


    private static class GameStatusDialogAdapter extends RecyclerView.Adapter<GameStatusDialogViewHolder> {

        private final ArrayList<Player> players;

        private GameStatusDialogAdapter() {
            players = Game.getInstance().getPlayers();
        }

        @NonNull
        @Override
        public GameStatusDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GameStatusItemBinding binding = GameStatusItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new GameStatusDialogViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull GameStatusDialogViewHolder holder, int position) {
            holder.display(players.get(position));
        }

        @Override
        public int getItemCount() {
            return players.size();
        }
    }
}
