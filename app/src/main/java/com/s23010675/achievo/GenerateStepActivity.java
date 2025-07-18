package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GenerateStepActivity extends AppCompatActivity {

    TextView goalName, statusText1, statusText2, statusText3, goalDone, goalViewSteps;
    ImageView doneIcon, viewIcon;
    ProgressBar loadingBar;


    boolean analizeSuccess = true;
    boolean undSuccess = true;
    boolean gSuccess = true;

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


        String goal = getIntent().getStringExtra("user_goal");
        goalName.setText(goal);


        if (analizeSuccess) {
            new Handler().postDelayed(() -> statusText1.setText("Analyzing your goal... ✅"), 1500);
        }else{
            new Handler().postDelayed(() -> statusText1.setText("Analyzing your goal... ❌"), 1500);
        }

        if (undSuccess) {
            new Handler().postDelayed(() -> statusText2.setText("Understanding required actions... ✅"), 3000);
        }else{
            new Handler().postDelayed(() -> statusText2.setText("Understanding required actions... ❌"), 3000);
        }

        if (gSuccess) {
            new Handler().postDelayed(() -> statusText3.setText("Generating step-by-step instructions... ✅"), 4500);
        }else{
            new Handler().postDelayed(() -> statusText3.setText("Generating step-by-step instructions... ❌"), 4500);
        }



        new Handler().postDelayed(() -> {
            loadingBar.setVisibility(View.GONE);

            boolean generationSuccess = (analizeSuccess && undSuccess && gSuccess);


            if (generationSuccess){

                goalDone.setText("Done! Back");
                goalDone.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                goalDone.setVisibility(View.VISIBLE);
                doneIcon.setImageResource(R.drawable.done_);
                doneIcon.setVisibility(View.VISIBLE);
                goalViewSteps.setVisibility(View.VISIBLE);
                viewIcon.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Goal processed successfully!", Toast.LENGTH_SHORT).show();

                //code for navigate to view steps page

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


        des_back.setOnClickListener(v -> {
            Intent intent = new Intent(GenerateStepActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(GenerateStepActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(GenerateStepActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }
}
