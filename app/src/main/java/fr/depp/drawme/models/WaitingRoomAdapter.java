package fr.depp.drawme.models;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.depp.drawme.databinding.WaitingRoomItemBinding;
import fr.depp.drawme.ui.viewHolders.WaitingRoomViewHolder;
import io.reactivex.rxjava3.disposables.Disposable;

public class WaitingRoomAdapter extends RecyclerView.Adapter<WaitingRoomViewHolder> {

    private Disposable usersSubscription;
    private List<Player> players;

    public WaitingRoomAdapter() {
        players = new ArrayList<>(6);
        usersSubscription = Game.getInstance().playersSubject.subscribe(this::updateData);
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
