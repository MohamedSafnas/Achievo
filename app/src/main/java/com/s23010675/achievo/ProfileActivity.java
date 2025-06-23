package com.s23010675.achievo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView editIcon;
    CircleImageView profilePic;

    UsersDbHelper dbHelper;
    String userEmail;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profilePic = findViewById(R.id.profilepic);
        editIcon = findViewById(R.id.editProfile);
        userName = findViewById(R.id.username);

        dbHelper = new UsersDbHelper(this);

        //get email from sharedPreferences
        SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
        userEmail = sp.getString("email", null);

        loadUserProfile();

        View.OnClickListener pickImageListener = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        };

        profilePic.setOnClickListener(pickImageListener);
        editIcon.setOnClickListener(pickImageListener);

        //bottom navigation
        TextView logout = findViewById(R.id.logout);
        ImageView home = findViewById(R.id.homeI);
        ImageView profile = findViewById(R.id.profileI);


        logout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

    }

    void loadUserProfile() {
        UsersDbHelper.User user = dbHelper.getUserProfile(userEmail);
        if (user != null) {
            userName.setText(user.username);
            if (user.profilePicUri != null && !user.profilePicUri.isEmpty()) {
                File file = new File(user.profilePicUri);
                if (file.exists()) {
                    profilePic.setImageURI(Uri.parse(user.profilePicUri));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == PICK_IMAGE_REQUEST && resCode == RESULT_OK && data != null) {
            Uri imgUri = data.getData();
            String path = saveImage(imgUri);
            profilePic.setImageURI(Uri.parse(path));
            dbHelper.updateProfilePic(userEmail, path);
        }
    }

    String saveImage(Uri uri) {
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
}


