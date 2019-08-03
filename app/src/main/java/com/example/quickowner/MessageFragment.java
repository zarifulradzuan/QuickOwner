package com.example.quickowner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickowner.adapter.NotificationMessageAdapter;
import com.example.quickowner.controller.NotificationMessageController;
import com.example.quickowner.controller.SwipeToAction;
import com.example.quickowner.model.NotificationMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MessageFragment extends Fragment {
    Button sendMessage;
    EditText message;
    RecyclerView notificationMessageRecycler;
    NotificationMessageAdapter notificationMessageAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String placeId = mAuth.getUid();

        message = rootView.findViewById(R.id.edtMessage);
        sendMessage = rootView.findViewById(R.id.btnSend);


        notificationMessageRecycler = rootView.findViewById(R.id.messageRecycler);
        notificationMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationMessageAdapter = new NotificationMessageAdapter();
        notificationMessageRecycler.setAdapter(notificationMessageAdapter);
        NotificationMessageController.getNotificationMessages(notificationMessageAdapter, placeId);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToAction(notificationMessageAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(notificationMessageRecycler);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (message.getText().toString().equals("") || message.getText().toString().equals(null))
                    return;


                NotificationMessageController.insertNotificationMessage(new NotificationMessage(placeId, message.getText().toString()));

                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                String url="https://aesuneus.000webhostapp.com/qservice.php";
                getActivity().findViewById(R.id.fullnessProgress).setVisibility(View.VISIBLE);
                message.setEnabled(false);
                sendMessage.setEnabled(false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        getActivity().findViewById(R.id.fullnessProgress).setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Successfully sent.", Toast.LENGTH_SHORT).show();
                        message.setText("");
                        message.setEnabled(true);
                        sendMessage.setEnabled(true);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getActivity().findViewById(R.id.fullnessProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Failed to send message.", Toast.LENGTH_SHORT).show();
                                message.setEnabled(true);
                                sendMessage.setEnabled(true);
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("placeId",placeId);
                        params.put("message",message.getText().toString());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
        return rootView;
    }
}
