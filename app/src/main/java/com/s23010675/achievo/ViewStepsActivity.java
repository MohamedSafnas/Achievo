package com.s23010675.achievo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewStepsActivity extends AppCompatActivity {
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_steps);

        output = findViewById(R.id.stepsOutput);

        String resultJson = getIntent().getStringExtra("goal_steps");

        try {
            JSONObject parsed = new JSONObject(resultJson);
            String type = parsed.optString("type", "N/A");
            JSONArray steps = parsed.getJSONArray("steps");

            StringBuilder sb = new StringBuilder();
            sb.append("Goal Type: ").append(type).append("\n\nSteps:\n");

            for (int i = 0; i < steps.length(); i++) {
                sb.append("• ").append(steps.getString(i)).append("\n");
            }

            output.setText(sb.toString());
        } catch (Exception e) {
            output.setText("Failed to parse steps.");
        }
    }
}
