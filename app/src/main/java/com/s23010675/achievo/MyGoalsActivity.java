package com.s23010675.achievo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MyGoalsActivity extends AppCompatActivity {

    LinearLayout setGoalForm;
    TextView setNewGoalBox;
    Button submitGoalBtn;
    EditText goalInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goals);


        setNewGoalBox = findViewById(R.id.setNewGoalBox);
        setGoalForm = findViewById(R.id.setGoalForm);
        submitGoalBtn = findViewById(R.id.submitGoalBtn);
        goalInput = findViewById(R.id.goalInput);

        //trigger when click the set new goal
        setNewGoalBox.setOnClickListener(v -> {
            if (setGoalForm.getVisibility() == View.GONE) {
                setGoalForm.setVisibility(View.VISIBLE);
            } else {
                setGoalForm.setVisibility(View.GONE);
            }
        });

        //trigger when the generate steps button clicks
        submitGoalBtn.setOnClickListener(v -> {
            String goal = goalInput.getText().toString().trim();
            if (!goal.isEmpty()) {
                Intent intent = new Intent(MyGoalsActivity.this, GenerateStepActivity.class);
                intent.putExtra("user_goal", goal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a goal", Toast.LENGTH_SHORT).show();
            }
        });


        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);

        //navigate to Profile page
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(MyGoalsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        //navigate to Dashboard page
        home.setOnClickListener(v -> {
            Intent intent = new Intent(MyGoalsActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }
}