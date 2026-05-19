package com.example.quizzapp_badreddineoussaih;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    public static double latitude = 0;
    public static double longitude = 0;
    public static String cityName = "Unknown Location";
    public static boolean isLocationReady = false;

    private static FusedLocationProviderClient fusedLocationClient;
    private static LocationCallback locationCallback;

    public interface LocationResultListener {
        void onLocationResult(double lat, double lon, String city);
    }

    public static void initLocation(Activity activity) {
        initLocation(activity, null);
    }

    public static void initLocation(Activity activity, LocationResultListener listener) {
        Log.d(TAG, "Initializing Location...");
        
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Permissions not granted. Requesting...");
            ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        }

        // Get last known location first for quick result
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                updateLocationData(activity, location, listener);
            } else {
                Log.w(TAG, "Last location is null, requesting fresh update.");
                requestFreshLocation(activity, listener);
            }
        });
    }

    private static void requestFreshLocation(Activity activity, LocationResultListener listener) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdates(1)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    updateLocationData(activity, locationResult.getLastLocation(), listener);
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private static void updateLocationData(Context context, Location location, LocationResultListener listener) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        cityName = getCityName(context, latitude, longitude);
        isLocationReady = true;
        Log.d(TAG, "Location acquired: " + cityName + " (" + latitude + ", " + longitude + ")");
        if (listener != null) {
            listener.onLocationResult(latitude, longitude, cityName);
        }
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