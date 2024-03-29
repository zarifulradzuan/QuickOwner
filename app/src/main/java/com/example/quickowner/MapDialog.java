package com.example.quickowner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapDialog extends DialogFragment {
    LocationManager locationManager;
    private GoogleMap googleMap;
    private Marker currentMarker;
    private Location currentLocation;
    private PremiseLocationInterface premiseLocationInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (!(PackageManager.PERMISSION_GRANTED == Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION))) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1337);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.map_dialog, null);
        premiseLocationInterface = (PremiseLocationInterface) getContext();
        builder.setView(view)
                .setTitle("Premise Location")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        premiseLocationInterface.setLocationOfPremise(currentMarker.getPosition());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        MapView mapView = view.findViewById(R.id.dialogMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                try {
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    };
                    locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                        currentLocation = location;
                    else {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null)
                            currentLocation = location;
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    if (currentLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude()),
                                15));
                    }
                }

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        currentMarker = googleMap.addMarker(new MarkerOptions()
                                .title("Premise location")
                                .position(latLng));

                    }
                });

            }
        });


        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    public interface PremiseLocationInterface {
        void setLocationOfPremise(LatLng locationOfPremise);
    }
}