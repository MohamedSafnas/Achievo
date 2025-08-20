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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.s23010675.achievo.GoalAdapter;
import com.s23010675.achievo.GoalModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGoalsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private List<GoalModel> goalList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView addGoalBtn;
    LinearLayout setGoalForm;
    TextView setNewGoalBox,userName;
    Button submitGoalBtn;
    EditText goalInput;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goals);

        recyclerView = findViewById(R.id.recyclerGoals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        goalList = new ArrayList<>();
        adapter = new GoalAdapter(this, goalList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setNewGoalBox = findViewById(R.id.setNewGoalBox);
        setGoalForm = findViewById(R.id.setGoalForm);
        submitGoalBtn = findViewById(R.id.submitGoalBtn);
        goalInput = findViewById(R.id.goalInput);

        loadGoals();


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

                    // Save to Firestore
                    db.collection("users")
                            .document(uid)
                            .collection("goals")
                            .add(goalData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MyGoalsActivity.this, "Goal saved!", Toast.LENGTH_SHORT).show();

                                // Firestore generated ID
                                String goalId = documentReference.getId();

                                // Navigate to GenerateStepActivity
                                Intent intent = new Intent(MyGoalsActivity.this, GenerateStepActivity.class);
                                intent.putExtra("user_goal", goal);
                                intent.putExtra("goal_id", goalId);
                                startActivity(intent);

                                goalInput.setText("");
                                setGoalForm.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MyGoalsActivity.this, "Failed to save goal.", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Please enter a goal", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        home.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));


    }

    private void loadGoals() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .collection("goals")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(MyGoalsActivity.this, "Error loading goals", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<GoalModel> newGoals = new ArrayList<>();
                    for(DocumentSnapshot doc : value.getDocuments()){
                        GoalModel goal = doc.toObject(GoalModel.class);
                        if(goal != null) {
                            goal.setId(doc.getId());
                            newGoals.add(goal);
                        }
                    }
                    adapter.setGoals(newGoals);
                });
    }

}
