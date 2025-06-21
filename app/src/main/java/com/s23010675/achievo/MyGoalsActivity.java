package com.s23010675.achievo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MyGoalsActivity extends AppCompatActivity {



    LinearLayout setGoalForm2;
    TextView setNewGoalBox2;
    Button submitGoalBtn2;
    EditText goalInput2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goals);


        setNewGoalBox2 = findViewById(R.id.setNewGoalBox2);
        setGoalForm2 = findViewById(R.id.setGoalForm2);
        submitGoalBtn2 = findViewById(R.id.submitGoalBtn2);
        goalInput2 = findViewById(R.id.goalInput2);

        setNewGoalBox2.setOnClickListener(v -> {
            if (setGoalForm2.getVisibility() == View.GONE) {
                setGoalForm2.setVisibility(View.VISIBLE);
            } else {
                setGoalForm2.setVisibility(View.GONE);
            }
        });


        submitGoalBtn2.setOnClickListener(v -> {
            String goal = goalInput2.getText().toString().trim();
            if (!goal.isEmpty()) {
                Intent intent = new Intent(MyGoalsActivity.this, GenerateStepActivity.class);
                intent.putExtra("user_goal", goal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a goal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
