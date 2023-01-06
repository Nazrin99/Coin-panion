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


    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO create layout for each notification
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout., parent, false);
//        return new ViewHolder(v);

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
//        holder.TVNotiDesc.setText(notification.getDesc());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView TVNotiDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            TVNotiDesc = itemView.findViewById(R.id.); TODO create recycler view
        }
    }
}

/*Code in fragment */
//NotificationAdapter adapter = new NotificationAdapter(data);
//    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//recyclerView.setAdapter(adapter);

