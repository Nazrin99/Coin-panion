package com.example.coin_panion.classes.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.Picture;

import java.util.ArrayList;
import java.util.List;

public class RemoveFriendAdapter extends RecyclerView.Adapter<RemoveFriendAdapter.ViewHolder>{

    /*Remove friend from a list of added friend*/
    List<Account> groupMembers;
    Context context;
    List<Account> removedMembers = new ArrayList<>();

    public RemoveFriendAdapter(List<Account> groupMembers, Context context) {
        this.groupMembers = groupMembers;
        this.context = context;
    }

    public List<Account> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<Account> removedMembers) {
        this.removedMembers = removedMembers;
    }

    @NonNull
    @Override
    public RemoveFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.remove_friend_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemoveFriendAdapter.ViewHolder holder, int position) {

        Account account = groupMembers.get(position);

        holder.removedFriendImageView.setImageDrawable(account.getAccountPic().getPicture() != null ? Picture.cropToSquareAndRound(account.getAccountPic().getPicture(), context.getResources()) : Picture.cropToSquareAndRound(ResourcesCompat.getDrawable(context.getResources(), R.mipmap.default_profile, null), context.getResources()));
        holder.removeFriendUsernameTextView.setText(account.getUser().getUsername());
        holder.removeFriendButton.setOnClickListener(v -> {
            if(holder.removeFriendButton.getText().toString().equalsIgnoreCase("Undo")){
                removedMembers.remove(account);
                holder.removeFriendButton.setText("Remove");
            }
            else if(holder.removeFriendButton.getText().toString().equalsIgnoreCase("Remove")){
                removedMembers.add(account);
                holder.removeFriendButton.setText("Undo");
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView removedFriendImageView;
        TextView removeFriendUsernameTextView;
        AppCompatButton removeFriendButton;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            removedFriendImageView = itemView.findViewById(R.id.removeFriendImageView);
            removeFriendUsernameTextView = itemView.findViewById(R.id.removeFriendUsernameTextView);
            removeFriendButton = itemView.findViewById(R.id.removeFriendButton);
        }

    }

    public List<Account> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<Account> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
