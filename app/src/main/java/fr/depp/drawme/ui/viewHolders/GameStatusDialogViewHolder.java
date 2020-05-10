package fr.depp.drawme.ui.viewHolders;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.depp.drawme.databinding.GameStatusItemBinding;
import fr.depp.drawme.models.Player;

public class GameStatusDialogViewHolder extends RecyclerView.ViewHolder {

    private GameStatusItemBinding binding;

    public GameStatusDialogViewHolder(@NonNull GameStatusItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void display(Player player) {
        binding.textViewUsername.setText(player.getUsername());
        binding.textViewScore.setText(String.valueOf(player.getScore()));
    }
}
