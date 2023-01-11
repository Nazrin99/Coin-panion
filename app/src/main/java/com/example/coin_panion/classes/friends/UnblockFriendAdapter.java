package com.example.coin_panion.classes.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.User;
import com.example.coin_panion.classes.utility.ConfirmationDialog;

import java.util.ArrayList;
import java.util.List;

public class UnblockFriendAdapter extends RecyclerView.Adapter<UnblockFriendAdapter.ViewHolder>{

    private List<User> blockedfriends;

    public UnblockFriendAdapter() {
        blockedfriends = new ArrayList<>();
    }

    public UnblockFriendAdapter(List<User> blockedfriends) {
        this.blockedfriends = blockedfriends;
    }

    @NonNull
    @Override
    public UnblockFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.unblock_friend_item, parent, false);

        return new ViewHolder(view);
    }

    /*TODO complete action to fecth friend profile image and set the status back to false when popup is confirmed*/
    @Override
    public void onBindViewHolder(@NonNull UnblockFriendAdapter.ViewHolder holder, int position) {

        User user = blockedfriends.get(position);
        holder.TVUnblockUserName.setText(user.getFirstName()+" "+user.getLastName());

//        holder.IVUnblockProfile.setImageDrawable(user.);

        holder.BtnUnblockFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationDialog confirmationDialog = new ConfirmationDialog();

                if(confirmationDialog.isConfirmed()){

                    /*TODO if confirmed set the blocked status to false*/

                }else{

                    /*TODO dismiss the popup and back to original*/

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockedfriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView IVUnblockProfile;
        TextView TVUnblockUserName;
        Button BtnUnblockFriend;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            IVUnblockProfile = itemView.findViewById(R.id.IVUnblockProfile);
            TVUnblockUserName = itemView.findViewById(R.id.TVUnblockUserName);
            BtnUnblockFriend = itemView.findViewById(R.id.BtnUnblockFriend);

        }
    }

}
