package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DashboardActivity extends AppCompatActivity {

    LinearLayout setGoalForm;
    TextView setNewGoalBox,userName;
    Button submitGoalBtn;
    EditText goalInput;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Views
        userName = findViewById(R.id.userN);
        setNewGoalBox = findViewById(R.id.setNewGoalBox);
        setGoalForm = findViewById(R.id.setGoalForm);
        submitGoalBtn = findViewById(R.id.submitGoalBtn);
        goalInput = findViewById(R.id.goalInput);

        // Fetch and display username
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            firestore.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            userName.setText(username);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load username", Toast.LENGTH_SHORT).show();
                    });
        }

        // Toggle goal form
        setNewGoalBox.setOnClickListener(v -> {
            if (setGoalForm.getVisibility() == View.GONE) {
                setGoalForm.setVisibility(View.VISIBLE);
            } else {
                setGoalForm.setVisibility(View.GONE);
            }
        });

        // Submit goal
        submitGoalBtn.setOnClickListener(v -> {
            String goal = goalInput.getText().toString().trim();
            if (!goal.isEmpty()) {
                currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    String uid = currentUser.getUid();

                    // Create goal data
                    Map<String, Object> goalData = new HashMap<>();
                    goalData.put("name", goal);
                    goalData.put("date", FieldValue.serverTimestamp());
                    goalData.put("steps", new ArrayList<>());

                    // Save to Firestore
                    firestore.collection("users")
                            .document(uid)
                            .collection("goals")
                            .add(goalData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(DashboardActivity.this, "Goal saved!", Toast.LENGTH_SHORT).show();

                                // Pass firedtore goal id to the next activity
                                String goalId = documentReference.getId();

                                // Navigate to GenerateStepActivity
                                Intent intent = new Intent(DashboardActivity.this, GenerateStepActivity.class);
                                intent.putExtra("user_goal", goal);
                                intent.putExtra("goal_id", goalId);
                                startActivity(intent);

                                // clear input
                                goalInput.setText("");
                                setGoalForm.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DashboardActivity.this, "Failed to save goal.", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Please enter a goal", Toast.LENGTH_SHORT).show();
            }
        });



        TextView mygoals = findViewById(R.id.myGoals);
        LinearLayout predictNew = findViewById(R.id.predictNew);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);
        Button getLocation = findViewById(R.id.getLocationBtn);
        Button viewLocation = findViewById(R.id.LocationList);
        LinearLayout predictHistory = findViewById(R.id.predictHistory);

        //navigate to My Goals page
        mygoals.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MyGoalsActivity.class);
            startActivity(intent);
        });

        //navigate to Predict Future page
        predictNew.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PredictSelectMethodActivity.class);
            startActivity(intent);
        });

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //navigate to Find Location page
        getLocation.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FindLocationActivity.class);
            startActivity(intent);
        });

        //navigate to Saved Locations page
        viewLocation.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SavedLocationsActivity.class);
            startActivity(intent);
        });

        //navigate to My predictions page
        predictHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PredictionsListActivity.class);
            startActivity(intent);
        });

    }
}
