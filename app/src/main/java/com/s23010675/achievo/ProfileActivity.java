package com.s23010675.achievo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView profilePic, editIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        profilePic = findViewById(R.id.profilepic);
        editIcon = findViewById(R.id.editProfile);

        editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profilePic.setImageURI(imageUri);
        }
    }

}
