package com.s23010675.achievo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView sign = findViewById(R.id.sign);
        TextView forget = findViewById(R.id.forgetT);

        loginBtn.setOnClickListener(v -> {
            String userEmail = emailInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            if (userEmail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(userEmail, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                String uid = firebaseUser.getUid();

                                // Save session
                                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                                sp.edit().putString("uid", uid).apply();

                                // Fetch username from Firestore
                                db.collection("users").document(uid).get()
                                        .addOnSuccessListener(document -> {
                                            if (document.exists()) {
                                                String username = document.getString("username");
                                                Toast.makeText(this, "Login successful! Welcome " + username, Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Create Firestore document for old users
                                                UserModel userModel = new UserModel(
                                                        firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "",
                                                        firebaseUser.getEmail()
                                                );

                                                db.collection("users").document(uid)
                                                        .set(userModel)
                                                        .addOnSuccessListener(unused -> {
                                                            Toast.makeText(this, "User data created. Login successful!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(this, "Failed to create user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                mAuth.signOut(); // logout unverified
                            }

                        } else {
                            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Navigate to reset password page
        forget.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // Navigate to signup page
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}
