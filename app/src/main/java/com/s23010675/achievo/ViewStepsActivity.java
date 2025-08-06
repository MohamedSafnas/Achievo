package com.s23010675.achievo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewStepsActivity extends AppCompatActivity {
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_steps);

        output = findViewById(R.id.stepsOutput);

        String stepsText = getIntent().getStringExtra("goal_steps");

        if (stepsText != null && !stepsText.isEmpty()) {
            // Optional: clean up markdown formatting
            stepsText = stepsText
                    .replace("\\n", "\n")         // Convert escaped \n
                    .replace("**", "")            // Remove bold markers
                    .replace("##", "");           // Remove markdown headers

            output.setText(stepsText);
        } else {
            output.setText("Failed to load steps.");
        }
    }
}
