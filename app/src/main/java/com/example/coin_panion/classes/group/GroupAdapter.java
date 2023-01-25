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
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.CreateGroupFragment;
import com.example.coin_panion.GroupBalanceFragment;
import com.example.coin_panion.MainActivity;
import com.example.coin_panion.R;
import com.example.coin_panion.classes.utility.BaseViewModel;
import com.example.coin_panion.classes.utility.Picture;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    Context activityGroup;
    private List<Group> groups;
    int position;
    BaseViewModel mainViewModel;

    public GroupAdapter() {
        this.groups = new ArrayList<>();
    }

    /*Set with initial number of group*/
    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    public GroupAdapter(Context activityGroup, List<Group> groups, BaseViewModel mainViewModel) {
        this.activityGroup = activityGroup;
        this.groups = groups;
        this.mainViewModel = mainViewModel;
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
        holder.IVGroupProfileItem.setImageDrawable(group.getGroupPic().getPicture() != null ? Picture.cropToSquareAndRound(group.getGroupPic().getPicture(), activityGroup.getResources()) : Picture.cropToSquareAndRound(ResourcesCompat.getDrawable(activityGroup.getResources(), R.mipmap.default_profile, null), activityGroup.getResources()));
        holder.IVGroupInfoItem.setOnClickListener(v -> {

            mainViewModel.put("selectedGroup", group);
            Navigation.findNavController(v).navigate(R.id.groupInfoFragment);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TVGroupName;
        ImageView IVGroupProfileItem, IVGroupInfoItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVGroupName = itemView.findViewById(R.id.TVGroupNameItem);
            IVGroupProfileItem = itemView.findViewById(R.id.IVGroupProfileItem);
            IVGroupInfoItem = itemView.findViewById(R.id.IVGroupInfoItem);
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
