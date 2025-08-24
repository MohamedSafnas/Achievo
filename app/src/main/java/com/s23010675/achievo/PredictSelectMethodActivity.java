package com.s23010675.achievo;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.s23010675.achievo.services.AIService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class PredictSelectMethodActivity extends AppCompatActivity {

    private RadioGroup radioGroupMethod;
    private LinearLayout layoutBasedGoal, layoutCustom;
    private Spinner spinnerGoals, spinnerFrequency;
    private TextView textProgress;
    private ProgressBar progressBarGoal;
    private Button btnPredictGoal, btnPredictCustom;
    private EditText editCustomGoal, editDuration;
    private AIService aiService;


    private ArrayList<String> userGoals = new ArrayList<>();
    private Date selectedCreatedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_prediction_method);

        aiService = new AIService(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // Initialize Views
        radioGroupMethod = findViewById(R.id.radioGroupMethod);
        layoutBasedGoal = findViewById(R.id.layoutBasedGoal);
        layoutCustom = findViewById(R.id.layoutCustom);
        spinnerGoals = findViewById(R.id.spinnerGoals);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        textProgress = findViewById(R.id.textProgress);
        progressBarGoal = findViewById(R.id.progressBarGoal);
        btnPredictGoal = findViewById(R.id.btnPredictGoal);
        btnPredictCustom = findViewById(R.id.btnPredictCustom);
        editCustomGoal = findViewById(R.id.editCustomGoal);
        editDuration = findViewById(R.id.editDuration);
        ImageView back = findViewById(R.id.backI);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        // RadioGroup listener
        radioGroupMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioBasedGoal) {
                layoutBasedGoal.setVisibility(View.VISIBLE);
                layoutCustom.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioCustom) {
                layoutBasedGoal.setVisibility(View.GONE);
                layoutCustom.setVisibility(View.VISIBLE);
            }
        });

        // Populate goals from Firestore
        ArrayAdapter<String> adapterGoals = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, userGoals);
        adapterGoals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoals.setAdapter(adapterGoals);

        db.collection("users")
                .document(userId)
                .collection("goals")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userGoals.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String goalName = doc.getString("name");
                        if (goalName != null) {
                            userGoals.add(goalName);
                        }
                    }
                    adapterGoals.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load goals: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


        // Populate frequency spinner
        String[] frequencies = {"Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> adapterFreq = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, frequencies);
        adapterFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(adapterFreq);

        spinnerGoals.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = userGoals.get(position);

                // Fetch the selected goal's details
                db.collection("users")
                        .document(userId)
                        .collection("goals")
                        .whereEqualTo("name", selectedGoal)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                Long completedPercent = doc.getLong("completedPercent");
                                Date createdDate = doc.getDate("date");

                                if (completedPercent != null) {
                                    progressBarGoal.setProgress(completedPercent.intValue());
                                    textProgress.setText("Progress: " + completedPercent + "%");
                                }

                                selectedCreatedDate = createdDate;
                            }
                        });
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });


        // Based on Goal Predict button
        btnPredictGoal.setOnClickListener(v -> {
            String goalName = spinnerGoals.getSelectedItem().toString();
            int progress = progressBarGoal.getProgress();


            try {
                JSONObject payload = new JSONObject();
                payload.put("goalName", goalName);
                payload.put("completionPercentage", progress);
                //payload.put("createdDate", createdDate.getTime());

                if (selectedCreatedDate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    payload.put("createdDate", sdf.format(selectedCreatedDate));
                }

                aiService.getPrediction(payload, new AIService.GoalCallback() {
                         public void onSuccess(String resultJson) {
                        // Save to Firestore
                        Map<String, Object> predictionData = new HashMap<>();
                        predictionData.put("goalName", goalName);
                        predictionData.put("predictionText", resultJson);
                        predictionData.put("createdAt", new Date());

                        db.collection("users")
                                .document(userId)
                                .collection("predictions")
                                .add(predictionData)
                                .addOnSuccessListener(docRef -> {
                                    // Open outcome activity
                                    Intent intent = new Intent(PredictSelectMethodActivity.this, PredictOutcomeActivity.class);
                                    intent.putExtra("predictionType", "goal");
                                    intent.putExtra("goalName", goalName);
                                    intent.putExtra("predictionOutcome", resultJson);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PredictSelectMethodActivity.this, "Failed to save prediction", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PredictSelectMethodActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Payload error", Toast.LENGTH_SHORT).show();
            }
        });


        // Custom Predict button
        btnPredictCustom.setOnClickListener(v -> {
            String title = editCustomGoal.getText().toString().trim();
            String freq = spinnerFrequency.getSelectedItem().toString();
            String duration = editDuration.getText().toString().trim();

            if (title.isEmpty() || duration.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject payload = new JSONObject();
                payload.put("title", title);
                payload.put("frequency", freq);
                payload.put("duration", Integer.parseInt(duration));

                aiService.getPrediction(payload, new AIService.GoalCallback() {

                    public void onSuccess(String resultJson) {
                        // Save to Firestore
                        Map<String, Object> predictionData = new HashMap<>();
                        predictionData.put("goalName", title);
                        predictionData.put("predictionText", resultJson);
                        predictionData.put("createdAt", new Date());

                        db.collection("users")
                                .document(userId)
                                .collection("predictions")
                                .add(predictionData)
                                .addOnSuccessListener(docRef -> {
                                    // Open outcome activity
                                    Intent intent = new Intent(PredictSelectMethodActivity.this, PredictOutcomeActivity.class);
                                    intent.putExtra("predictionType", "custom");
                                    intent.putExtra("title", title);
                                    intent.putExtra("predictionOutcome", resultJson);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PredictSelectMethodActivity.this, "Failed to save prediction", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PredictSelectMethodActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Payload error", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> finish());

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(PredictSelectMethodActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //navigate to home page
        home.setOnClickListener(v -> {
            Intent intent = new Intent(PredictSelectMethodActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

}
