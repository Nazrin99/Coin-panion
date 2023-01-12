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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;
import com.example.coin_panion.classes.friends.ContactAdapter;
import com.example.coin_panion.classes.general.User;

import java.util.HashMap;
import java.util.List;

public class FriendExpansesItemAdapter extends RecyclerView.Adapter<FriendExpansesItemAdapter.ViewHolder> {

    Context context;
    List<User> allGroupMembers;
    HashMap<Integer, Double> selectedGroupMembers;

    public FriendExpansesItemAdapter(Context context, List<User> allGroupMembers, HashMap<Integer, Double> selectedGroupMembers) {
        this.context = context;
        this.allGroupMembers = allGroupMembers;
        this.selectedGroupMembers = selectedGroupMembers;
    }

    @NonNull
    @Override
    public FriendExpansesItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /*Initialize view item*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_expenses_friend_item,parent,false);

        /*Return the view inflated*/
        return new FriendExpansesItemAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendExpansesItemAdapter.ViewHolder holder, int position) {

        User user = allGroupMembers.get(position);

        /*Initialze the variable based on Friend Expanses*/
        holder.TVCustomExpansesFriendName.setText(user.getFirstName());

//        Integer expansesAmount = Integer.valueOf(holder.ETCustomExpansesItemAmount.getText().toString().replaceAll("[^0-9]", ""));

        holder.CBCustomExpansesItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Double amount = Double.parseDouble(holder.ETCustomExpansesItemAmount.getText().toString());
                    selectedGroupMembers.put(user.getUserID(), amount);
                }else {
                    selectedGroupMembers.remove(user.getUserID());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return selectedGroupMembers.size();
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

    public List<User> getAllGroupMembers() {
        return allGroupMembers;
    }

    public void setAllGroupMembers(List<User> allGroupMembers) {
        this.allGroupMembers = allGroupMembers;
    }

    public HashMap<Integer, Double> getSelectedGroupMembers() {
        return selectedGroupMembers;
    }

    public void setSelectedGroupMembers(HashMap<Integer, Double> selectedGroupMembers) {
        this.selectedGroupMembers = selectedGroupMembers;
    }
}
