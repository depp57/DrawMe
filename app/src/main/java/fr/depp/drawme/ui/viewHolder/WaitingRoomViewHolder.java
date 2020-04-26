package fr.depp.drawme.ui.viewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import fr.depp.drawme.databinding.WaitingRoomItemBinding;
import fr.depp.drawme.models.User;

public class WaitingRoomViewHolder extends RecyclerView.ViewHolder {

    private WaitingRoomItemBinding binding;

    public WaitingRoomViewHolder(@NonNull WaitingRoomItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void display(User user) {
        binding.textViewUsername.setText(user.getUsername());
    }
}
