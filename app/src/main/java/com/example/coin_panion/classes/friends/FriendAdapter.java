package com.example.coin_panion.classes.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.fragments.friends.FriendsAddFragment;
import com.example.coin_panion.fragments.friends.FriendsAddFragmentDirections;
import com.example.coin_panion.fragments.friends.FriendsFragmentDirections;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    List<Contact> contacts;
    BaseViewModel friendsViewModel;
    View view;

    public FriendAdapter(List<Contact> contacts, BaseViewModel friendsViewModel, View view) {
        this.contacts = contacts;
        this.friendsViewModel = friendsViewModel;
        this.view = view;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.contactNameTextView);
            imageView = itemView.findViewById(R.id.selectContactImageView);
        }

        void bind(int position, Contact contact){
            textView.setText(contact.getContactName());
            imageView.setOnClickListener(v -> {
                friendsViewModel.put("phoneNumber", Long.parseLong(contact.getContactNumber()));

                NavDirections navDirections = FriendsFragmentDirections.actionFriendsFragmentToFriendsDetailFragment();
                Navigation.findNavController(view).navigate(navDirections);
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_from_acc_item,parent,false);

        /*Return the view inflated*/
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        holder.bind(position, contact);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
