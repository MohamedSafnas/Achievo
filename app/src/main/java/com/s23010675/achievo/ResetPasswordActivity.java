package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText emailInput, newPasswordInput, confirmPasswordInput;
    UsersDbHelper dbHelper;
    Button resetbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        resetbtn = findViewById(R.id.resetbtn);
        emailInput = findViewById(R.id.emailInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        dbHelper = new UsersDbHelper(this);


        resetbtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String newPass = newPasswordInput.getText().toString();
            String confirmPass = confirmPasswordInput.getText().toString();


            if (email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }


            boolean updated = dbHelper.updatePassword(email, newPass);
            if (updated) {
                Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            }


            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
