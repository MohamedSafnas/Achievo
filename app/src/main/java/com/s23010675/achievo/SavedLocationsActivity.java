package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SavedLocationsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);

        LinearLayout locationListContainer = findViewById(R.id.LocationListContainer);
        LinearLayout noLocationContainer = findViewById(R.id.noLocationContainer);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("locations")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        noLocationContainer.setVisibility(View.GONE);
                        locationListContainer.setVisibility(View.VISIBLE);

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String name = doc.getString("name");
                            double lat = doc.getDouble("latitude");
                            double lng = doc.getDouble("longitude");

                            TextView tv = new TextView(this);
                            tv.setText(name + " (" + lat + ", " + lng + ")");
                            tv.setPadding(10, 10, 10, 10);
                            tv.setTextSize(16);
                            locationListContainer.addView(tv);
                        }
                    } else {
                        noLocationContainer.setVisibility(View.VISIBLE);
                        locationListContainer.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load locations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


        setContentView(R.layout.activity_saved_locations);

        TextView addLocation = findViewById(R.id.addNewLocation);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        //navigate to Find Location page
        addLocation.setOnClickListener(v -> {
            Intent intent = new Intent(SavedLocationsActivity.this, FindLocationActivity.class);
            startActivity(intent);
        });

        //navigate to Dashboard page
        home.setOnClickListener(v -> {
            Intent intent = new Intent(SavedLocationsActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(SavedLocationsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
