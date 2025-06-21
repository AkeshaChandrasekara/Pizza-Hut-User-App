package com.myapp.pizzahut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


//import com.myapp.pizzahut.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.notificationTitle.setText(notification.getTitle());
        holder.notificationMessage.setText(notification.getMessage());
        holder.notificationTimestamp.setText(notification.getTimestamp());
        holder.notificationIcon.setImageResource(R.drawable.pizza_hut);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        ImageView notificationIcon;
        TextView notificationTitle, notificationMessage, notificationTimestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationTimestamp = itemView.findViewById(R.id.notificationTimestamp);
        }
    }
}
