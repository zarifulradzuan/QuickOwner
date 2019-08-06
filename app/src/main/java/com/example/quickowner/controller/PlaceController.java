package com.example.quickowner.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickowner.R;
import com.example.quickowner.TrendFragment;
import com.example.quickowner.model.OpeningHours;
import com.example.quickowner.model.Place;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceController {
    public static final int MODE_WEEKLY = 1;
    public static final int MODE_DAILY = 2;
    private static final String[] daysList = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private Place place;
    private FirebaseDatabase database;
    private PlaceListener placeListener;
    private DatabaseReference databaseReference;
    public PlaceController(Place place) {
        this.place = place;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("places");
    }

    public PlaceController() {
        this.place = place;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("places");
    }
    public static void insertPlace(Place place){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("places");
        DatabaseReference placeRef = databaseReference.child(place.getPlaceId());
        placeRef.setValue(place);
    }

    public void setPlaceListener(PlaceListener placeListener) {
        this.placeListener = placeListener;
    }

    public void getTrendData(final String placeId, final int mode, final BarChart trendChart, TrendFragment trendFragment) {
        final List<BarEntry> entries = new ArrayList<>();
        Context context = trendFragment.getContext();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getString(R.string.hostUrl), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray dataSetJSON = null;

                final ArrayList<String> labels = new ArrayList<>();
                try {
                    dataSetJSON = new JSONArray(response);
                } catch (JSONException e) {
                    Log.d("ERROR", "Failed to decode response");
                }
                if (dataSetJSON.length() == 0)
                    return;
                if (mode == MODE_WEEKLY) {
                    try {
                        for (int i = 0; i < 7; i++) {
                            labels.add(daysList[i]);
                            for (int j = 0; j < dataSetJSON.length(); j++) {
                                JSONObject currentObj = dataSetJSON.getJSONObject(j);
                                if (currentObj.getString("Day").equals(daysList[i])) {
                                    entries.add(new BarEntry(i, currentObj.getInt("Average")));
                                    break;
                                }
                                if (j == dataSetJSON.length() - 1)
                                    entries.add(new BarEntry(i, 0));

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        for (int i = 0; i < 24; i++) {
                            labels.add(i + ":00");
                            for (int j = 0; j < dataSetJSON.length(); j++) {
                                JSONObject currentObj = dataSetJSON.getJSONObject(j);
                                if (currentObj.getInt("Hour") == i) {
                                    entries.add(new BarEntry(i, currentObj.getInt("Average")));
                                    break;
                                }
                                if (j == dataSetJSON.length() - 1)
                                    entries.add(new BarEntry(i, 0));

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ValueFormatter valueFormatter = new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        Float valueObj = value;
                        try {
                            return labels.get((valueObj.intValue()));
                        } catch (IndexOutOfBoundsException e) {
                            return "";
                        }
                    }
                };
                BarDataSet barDataSet = new BarDataSet(entries, "Percentage of fullness");
                BarData barData = new BarData(barDataSet);
                trendChart.getXAxis().setGranularity(1f);
                trendChart.getXAxis().setValueFormatter(valueFormatter);
                trendChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                trendChart.setData(barData);
                trendChart.resetZoom();
                if (mode == MODE_DAILY) {
                    trendChart.zoom(0f, 1f, 0, 0);
                } else
                    trendChart.zoom(3.5f, 1f, 0, 0);
                trendChart.invalidate();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("trendPlaceId", placeId);
                params.put("mode", String.valueOf(mode));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getPlace(String placeId) throws Exception {
        if (placeListener == null)
            throw new Exception("No listener set");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("places");
        Query query = databaseReference.orderByChild("placeId").startAt(placeId).endAt(placeId);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("Received");
                placeListener.returnPlace(dataSnapshot.getValue(Place.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                placeListener.returnPlace(dataSnapshot.getValue(Place.class));
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


    public MarkerOptions getPlaceMarker(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(this.place.getPlaceName());
        markerOptions.position(new LatLng(this.place.getPlaceLatitude(),this.place.getPlaceLongitude()));
        if(!isOpen()){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            return markerOptions;
        }
        if(getFullness()<49)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        else if(getFullness()<85)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        else
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        return markerOptions;
    }

    public int getFullness(){
        double currentOccupancy, maxOccupancy;
        currentOccupancy = this.place.getCurrentOccupancy();
        maxOccupancy = this.place.getMaxOccupancy();
        double fullnessInDouble = currentOccupancy/maxOccupancy;
        fullnessInDouble*=100;
        return (int) fullnessInDouble;
    }

    public boolean isOpen(){
        final int OPEN = 1;
        final int CLOSED = 0;

        if(place.getOverrideStatus() == OPEN)
            return true;
        if(place.getOverrideStatus() == CLOSED)
            return false;

        if(place.getOpeningHours().isEmpty()){
            return false;
        }
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();
        OpeningHours today = place.getOpeningHours().get(currentDate.getDayOfWeek().getValue()-1);
        if(today.getOpening().equals(today.getClosing())) {
            return true;
        } else if (today.getOpening() == null) {
            return false;
        }
        else if(LocalTime.parse(today.getOpening()).isAfter(LocalTime.parse(today.getClosing()))){
            return !currentTime.isAfter(LocalTime.parse(today.getClosing())) || !currentTime.isBefore(LocalTime.parse(today.getOpening()));
        }
        else
            return currentTime.isAfter(LocalTime.parse(today.getOpening())) && currentTime.isBefore(LocalTime.parse(today.getClosing()));
    }

    public interface PlaceListener {
        void returnPlace(Place place);
    }
}
