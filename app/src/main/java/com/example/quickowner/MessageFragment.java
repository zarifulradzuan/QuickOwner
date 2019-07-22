package com.example.quickowner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MessageFragment extends Fragment {
    Button sendMessage;
    EditText message;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        message = rootView.findViewById(R.id.edtMessage);
        sendMessage = rootView.findViewById(R.id.btnSend);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String placeId = getActivity().getSharedPreferences("quickowner", Context.MODE_PRIVATE).getString("placeid", null);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                String url="https://aesuneus.000webhostapp.com/qservice.php";
                getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        getActivity().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Message Successfully sent.", Toast.LENGTH_SHORT).show();
                        message.setText("");
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getActivity().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
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
