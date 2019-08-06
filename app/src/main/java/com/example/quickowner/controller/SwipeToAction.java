package com.example.quickowner.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickowner.R;
import com.example.quickowner.adapter.NotificationMessageAdapter;
import com.example.quickowner.model.NotificationMessage;

import java.util.HashMap;
import java.util.Map;

public class SwipeToAction extends ItemTouchHelper.SimpleCallback {

    private NotificationMessageAdapter notificationMessageAdapter;
    private Context context;

    public SwipeToAction(NotificationMessageAdapter notificationMessageAdapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.notificationMessageAdapter = notificationMessageAdapter;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        final NotificationMessage notificationMessage = notificationMessageAdapter.getNotificationMessages().get(position);

        if (i == ItemTouchHelper.LEFT) {
            NotificationMessageController.deleteNotificationMessage(notificationMessage);
            Toast.makeText(context, "Message deleted.", Toast.LENGTH_SHORT).show();
        } else if (i == ItemTouchHelper.RIGHT) {
            Toast.makeText(context, "Sending message...", Toast.LENGTH_SHORT).show();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getString(R.string.hostUrl), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Message sent.", Toast.LENGTH_SHORT).show();
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Failed to send message.", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("placeId", notificationMessage.getIdPlace());
                    params.put("message", notificationMessage.getMessage());
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }

        NotificationMessageController.getNotificationMessages(notificationMessageAdapter, notificationMessage.getIdPlace());
    }
}
