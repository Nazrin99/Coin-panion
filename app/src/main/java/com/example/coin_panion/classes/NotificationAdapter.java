package com.example.coin_panion.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coin_panion.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView TVNotiTitle;
        TextView TVNotiDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVNotiTitle = itemView.findViewById(R.id.TVTitle);
            TVNotiDesc = itemView.findViewById(R.id.TVNotiDesc);
        }
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.TVNotiTitle.setText(notification.getNoti_title());
        holder.TVNotiDesc.setText(notification.getNoti_desc());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}

/*Code in fragment */
//NotificationAdapter adapter = new NotificationAdapter(data);
//    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//recyclerView.setAdapter(adapter);

