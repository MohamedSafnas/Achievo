package com.s23010675.achievo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LocationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<LocationModel> locationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_location);

        recyclerView = findViewById(R.id.recyclerViewLocations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationList = new ArrayList<>();
        adapter = new LocationAdapter(this, locationList);
        recyclerView.setAdapter(adapter);

        loadLocationsFromFirestore();

        findViewById(R.id.addNewLocation).setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            LocationModel newLocation = new LocationModel();
            newLocation.setName("New Place");

            db.collection("locations")
                    .add(newLocation)
                    .addOnSuccessListener(docRef -> {
                        newLocation.setId(docRef.getId());
                        Toast.makeText(this, "Location added", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show()
                    );
        });

    }


    private void loadLocationsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("locations")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        // Handle error
                        return;
                    }

                    locationList.clear();
                    if (querySnapshot != null) {
                        for (DocumentSnapshot snapshot : querySnapshot) {
                            LocationModel location = snapshot.toObject(LocationModel.class);
                            if (location != null) {
                                location.setId(snapshot.getId()); // store Firestore doc ID
                                locationList.add(location);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

                    // Show/hide "No Locations" view
                    findViewById(R.id.noLocationContainer)
                            .setVisibility(locationList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }


}
