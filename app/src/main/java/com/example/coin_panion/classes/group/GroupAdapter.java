package com.example.coin_panion.classes.group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groups;

    public GroupAdapter() {
        this.groups = new ArrayList<>();
    }

    /*Set with initial number of group*/
    public GroupAdapter(List<Group> groups) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView TVGroupName;
        ImageView IVGroupProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVGroupName = itemView.findViewById(R.id.TVGroupNameItem);
            IVGroupProfile = itemView.findViewById(R.id.IVGroupProfileItem);
        }

    }
}
