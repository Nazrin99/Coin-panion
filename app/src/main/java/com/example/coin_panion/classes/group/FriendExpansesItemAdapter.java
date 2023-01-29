package com.example.coin_panion.classes.group;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.friends.ContactAdapter;
import com.example.coin_panion.classes.general.Account;
import com.example.coin_panion.classes.general.User;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendExpansesItemAdapter extends RecyclerView.Adapter<FriendExpansesItemAdapter.ViewHolder> {

    Context context;
    List<Account> allGroupMembers;
    Map<User, Double> selectedGroupMembers = new HashMap<>();
    List<Account> accounts = new ArrayList<>();
    boolean allSelected = false;
    double amount;

    public FriendExpansesItemAdapter(Context context, List<Account> allGroupMembers) {
        this.context = context;
        this.allGroupMembers = allGroupMembers;
    }

    @NonNull
    @Override
    public FriendExpansesItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /*Initialize view item*/
        View view = LayoutInflater.from(context).inflate(R.layout.custom_expenses_friend_item,parent,false);

        /*Return the view inflated*/
        return new FriendExpansesItemAdapter.ViewHolder(view);

    }



    @Override
    public void onBindViewHolder(@NonNull FriendExpansesItemAdapter.ViewHolder holder, int position) {

        Account account = allGroupMembers.get(position);

        /*Initialze the variable based on Friend Expanses*/
        holder.TVCustomExpansesFriendName.setText(account.getUser().getUsername());

        if(allSelected){
            holder.ETCustomExpansesItemAmount.setText(Double.toString(amount));
            holder.CBCustomExpansesItem.setChecked(true);
            holder.CBCustomExpansesItem.setEnabled(false);
            holder.ETCustomExpansesItemAmount.setEnabled(false);
        }
        else{
            holder.CBCustomExpansesItem.setChecked(false);
            holder.CBCustomExpansesItem.setEnabled(true);
            holder.ETCustomExpansesItemAmount.setEnabled(true);
            holder.ETCustomExpansesItemAmount.setText(null);
        }

        holder.CBCustomExpansesItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(holder.ETCustomExpansesItemAmount.getText().toString().isEmpty()){
                        holder.CBCustomExpansesItem.setChecked(false);
                        Toast.makeText(context, "You have to enter an amount before checking the box", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        System.out.println("Checked");
                        double amount = Double.parseDouble(holder.ETCustomExpansesItemAmount.getText().toString());

                        selectedGroupMembers.put(account.getUser(), amount);
                        accounts.add(account);
                        holder.ETCustomExpansesItemAmount.setEnabled(false);
                    }
                }else {
                    System.out.println("Not checked");
                    selectedGroupMembers.remove(account.getUser());
                    accounts.remove(account);
                    holder.ETCustomExpansesItemAmount.setText(null);
                    holder.ETCustomExpansesItemAmount.setEnabled(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return allGroupMembers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox CBCustomExpansesItem;
        TextView TVCustomExpansesFriendName;
        EditText ETCustomExpansesItemAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            CBCustomExpansesItem = itemView.findViewById(R.id.CBCustomExpansesItem);
            TVCustomExpansesFriendName = itemView.findViewById(R.id.TVCustomExpansesFriendName);
            ETCustomExpansesItemAmount = itemView.findViewById(R.id.ETCustomExpansesItemAmount);

        }

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Account> getAllGroupMembers() {
        return allGroupMembers;
    }

    public void setAllGroupMembers(List<Account> allGroupMembers) {
        this.allGroupMembers = allGroupMembers;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Map<User, Double> getSelectedGroupMembers() {
        return selectedGroupMembers;
    }

    public void setSelectedGroupMembers(Map<User, Double> selectedGroupMembers) {
        this.selectedGroupMembers = selectedGroupMembers;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
