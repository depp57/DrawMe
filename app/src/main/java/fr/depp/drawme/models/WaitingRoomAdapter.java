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
    private List<User> users;

    public WaitingRoomAdapter(WaitingRoomViewModel viewModel) {
        users = new ArrayList<>(6);
        usersSubscription = viewModel.playersSubject.subscribe(this::updateData);
    }

    private void updateData(List<User> users) {
        this.users = users;
        notifyDataSetChanged(); // TODO https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter#notifydatasetchanged
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
        holder.display(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
