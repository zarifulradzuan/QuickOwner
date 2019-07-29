package com.example.quickowner;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;
import java.util.Locale;

class ReverseGeocoder {

    static void reverseGeoCode(final Context context, final Location location) {
        Thread reverseGeocodeThread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                ReverseGeocodeListener reverseGeocodeListener = (ReverseGeocodeListener) context;
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address address = addresses.get(0);
                    String addressString = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        stringBuilder.append(address.getAddressLine(i));
                        stringBuilder.append("\n");
                    }

                    reverseGeocodeListener.returnAddress(addressString);
                } catch (Exception e) {
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
