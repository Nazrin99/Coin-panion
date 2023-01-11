package com.example.coin_panion.classes.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.ConfirmationDialog;

import java.util.ArrayList;
import java.util.List;

public class RemoveFriendAdapter extends RecyclerView.Adapter<RemoveFriendAdapter.ViewHolder>{

    /*Remove friend from a list of added friend*/
    List<User> users ;

    public RemoveFriendAdapter() {
        this.users = new ArrayList<>();
    }

    public RemoveFriendAdapter(List<User> users) {
        this.users = users;
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

        User user = users.get(position);

//        holder.IVRemoveFriendProfile.setImageDrawable(); TODO get profile image of other user

        holder.TVRemoveFriendUserName.setText(user.getFirstName() + " " + user.getLastName());

        holder.BtnRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationDialog confirmationDialog = new ConfirmationDialog();

                if(confirmationDialog.isConfirmed()){

                    users.remove(user);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView IVRemoveFriendProfile;
        TextView TVRemoveFriendUserName;
        Button BtnRemoveFriend;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            IVRemoveFriendProfile = itemView.findViewById(R.id.IVRemoveFriendProfile);
            TVRemoveFriendUserName = itemView.findViewById(R.id.TVRemoveFriendUserName);
            BtnRemoveFriend = itemView.findViewById(R.id.BtnRemoveFriend);

        }

    }
}
