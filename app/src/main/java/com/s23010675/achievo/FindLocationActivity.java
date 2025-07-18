package com.s23010675.achievo;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class FindLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Map;
    private AutoCompleteTextView searchLocation;
    private Button addButton;
    private LatLng selectedLatLng;
    String locationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_location);


        searchLocation = findViewById(R.id.search_location);
        addButton = findViewById(R.id.addLocation);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //enable to search using enter key
        searchLocation.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {

                String location = searchLocation.getText().toString().trim();
                if (!location.isEmpty()) {
                    searchAndZoom(location);
                }
                return true;
            }
            return false;
        });

        addButton.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Toast.makeText(this, "Location added: " + locationName, Toast.LENGTH_SHORT).show();

                //code for save to database

                addButton.setVisibility(View.GONE);
            }
        });



        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);


        profile.setOnClickListener(v -> {
            Intent intent = new Intent(FindLocationActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(FindLocationActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map = googleMap;

        Map.getUiSettings().setZoomControlsEnabled(true);         // buttons for zoom
        Map.getUiSettings().setZoomGesturesEnabled(true);         // zoom
        Map.getUiSettings().setScrollGesturesEnabled(true);       // scrolling
        Map.getUiSettings().setRotateGesturesEnabled(true);       // rotation
        Map.getUiSettings().setTiltGesturesEnabled(true);         // tilt


        //allow to mark the location by tap on map
        Map.setOnMapClickListener(latLng -> {
            Map.clear();

            // Reverse geocode to get address
            Geocoder geocoder = new Geocoder(FindLocationActivity.this);
            locationName = "Selected Location";
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    locationName = address.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map.addMarker(new MarkerOptions().position(latLng).title(locationName));
            selectedLatLng = latLng;
            addButton.setVisibility(View.VISIBLE);
        });



        LatLng Location = new LatLng(8.3114, 80.4037);
        Map.addMarker(new MarkerOptions().position(Location));
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(Location, 15));
    }

    private void searchAndZoom(String locationNameInput) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationNameInput, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // ✅ Set location name correctly
                locationName = address.getAddressLine(0);

                Map.clear();
                Map.addMarker(new MarkerOptions().position(latLng).title(locationName));
                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                selectedLatLng = latLng;
                addButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error finding location", Toast.LENGTH_SHORT).show();
        }
    }

}

