package fr.depp.drawme.ui.viewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.depp.drawme.databinding.WaitingRoomItemBinding;
import fr.depp.drawme.models.Player;

public class WaitingRoomViewHolder extends RecyclerView.ViewHolder {

    private WaitingRoomItemBinding binding;

    public WaitingRoomViewHolder(@NonNull WaitingRoomItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void display(Player player) {
        binding.textViewUsername.setText(player.getUsername());
    }
}
