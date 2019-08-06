package com.example.quickowner.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.quickowner.adapter.NotificationMessageAdapter;
import com.example.quickowner.model.NotificationMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class NotificationMessageController {

    public static void insertNotificationMessage(NotificationMessage notificationMessage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("notifications/" + notificationMessage.getIdPlace());
        databaseReference = databaseReference.push();
        databaseReference.setValue(notificationMessage);
    }

    static void deleteNotificationMessage(NotificationMessage notificationMessage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("notifications/"
                + notificationMessage.getIdPlace()
                + "/" + notificationMessage.getIdMessage());
        databaseReference.removeValue();
    }

    public static void getNotificationMessages(final NotificationMessageAdapter notificationMessageAdapter, String placeId) {
        final ArrayList<NotificationMessage> notificationMessages = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query databaseQuery = database.getReference("notifications/" + placeId);
        databaseQuery.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                NotificationMessage notificationMessage;
                try {
                    notificationMessage = dataSnapshot.getValue(NotificationMessage.class);
                    if (notificationMessage != null) {
                        notificationMessage.setIdMessage(dataSnapshot.getKey());
                    }
                    notificationMessages.add(notificationMessage);
                    notificationMessageAdapter.setNotificationMessages(notificationMessages);
                    notificationMessageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NotificationMessage notificationMessage;
                try {
                    notificationMessage = dataSnapshot.getValue(NotificationMessage.class);
                    if (notificationMessage != null) {
                        notificationMessage.setIdMessage(dataSnapshot.getKey());
                    }
                    notificationMessages.add(notificationMessage);
                    notificationMessageAdapter.setNotificationMessages(notificationMessages);
                    notificationMessageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }


}
