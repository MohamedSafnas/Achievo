package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PredictOutcomeActivity extends AppCompatActivity {

    private TextView tvPredictionTitle, tvPredictionOutcome;
    private ImageView btnBackToHome;
    private Button btnPredictions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_outcome);



        tvPredictionTitle = findViewById(R.id.tvPredictionTitle);
        tvPredictionOutcome = findViewById(R.id.tvPredictionOutcome);
        btnBackToHome = findViewById(R.id.backI);
        btnPredictions = findViewById(R.id.btnPredictions);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);


        // Receive prediction data from previous activity
        Intent intent = getIntent();
        String predictionType = intent.getStringExtra("predictionType");
        String goalName = intent.getStringExtra("goalName");
        String title = intent.getStringExtra("title");
        String predictionOutcome = intent.getStringExtra("predictionOutcome"); // response from gemini api


        if (predictionType != null && predictionType.equals("goal") && goalName != null) {
            tvPredictionTitle.setText(goalName);   // show goal name
        } else if (predictionType != null && predictionType.equals("custom") && title != null) {
            tvPredictionTitle.setText(title);      // show custom name
        } else {
            tvPredictionTitle.setText("Prediction");
        }


        // Show prediction
        if(predictionOutcome != null){
            tvPredictionOutcome.setText(predictionOutcome);
        }

        btnPredictions.setOnClickListener(v -> {
            Intent toMyPrediction = new Intent(PredictOutcomeActivity.this, PredictionsListActivity.class);
            startActivity(toMyPrediction);
        });

        //navigate back
        btnBackToHome.setOnClickListener(v -> finish());

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent toprofile = new Intent(PredictOutcomeActivity.this, ProfileActivity.class);
            startActivity(toprofile);
        });

        //navigate to Dashboard page
        home.setOnClickListener(v -> {
            Intent toDashboard = new Intent(PredictOutcomeActivity.this, DashboardActivity.class);
            startActivity(toDashboard);
        });
    }
}
