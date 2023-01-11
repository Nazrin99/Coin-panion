package com.example.coin_panion.classes.group;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.CreateGroupFragment;
import com.example.coin_panion.R;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    Context activityGroup;
    private List<Group> groups;

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
        ImageView IVGroupProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVGroupName = itemView.findViewById(R.id.TVGroupNameItem);
            IVGroupProfile = itemView.findViewById(R.id.IVGroupProfileItem);
            notifyDataSetChanged();
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            Group group = groups.get(position);

            Intent groupInfo = new Intent(activityGroup, CreateGroupFragment.class);

            groupInfo.putExtra("groupID",group.getGroupID());
            groupInfo.putExtra("groupName",group.getGroupName());
            groupInfo.putExtra("groupDesc",group.getGroupDesc());
        }
    }
}
