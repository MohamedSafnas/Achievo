package com.s23010675.achievo;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText emailInput, newPasswordInput, confirmPasswordInput;
    UsersDbHelper dbHelper;
    Button resetbtn;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        back = findViewById(R.id.backI);
        resetbtn = findViewById(R.id.resetbtn);
        emailInput = findViewById(R.id.emailInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        dbHelper = new UsersDbHelper(this);


        emailInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String email = emailInput.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (dbHelper.checkEmailExists(email)) {
                    // Show new password and confirm password fields + button
                    newPasswordInput.setVisibility(View.VISIBLE);
                    confirmPasswordInput.setVisibility(View.VISIBLE);
                    resetbtn.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Email not found!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });


        resetbtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String newPass = newPasswordInput.getText().toString();
            String confirmPass = confirmPasswordInput.getText().toString();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.updatePassword(email, newPass);
            if (success) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating password", Toast.LENGTH_SHORT).show();
            }


            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        back.setOnClickListener((v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        }));

    }
}
