package com.example.coin_panion.classes.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.fragments.friends.FriendsFragmentDirections;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    List<Account> accounts;
    BaseViewModel mainViewModel;
    Context context;

    public FriendAdapter(List<Account> accounts, BaseViewModel mainViewModel, Context context) {
        this.accounts = accounts;
        this.mainViewModel = mainViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_from_acc_item,parent,false);

        /*Return the view inflated*/
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);

        holder.contactNameTextView.setText(account.getUser().getUsername());
        holder.selectContactImageView.setOnClickListener(v -> {
            mainViewModel.put("selectedFriend", account);
            NavDirections navDirections = FriendsFragmentDirections.actionFriendsFragmentToFriendsDetailFragment();
            Navigation.findNavController(v).navigate(navDirections);
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView contactNameTextView;
        ImageView selectContactImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactNameTextView = itemView.findViewById(R.id.creditorUsernameTextView);
            selectContactImageView = itemView.findViewById(R.id.selectContactImageView);
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public BaseViewModel getMainViewModel() {
        return mainViewModel;
    }

    public void setMainViewModel(BaseViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
