package com.example.quickowner;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

class ReverseGeocoder {

    static void reverseGeoCode(final Context context, final LatLng location) {
        Thread reverseGeocodeThread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                ReverseGeocodeListener reverseGeocodeListener = (ReverseGeocodeListener) context;
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    reverseGeocodeListener.returnAddress(addresses.get(0).getAddressLine(0));
                } catch (Exception e) {
                    System.out.println("Error");
                    e.printStackTrace();
                    reverseGeocodeListener.returnAddress(null);
                }
            }
        };
        reverseGeocodeThread.run();
    }

    public interface ReverseGeocodeListener {
        void returnAddress(String address);
    }
}
