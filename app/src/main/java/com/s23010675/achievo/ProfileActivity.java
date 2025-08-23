package com.s23010675.achievo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    CircleImageView profilePic;
    ImageView editIcon;
    TextView userName, goalCount, achievedCount;

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.profilepic);
        editIcon = findViewById(R.id.editProfile);
        userName = findViewById(R.id.username);
        goalCount = findViewById(R.id.goalCount);
        achievedCount = findViewById(R.id.achievedCount);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        uid = user.getUid();
        loadGoalCounts();

        // Load user profile from Firestore
        loadUserProfile();

        // Handle image picking
        View.OnClickListener pickImage = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        };

        profilePic.setOnClickListener(pickImage);
        editIcon.setOnClickListener(pickImage);

        // Bottom navigation
        TextView logout = findViewById(R.id.logout);
        ImageView home = findViewById(R.id.homeI);

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        home.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
        });
    }

    private void loadUserProfile() {
        firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String uname = doc.getString("username");
                        String picUri = doc.getString("profilePicUri");

                        userName.setText(uname);
                        if (picUri != null && !picUri.isEmpty()) {
                            File file = new File(picUri);
                            if (file.exists()) {
                                profilePic.setImageURI(Uri.parse(picUri));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == PICK_IMAGE_REQUEST && resCode == RESULT_OK && data != null) {
            Uri imgUri = data.getData();
            String path = saveImage(imgUri);
            if (path != null) {
                profilePic.setImageURI(Uri.parse(path));
                firestore.collection("users").document(uid)
                        .update("profilePicUri", path)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                        );
            }
        }
    }

    private String saveImage(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void loadGoalCounts() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        firestore.collection("users")
                .document(uid)
                .collection("goals")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalGoals = querySnapshot.size();
                    int achievedGoals = 0;

                    for (DocumentSnapshot doc : querySnapshot) {
                        Long completedPercent = doc.getLong("completedPercent");
                        if (completedPercent != null && completedPercent == 100) {
                            achievedGoals++;
                        }
                    }

                    goalCount.setText(String.valueOf(totalGoals));
                    achievedCount.setText(String.valueOf(achievedGoals));

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Failed to load goals", Toast.LENGTH_SHORT).show();
                });
    }
}
