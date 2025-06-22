package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PredictMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_method_selection);


        RadioGroup radioGroup = findViewById(R.id.predictionRadioGroup);


        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.bogBtn) {
            //based on your goals
        } else if (selectedId == R.id.bocBtn) {
            //based on custom activity
        }

        ImageView backI = findViewById(R.id.backI);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        backI.setOnClickListener(v -> {
            Intent intent = new Intent(PredictMethodActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(PredictMethodActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(PredictMethodActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }
}
