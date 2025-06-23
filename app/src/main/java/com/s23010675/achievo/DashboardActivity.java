package com.s23010675.achievo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    LinearLayout setGoalForm;
    TextView setNewGoalBox,userName;
    Button submitGoalBtn;
    EditText goalInput;
    UsersDbHelper dbHelper;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userName = findViewById(R.id.userN);
        dbHelper = new UsersDbHelper(this);


        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        String email = sp.getString("email", null);


        if (email != null) {
            UsersDbHelper.User user = dbHelper.getUserProfile(email);
            if (user != null) {
                userName.setText(user.username);
            }
        }


        setNewGoalBox = findViewById(R.id.setNewGoalBox);
        setGoalForm = findViewById(R.id.setGoalForm);
        submitGoalBtn = findViewById(R.id.submitGoalBtn);
        goalInput = findViewById(R.id.goalInput);

        setNewGoalBox.setOnClickListener(v -> {
            if (setGoalForm.getVisibility() == View.GONE) {
                setGoalForm.setVisibility(View.VISIBLE);
            } else {
                setGoalForm.setVisibility(View.GONE);
            }
        });



        submitGoalBtn.setOnClickListener(v -> {
            String goal = goalInput.getText().toString().trim();
            if (!goal.isEmpty()) {
                Intent intent = new Intent(DashboardActivity.this, GenerateStepActivity.class);
                intent.putExtra("user_goal", goal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a goal", Toast.LENGTH_SHORT).show();
            }
        });

        TextView mygoals = findViewById(R.id.myGoals);
        TextView predictNew = findViewById(R.id.predictNew);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);
        Button getLocation = findViewById(R.id.getLocationBtn);


        mygoals.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MyGoalsActivity.class);
            startActivity(intent);
        });

        predictNew.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PredictMethodActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        getLocation.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FindLocationActivity.class);
            startActivity(intent);
        });


    }
}
