package com.s23010675.achievo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {


    EditText username,email,password,cpassword;
    UsersDbHelper dbHelper;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        dbHelper = new UsersDbHelper(this);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);

        Button signBtn = findViewById(R.id.signBtn);
        TextView login = findViewById(R.id.login);

        signBtn.setOnClickListener(v -> {

            String user = username.getText().toString();
            String mail = email.getText().toString();
            String pass = password.getText().toString();
            String cpass = cpassword.getText().toString();

            if(user.isEmpty() || mail.isEmpty() || pass.isEmpty() || cpass.isEmpty()){
                Toast.makeText(this,"It's can't be empty!",Toast.LENGTH_SHORT).show();
                return;
            }

            if(!pass.equals(cpass)){
                Toast.makeText(this,"please enter same password!",Toast.LENGTH_SHORT).show();
                return;
            }


            boolean response = dbHelper.signup(user,mail,pass);
                if (response){

                    SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                    sp.edit().putString("email", mail).apply();

                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();

                    //clear input fields
                    username.setText("");
                    email.setText("");
                    password.setText("");
                    cpassword.setText("");

                    //navigate to Login page after signup
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this,"try again with different email!",Toast.LENGTH_SHORT).show();
                }




        });

        login.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
