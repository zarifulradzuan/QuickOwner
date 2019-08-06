package com.example.quickowner.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quickowner.R;
import com.example.quickowner.model.NotificationMessage;

import java.util.ArrayList;

public class NotificationMessageAdapter extends RecyclerView.Adapter<NotificationMessageAdapter.ViewHolder> {
    private ArrayList<NotificationMessage> notificationMessages = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(notificationMessages.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return notificationMessages.size();
    }

    public ArrayList<NotificationMessage> getNotificationMessages() {
        return notificationMessages;
    }

    public void setNotificationMessages(ArrayList<NotificationMessage> notificationMessages) {
        this.notificationMessages = notificationMessages;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageComment);
        }
    }
}
