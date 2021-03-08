package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextView emailWarning, passwordWarning, invalidLoginWarning, signupBtn;
    private EditText editEmail, editPassword;
    private Button loginBtn;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        initViews();
    }

    private void initViews() {
        invalidLoginWarning = findViewById(R.id.invalidLoginWarning);
        emailWarning = findViewById(R.id.emailWarning);
        passwordWarning = findViewById(R.id.passwordWarning);
        signupBtn = findViewById(R.id.signupBtn);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.loginBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (validateData()) {
                    emailWarning.setVisibility(View.INVISIBLE);
                    passwordWarning.setVisibility(View.INVISIBLE);

                    auth.signInWithEmailAndPassword("johndoe@gmail.com", "12345678").addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("date", "Mar-05-2021");
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            invalidLoginWarning.setVisibility(View.VISIBLE);
                        }
                    });
//                }
            }
        });
    }

    private boolean validateData() {
        invalidLoginWarning.setVisibility(View.INVISIBLE);
        boolean valid = true;
        if (editEmail.getText().toString().equals("")) {
            emailWarning.setText("Please enter a valid email.");
            emailWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (editPassword.getText().toString().equals("")) {
            passwordWarning.setText("Please enter a valid password.");
            passwordWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if (!Pattern.compile(emailRegex).matcher(editEmail.getText().toString()).matches()) {
            emailWarning.setText("Please enter a valid email.");
            emailWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

//        String passwordRegex = "^[a-zA-Z0-9]{8,}$";
//        if (!Pattern.compile(passwordRegex).matcher(editPassword.getText().toString()).matches()) {
//            passwordWarning.setText("Password cannot contain special characters and must have at least 8 characters");
//            passwordWarning.setVisibility(View.VISIBLE);
//            valid = false;
//        }

        if (!valid) {
            return false;
        }
        else {
            return true;
        }
    }
}
