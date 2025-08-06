package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SavedLocationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
