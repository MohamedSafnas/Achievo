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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class FindLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Map;
    private AutoCompleteTextView searchLocation;
    private Button addButton,myLocations;
    private LatLng selectedLatLng;
    String locationName;
    private double passedLatitude = Double.NaN;
    private double passedLongitude = Double.NaN;
    private String passedLocationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_location);

        searchLocation = findViewById(R.id.search_location);
        addButton = findViewById(R.id.addLocation);
        myLocations = findViewById(R.id.locations);

        passedLatitude = getIntent().getDoubleExtra("latitude", Double.NaN);
        passedLongitude = getIntent().getDoubleExtra("longitude", Double.NaN);
        passedLocationName = getIntent().getStringExtra("locationName");

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

        //trigger when the add to list clicked
        addButton.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Toast.makeText(this, "Location added: " + locationName, Toast.LENGTH_SHORT).show();

                LocationModel location = new LocationModel(locationName, selectedLatLng.latitude, selectedLatLng.longitude);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                db.collection("users")
                        .document(uid)
                        .collection("locations")
                        .add(location)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
                            addButton.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
        });

        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        //navigate to saved location page
        myLocations.setOnClickListener(v -> {
            Intent intent = new Intent(FindLocationActivity.this, SavedLocationsActivity.class);
            startActivity(intent);
        });

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(FindLocationActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //navigate to Dashboard page
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

        LatLng initialLatLng;

        if (!Double.isNaN(passedLatitude) && !Double.isNaN(passedLongitude)) {
            // Use saved location
            initialLatLng = new LatLng(passedLatitude, passedLongitude);
            locationName = passedLocationName;
            addButton.setVisibility(View.VISIBLE);
        } else {
            // Default location
            initialLatLng = new LatLng(8.3114, 80.4037);
            locationName = "Default";
            addButton.setVisibility(View.GONE);
        }

        selectedLatLng = initialLatLng;
        Map.addMarker(new MarkerOptions().position(initialLatLng).title(locationName));
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15f));

        //allow to mark the location by tap on map
        Map.setOnMapClickListener(latLng -> {
            Map.clear();

            //reverse geocode code to get the address
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
    }

    private void searchAndZoom(String locationNameInput) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationNameInput, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

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

