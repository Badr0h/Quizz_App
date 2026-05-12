package com.example.quizzapp_badreddineoussaih;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    public static double latitude = 0;
    public static double longitude = 0;
    public static String cityName = "Unknown Location";
    public static boolean isLocationReady = false;

    public static void initLocation(Activity activity) {
        Log.d(TAG, "Initializing Location...");
        
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            Log.d(TAG, "Permissions not granted. Requesting...");
            ActivityCompat.requestPermissions(activity, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    cityName = getCityName(activity, latitude, longitude);
                    isLocationReady = true;
                    Log.d(TAG, "Location acquired: " + cityName + " (" + latitude + ", " + longitude + ")");
                } else {
                    Log.w(TAG, "Location is null. GPS might be disabled.");
                    isLocationReady = false;
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get location", e);
            isLocationReady = false;
        });
    }

    private static String getCityName(Context context, double lat, double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                return (city != null) ? city : "Unknown City";
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder service unavailable", e);
        }
        return "Unknown City";
    }
}