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

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText username, email, password, cpassword;
    private Button signBtn;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);

        signBtn = findViewById(R.id.signBtn);
        login = findViewById(R.id.login);

        signBtn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String mail = email.getText().toString().trim().toLowerCase();
            String pass = password.getText().toString().trim();
            String cpass = cpassword.getText().toString().trim();

            if (user.isEmpty() || mail.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
                Toast.makeText(this, "Fields can't be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(cpass)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Firebase user
            mAuth.createUserWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Send verification
                                firebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(verifyTask -> {
                                            if (verifyTask.isSuccessful()) {
                                                // Save user info to Firestore
                                                String uid = firebaseUser.getUid();
                                                UserModel userModel = new UserModel(user, mail);
                                                db.collection("users").document(uid)
                                                        .set(userModel)
                                                        .addOnSuccessListener(unused -> {
                                                            Toast.makeText(this, "Signup successful. Please verify your email.", Toast.LENGTH_LONG).show();

                                                            // Save to SharedPreferences
                                                            SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                                                            sp.edit().putString("uid", uid).apply();

                                                            // Clear inputs
                                                            username.setText("");
                                                            email.setText("");
                                                            password.setText("");
                                                            cpassword.setText("");

                                                            // Navigate to login
                                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                        });
                                            } else {
                                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Navigate to login
        login.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}
