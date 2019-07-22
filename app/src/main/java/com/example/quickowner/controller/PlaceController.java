package com.example.quickowner.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.quickowner.DashboardFragment;
import com.example.quickowner.model.OpeningHours;
import com.example.quickowner.model.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public class PlaceController {
    private Place place;
    private FirebaseDatabase database;
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


    public static void getPlace(String placeId, final DashboardFragment dashboardFragment) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("places");
        Query query = databaseReference.orderByChild("placeId").startAt(placeId).endAt(placeId);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dashboardFragment.updatePlace(dataSnapshot.getValue(Place.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dashboardFragment.updatePlace(dataSnapshot.getValue(Place.class));
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
        }
        else if (today.getOpening()==null){
            return false;
        }
        else if(LocalTime.parse(today.getOpening()).isAfter(LocalTime.parse(today.getClosing()))){
            return !currentTime.isAfter(LocalTime.parse(today.getClosing())) || !currentTime.isBefore(LocalTime.parse(today.getOpening()));
        }
        else
            return currentTime.isAfter(LocalTime.parse(today.getOpening())) && currentTime.isBefore(LocalTime.parse(today.getClosing()));
    }
}
