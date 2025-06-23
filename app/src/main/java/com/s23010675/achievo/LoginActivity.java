package com.s23010675.achievo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    UsersDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        dbHelper = new UsersDbHelper(this);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);

        TextView sign = findViewById(R.id.sign);
        TextView forget = findViewById(R.id.forgetT);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            String userEmail = emailInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            if (userEmail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            //login using database
            String username = dbHelper.login(userEmail, pass);

            if (username != null) {
                //save email
                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                sp.edit().putString("email", userEmail).apply();

                Toast.makeText(this, "Login successful! Welcome " + username, Toast.LENGTH_SHORT).show();

                //navigate to Dashboard
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        //navigate to reset password page
        forget.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        //navigate to signup page
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

}
