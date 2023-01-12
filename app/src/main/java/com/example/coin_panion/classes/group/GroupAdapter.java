package com.example.coin_panion.classes.group;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.CreateGroupFragment;
import com.example.coin_panion.GroupBalanceFragment;
import com.example.coin_panion.MainActivity;
import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    Context activityGroup;
    private List<Group> groups;
    int position;

    public GroupAdapter() {
        this.groups = new ArrayList<>();
    }

    /*Set with initial number of group*/
    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    public GroupAdapter(Context activityGroup, List<Group> groups) {
        this.activityGroup = activityGroup;
        this.groups = groups;

    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.TVGroupName.setText(group.getGroupName());
        holder.IVGroupProfile.setImageDrawable(group.getGroupPic().getPicture());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView TVGroupName;
        ImageView IVGroupProfile, IVGroupInfoItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVGroupName = itemView.findViewById(R.id.TVGroupNameItem);
            IVGroupProfile = itemView.findViewById(R.id.IVGroupProfileItem);
            IVGroupInfoItem = itemView.findViewById(R.id.IVGroupInfoItem);
            IVGroupInfoItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            position = getAdapterPosition();

            Group group = groups.get(position);

            System.out.println("Bitch");

//            MainActivity mainActivity = (MainActivity)(activityGroup.getApplicationContext());
//            BaseViewModel baseViewModel = mainActivity.getMainViewModel();
//            baseViewModel.put("groupID", group.getGroupID());

            Bundle bundle = new Bundle();
            bundle.putInt("groupID", group.getGroupID());
            Navigation.findNavController(v).navigate(R.id.groupInfoFragment, bundle);
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
