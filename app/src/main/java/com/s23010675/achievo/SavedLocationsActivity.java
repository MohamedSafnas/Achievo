package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<LocationModel> locationList;
    private LinearLayout noLocationContainer;
    private LinearLayout addLocation;
    private ImageView home, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        recyclerView = findViewById(R.id.recyclerViewLocations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noLocationContainer = findViewById(R.id.noLocationContainer);
        addLocation = findViewById(R.id.addNewLocation);
        home = findViewById(R.id.homeI);
        profile = findViewById(R.id.profileI);

        locationList = new ArrayList<>();
        adapter = new LocationAdapter(this, locationList);
        recyclerView.setAdapter(adapter);

        loadLocationsFromFirestore();

        addLocation.setOnClickListener(v -> {
            // Navigate to Find Location page
            Intent intent = new Intent(SavedLocationsActivity.this, FindLocationActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(SavedLocationsActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(SavedLocationsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadLocationsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("locations")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error loading locations", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    locationList.clear();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot snapshot : querySnapshot) {
                            LocationModel location = snapshot.toObject(LocationModel.class);
                            if (location != null) {
                                location.setId(snapshot.getId());
                                locationList.add(location);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // Show/hide "No Locations" container
                    noLocationContainer.setVisibility(locationList.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(locationList.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }
}

