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

import java.util.ArrayList;
import java.util.List;

public class ViewStepsActivity extends AppCompatActivity {

    RecyclerView stepsRecycler;
    ProgressBar progressBar;
    TextView progressText;
    Button btnRestart, btnComplete;

    List<StepModel> stepList = new ArrayList<>();
    StepsAdapter adapter;
    private String goalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_steps);

        // 1️⃣ Get goalId from intent
        goalId = getIntent().getStringExtra("goalId");
        if (goalId == null) {
            Toast.makeText(this, "Goal not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2️⃣ Firestore + user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 3️⃣ Goal title
        TextView goalTitle = findViewById(R.id.goalTitle);
        String goalName = getIntent().getStringExtra("user_goal");
        if (goalName != null && !goalName.isEmpty()) {
            goalTitle.setText(goalName);
        }

        stepsRecycler = findViewById(R.id.stepsRecycler);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        btnRestart = findViewById(R.id.btnRestart);
        btnComplete = findViewById(R.id.btnComplete);

        adapter = new StepsAdapter(stepList, this::updateProgress);
        stepsRecycler.setLayoutManager(new LinearLayoutManager(this));
        stepsRecycler.setAdapter(adapter);

        // Load steps from Firestore
        db.collection("users")
                .document(uid)
                .collection("goals")
                .document(goalId)
                .collection("steps")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    stepList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String stepText = doc.getString("stepText");
                        boolean completed = doc.getBoolean("completed") != null && doc.getBoolean("completed");
                        StepModel step = new StepModel(stepText, completed, doc.getId());
                        stepList.add(step);
                    }
                    adapter.notifyDataSetChanged();
                    updateProgress();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load steps: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        // 🔄 Restart Goal (untick all)
        btnRestart.setOnClickListener(v -> {
            for (StepModel step : stepList) {
                step.setCompleted(false);  // ✅ fixed
            }
            adapter.notifyDataSetChanged();
            updateProgress();
        });

        // ✅ Complete Goal (tick all)
        btnComplete.setOnClickListener(v -> {
            for (StepModel step : stepList) {
                step.setCompleted(true);  // ✅ fixed
            }
            adapter.notifyDataSetChanged();
            updateProgress();
        });

        updateProgress();
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
                    .update("completed", step.isCompleted());  // ✅ fixed
        }

        // Optional: update goal's overall percent
        db.collection("users")
                .document(uid)
                .collection("goals")
                .document(goalId)
                .update("completedPercent", percent);

        // ✅ Save locally
        if (goalId != null) {
            List<String> stepsWithChecks = new ArrayList<>();
            for (StepModel step : stepList) {
                stepsWithChecks.add((step.isCompleted() ? "[x] " : "[ ] ") + step.getStepText());  // ✅ fixed
            }
            getSharedPreferences("GoalSteps", MODE_PRIVATE)
                    .edit()
                    .putString(goalId, String.join("\n", stepsWithChecks))
                    .apply();
        }
    }
}
