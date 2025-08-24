package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ViewStepsActivity extends AppCompatActivity {
    RecyclerView stepsRecycler;
    ProgressBar progressBar;
    TextView progressText;
    ImageView back,btnRestart, btnComplete;
    Button MyGoals;
    List<StepModel> stepList = new ArrayList<>();
    StepsAdapter adapter;
    private String goalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_steps);

        // Get goalId from intent
        goalId = getIntent().getStringExtra("goal_id");
        String goalName = getIntent().getStringExtra("goal_name");
        if (goalId == null) {
            Toast.makeText(this, "Goal not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firestore + user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Goal title
        TextView goalTitle = findViewById(R.id.goalTitle);
        //String goalName = getIntent().getStringExtra("user_goal");
        if (goalName != null && !goalName.isEmpty()) {
            goalTitle.setText(goalName);
        }

        stepsRecycler = findViewById(R.id.stepsRecycler);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        btnRestart = findViewById(R.id.btnRestart);
        btnComplete = findViewById(R.id.btnComplete);
        back = findViewById(R.id.back);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);
        MyGoals = findViewById(R.id.btnMyGoals);

        adapter = new StepsAdapter(stepList, this::updateProgress);
        stepsRecycler.setLayoutManager(new LinearLayoutManager(this));
        stepsRecycler.setAdapter(adapter);

        // Load steps from Firestore
        db.collection("users")
                .document(uid)
                .collection("goals")
                .document(goalId)
                .collection("steps")
                .orderBy("stepNumber", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    stepList.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String stepText = doc.getString("stepText");
                        boolean completed = doc.getBoolean("completed") != null && doc.getBoolean("completed");
                        Long sn = doc.getLong("stepNumber");
                        int stepNumber = sn != null ? sn.intValue() : 0;
                        StepModel step = new StepModel(stepText, completed, doc.getId());

                        step.setStepNumber(stepNumber);
                        stepList.add(step);

                    }
                    adapter.notifyDataSetChanged();
                    updateProgress();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load steps: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        // Restart Goal (untick all)
        btnRestart.setOnClickListener(v -> {
            for (StepModel step : stepList) {
                step.setCompleted(false);
            }
            adapter.notifyDataSetChanged();
            updateProgress();
        });

        // Complete Goal (tick all)
        btnComplete.setOnClickListener(v -> {
            for (StepModel step : stepList) {
                step.setCompleted(true);  //
            }
            adapter.notifyDataSetChanged();
            updateProgress();
        });

        updateProgress();

        back.setOnClickListener(v -> finish());

        //navigate to My Goals Page
        MyGoals.setOnClickListener(v -> {
            Intent intent = new Intent(ViewStepsActivity.this, MyGoalsActivity.class);
            startActivity(intent);
        });

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(ViewStepsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //navigate to home page
        home.setOnClickListener(v -> {
            Intent intent = new Intent(ViewStepsActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    // Progress update method
    private void updateProgress() {
        int checked = adapter.getCheckedCount();
        int total = adapter.getTotalCount();
        int percent = total == 0 ? 0 : (checked * 100) / total;

        progressBar.setProgress(percent);
        progressText.setText(percent + "% completed");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (StepModel step : stepList) {
            db.collection("users")
                    .document(uid)
                    .collection("goals")
                    .document(goalId)
                    .collection("steps")
                    .document(step.getId())
                    .update("completed", step.isCompleted());
        }

        // update goal overall percent
        db.collection("users")
                .document(uid)
                .collection("goals")
                .document(goalId)
                .update("completedPercent", percent);

        // Save locally
        if (goalId != null) {
            List<String> stepsWithChecks = new ArrayList<>();
            for (StepModel step : stepList) {
                stepsWithChecks.add((step.isCompleted() ? "[x] " : "[ ] ") + step.getStepText());
            }
            getSharedPreferences("GoalSteps", MODE_PRIVATE)
                    .edit()
                    .putString(goalId, String.join("\n", stepsWithChecks))
                    .apply();
        }
    }
}
