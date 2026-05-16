package com.example.quizzapp_badreddineoussaih;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private TextView tvMapCity, tvMapCoords;
    private Button bBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        tvMapCity = findViewById(R.id.tvMapCity);
        tvMapCoords = findViewById(R.id.tvMapCoords);
        bBack = findViewById(R.id.bBackToMenu);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        bBack.setOnClickListener(v -> finish());

        updateUI();
    }

    private void updateUI() {
        if (LocationHelper.isLocationReady) {
            tvMapCity.setText(String.format("City: %s", LocationHelper.cityName));
            tvMapCoords.setText(String.format(Locale.US, "Coords: %.4f, %.4f", 
                    LocationHelper.latitude, LocationHelper.longitude));
        } else {
            tvMapCity.setText("City: Signal Lost");
            tvMapCoords.setText("Coords: Unknown");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "Map is ready");

        // UI Settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (LocationHelper.isLocationReady) {
            LatLng userLocation = new LatLng(LocationHelper.latitude, LocationHelper.longitude);
            
            // Custom Gaming Marker (Using a built-in color for stability, could be a drawable)
            mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .title("Player Location")
                    .snippet("Mission Active")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            // Smooth camera animation
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
        } else {
            Toast.makeText(this, "Waiting for GPS signal...", Toast.LENGTH_SHORT).show();
        }
    }
}