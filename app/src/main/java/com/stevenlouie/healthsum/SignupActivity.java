package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout1, relativeLayout2;
    private TextView nameWarning, emailWarning, passwordWarning, loginBtn, loginBtn2, ageWarning, weightWarning;
    private EditText editName, editEmail, editPassword, editAge, editWeight;
    private Button signupBtn, slideDownButton, slideUpButton;
    private Spinner height_feet_spinner, height_inches_spinner, gender_spinner;
    private int heightFt = 1;
    private int heightIn = 0;
    private String gender = "male";

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        initViews();
    }

    private void initViews() {

        relativeLayout1 = findViewById(R.id.relativeLayout1);
        relativeLayout2 = findViewById(R.id.relativeLayout2);
        nameWarning = findViewById(R.id.nameWarning);
        emailWarning = findViewById(R.id.emailWarning);
        passwordWarning = findViewById(R.id.passwordWarning);
        ageWarning = findViewById(R.id.ageWarning);
        weightWarning = findViewById(R.id.weightWarning);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn2 = findViewById(R.id.loginBtn2);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        signupBtn = findViewById(R.id.signupBtn);
        slideDownButton = findViewById(R.id.slideDownButton);
        slideUpButton = findViewById(R.id.slideUpButton);
        height_feet_spinner = findViewById(R.id.height_feet_spinner);
        height_inches_spinner = findViewById(R.id.height_inches_spinner);
        gender_spinner = findViewById(R.id.gender_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(adapter);
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Integer[] heightFeet = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8};
        ArrayAdapter<Integer> adapter1 = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, heightFeet);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        height_feet_spinner.setAdapter(adapter1);
        height_feet_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightFt = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Integer[] heightInches = new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, heightInches);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        height_inches_spinner.setAdapter(adapter2);
        height_inches_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightIn = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        slideUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData1()) {
                    nameWarning.setVisibility(View.INVISIBLE);
                    emailWarning.setVisibility(View.INVISIBLE);
                    passwordWarning.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editEmail.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);

                    Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_up);
                    relativeLayout1.setVisibility(View.GONE);
                    relativeLayout2.startAnimation(bottomUp);
                    relativeLayout2.setVisibility(View.VISIBLE);
                }
            }
        });

        slideDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editAge.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
                Animation topDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
                relativeLayout2.startAnimation(topDown);
                relativeLayout2.setVisibility(View.GONE);
                relativeLayout1.setVisibility(View.VISIBLE);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData2()) {
                    ageWarning.setVisibility(View.INVISIBLE);
                    weightWarning.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editAge.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editWeight.getWindowToken(), 0);
                    auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString()).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "Successfully signed up.", Toast.LENGTH_SHORT).show();
                                HashMap<String, Object> map1 = new HashMap<>();
                                map1.put("gender", gender);
                                map1.put("age", Integer.valueOf(editAge.getText().toString()));
//                                map1.put("weight", Integer.valueOf(editWeight.getText().toString()));
                                map1.put("height", heightFt + "," + heightIn);
                                DatabaseReference database =  FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                                database.updateChildren(map1);
                                database.child("weight").child("2021-03-05").child(database.push().getKey()).setValue(Integer.valueOf(editWeight.getText().toString()));
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("caloriesLeft", 0);
//                                map.put("setSteps", 0);
                                map.put("caloriesBurned", 0);
                                map.put("calorieGoal", 0);
                                FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child("2021-03-05").updateChildren(map);
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.putExtra("date", "2021-03-05");
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loginBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateData1() {
        boolean valid = true;
        if (editName.getText().toString().equals("")) {
            nameWarning.setText("Please enter a valid name.");
            nameWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            nameWarning.setVisibility(View.INVISIBLE);
        }

        if (editEmail.getText().toString().equals("")) {
            emailWarning.setText("Please enter a valid email.");
            emailWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            emailWarning.setVisibility(View.INVISIBLE);
        }

        if (editPassword.getText().toString().equals("")) {
            passwordWarning.setText("Please enter a valid password.");
            passwordWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            passwordWarning.setVisibility(View.INVISIBLE);
        }

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if (!Pattern.compile(emailRegex).matcher(editEmail.getText().toString()).matches()) {
            emailWarning.setText("Please enter a valid email.");
            emailWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

        String passwordRegex = "^[a-zA-Z0-9]{8,}$";
        if (!Pattern.compile(passwordRegex).matcher(editPassword.getText().toString()).matches()) {
            passwordWarning.setText("Password cannot contain special characters and must have at least 8 characters");
            passwordWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (!valid) {
            return false;
        }
        else {
            return true;
        }
    }

    private boolean validateData2() {
        boolean valid = true;
        if (editAge.getText().toString().equals("")) {
            ageWarning.setText("Please enter a valid age.");
            ageWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            ageWarning.setVisibility(View.INVISIBLE);
        }

        if (editWeight.getText().toString().equals("")) {
            weightWarning.setText("Please enter a valid weight.");
            weightWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            weightWarning.setVisibility(View.INVISIBLE);
        }

        String numberRegex = "^[0-9]+$";
        if (!Pattern.compile(numberRegex).matcher(editAge.getText().toString()).matches()) {
            ageWarning.setText("Please enter a valid age.");
            ageWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (!Pattern.compile(numberRegex).matcher(editWeight.getText().toString()).matches()) {
            weightWarning.setText("Please enter a valid weight.");
            weightWarning.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (!valid) {
            return false;
        }
        else {
            return true;
        }
    }


}
