package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.s23010675.achievo.R;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Color;
import android.widget.TextView;


public class GetStartedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        Button btn = findViewById(R.id.getStartedBtn);

        btn.setOnClickListener(v -> {
                Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                startActivity(intent);
            });



        TextView textView = findViewById(R.id.slogen);

        Shader shader = new LinearGradient(
                0, 0, 0, textView.getTextSize(),
                new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#999999")},
                null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);

    }
}



