package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.s23010675.achievo.services.AIService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateStepActivity extends AppCompatActivity {

    TextView goalName, statusText1, statusText2, statusText3, goalDone, goalViewSteps;
    ImageView doneIcon, viewIcon;
    ProgressBar loadingBar;

    boolean analizeSuccess = true;
    boolean undSuccess = true;
    boolean gSuccess = true;
    String goalId;
    String goal;
    private String generatedStepsJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_steps);

        goalName = findViewById(R.id.goalName);
        statusText1 = findViewById(R.id.statusText);
        statusText2 = findViewById(R.id.statusText2);
        statusText3 = findViewById(R.id.statusText3);
        goalDone = findViewById(R.id.des_back);
        goalViewSteps = findViewById(R.id.viewSteps);
        doneIcon = findViewById(R.id.done_undone);
        viewIcon = findViewById(R.id.view);
        loadingBar = findViewById(R.id.loadingSteps);

        goalDone.setVisibility(View.GONE);
        goalViewSteps.setVisibility(View.GONE);
        doneIcon.setVisibility(View.GONE);
        viewIcon.setVisibility(View.GONE);

        goal = getIntent().getStringExtra("user_goal");
        goalId = getIntent().getStringExtra("goal_id");
        goalName.setText(goal);
        Log.d("GenerateStepActivity", "goal: " + goal + ", goalId: " + goalId);


        // Animation statuses
        new Handler().postDelayed(() -> statusText1.setText("Analyzing your goal... ✅"), 1500);
        new Handler().postDelayed(() -> statusText2.setText("Understanding required actions... ✅"), 3000);
        new Handler().postDelayed(() -> statusText3.setText("Generating step-by-step instructions... ✅"), 4500);

        new Handler().postDelayed(() -> {
            loadingBar.setVisibility(View.GONE);
            boolean generationSuccess = (analizeSuccess && undSuccess && gSuccess);

            if (generationSuccess) {
                goalDone.setText("Done! Back");
                goalDone.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                goalDone.setVisibility(View.VISIBLE);
                doneIcon.setImageResource(R.drawable.done_);
                doneIcon.setVisibility(View.VISIBLE);
                goalViewSteps.setVisibility(View.VISIBLE);
                viewIcon.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Goal processed successfully!", Toast.LENGTH_SHORT).show();

                //Call API to generate steps
                AIService aiService = new AIService(this);
                aiService.generateSteps(goal, new AIService.GoalCallback() {

                    @Override
                    public void onSuccess(String resultJson) {
                        // Store the AI output for later use
                        generatedStepsJson = resultJson;

                        // Save locally for fallback
                        saveGeneratedSteps(goalId, generatedStepsJson);

                        // Initialize Firestore and get current user ID
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Parse AI response into individual steps
                        String[] stepsArray = resultJson.split("\\n");  // split by new lines
                        List<String> stepTexts = new ArrayList<>();
                        List<Map<String, Object>> stepsForFirestore = new ArrayList<>();

                        for (String step : stepsArray) {
                            step = step.trim();
                            if (step.isEmpty()) continue;

                            stepTexts.add(step); // For storing in goal document array

                            // Prepare step object for subcollection
                            Map<String, Object> stepData = new HashMap<>();
                            stepData.put("stepText", step);
                            stepData.put("completed", false); // default status
                            stepsForFirestore.add(stepData);
                        }

                        // Save each step into subcollection under the goal
                        for (int i = 0; i < stepsForFirestore.size(); i++) {
                            Map<String, Object> stepData = stepsForFirestore.get(i);

                            // Add step number based on position
                            stepData.put("stepNumber", i);
                            final int finalI = i;

                            db.collection("users")
                                    .document(uid)
                                    .collection("goals")
                                    .document(goalId)
                                    .collection("steps")
                                    .add(stepData)
                                    .addOnSuccessListener(docRef ->
                                            Log.d("Firestore", "Step saved: " + stepData.get("stepText") + " | stepNumber: " + finalI))
                                    .addOnFailureListener(e ->
                                            Log.e("Firestore", "Failed to save step", e));
                        }


                        // Update the goal document with the steps array
                        Map<String, Object> goalUpdate = new HashMap<>();
                        goalUpdate.put("steps", stepTexts);
                        db.collection("users")
                                .document(uid)
                                .collection("goals")
                                .document(goalId)
                                .set(goalUpdate, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Goal steps array updated"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update goal steps array", e));

                        }

                    @Override
                    public void onError(String error) {
                        Log.e("DeepSeekError", error);
                        Toast.makeText(GenerateStepActivity.this, "AI failed", Toast.LENGTH_LONG).show();
                    }
                });





            } else {
                goalDone.setText("       Generation Failed ❌ Back");
                goalDone.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                goalDone.setVisibility(View.VISIBLE);
                doneIcon.setImageResource(R.drawable.dislike);
                doneIcon.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Prediction generation failed. Try again.", Toast.LENGTH_SHORT).show();
            }

        }, 5500);

        TextView des_back = findViewById(R.id.des_back);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);
        TextView view_goal = findViewById(R.id.viewSteps);


        des_back.setOnClickListener(v -> finish());
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        home.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));

        view_goal.setOnClickListener(v -> {
            String stepsToView = generatedStepsJson;

            // fallback to saved steps if current session variable is null
            if (stepsToView == null) {
                stepsToView = getSavedSteps(goalId);
            }

            if (stepsToView != null) {
                Intent intent = new Intent(GenerateStepActivity.this, ViewStepsActivity.class);
                intent.putExtra("goal_steps", stepsToView);
                intent.putExtra("user_goal", goal);
                intent.putExtra("goalId", goalId); // <<< add this line
                startActivity(intent);

            } else {
                Toast.makeText(this, "Steps are not ready yet!", Toast.LENGTH_SHORT).show();
            }
        });



    }
    // Save generated steps persistently
    private void saveGeneratedSteps(String goalId, String steps) {
        getSharedPreferences("GoalSteps", MODE_PRIVATE)
                .edit()
                .putString(goalId, steps)
                .apply();
    }


    // Retrieve saved steps
    private String getSavedSteps(String goalId) {
        return getSharedPreferences("GoalSteps", MODE_PRIVATE)
                .getString(goalId, null);
    }


}
