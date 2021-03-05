package com.stevenlouie.healthsum;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SetGoalsFragment extends Fragment {

    private TextView stepsWarning, caloriesWarning;
    private EditText stepsEditText, caloriesEditText;
    private Button setGoalsBtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public SetGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_goals, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        stepsEditText = view.findViewById(R.id.stepsEditText);
        caloriesEditText = view.findViewById(R.id.caloriesEditText);
        stepsWarning = view.findViewById(R.id.stepsWarning);
        caloriesWarning = view.findViewById(R.id.caloriesWarning);
        setGoalsBtn = view.findViewById(R.id.setGoalsButton);
        
        setGoalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateGoals()) {
                    stepsWarning.setVisibility(View.INVISIBLE);
                    caloriesWarning.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(stepsEditText.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(caloriesEditText.getWindowToken(), 0);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("setSteps", Integer.valueOf(stepsEditText.getText().toString()));
                    map.put("setCalories", Integer.valueOf(caloriesEditText.getText().toString()));

                    database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Mar-05-2021").updateChildren(map);
                    Toast.makeText(getActivity(), "Successfully set goals", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validateGoals() {
        boolean valid = true;
        if (stepsEditText.getText().toString().equals("")) {
            stepsWarning.setText("Steps count cannot be empty");
            stepsWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            stepsWarning.setVisibility(View.INVISIBLE);
        }

        if (caloriesEditText.getText().toString().equals("")) {
            caloriesWarning.setText("Calories count cannot be empty");
            caloriesWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            caloriesWarning.setVisibility(View.INVISIBLE);
        }

        if (valid) {
            String regex = "^[0-9]+$";
            if (!Pattern.compile(regex).matcher(stepsEditText.getText().toString()).matches() && !stepsEditText.getText().toString().equals("")) {
                stepsWarning.setText("Steps count can only be numbers");
                stepsWarning.setVisibility(View.VISIBLE);
                valid = false;
            } else {
                stepsWarning.setVisibility(View.INVISIBLE);
            }

            if (!Pattern.compile(regex).matcher(caloriesEditText.getText().toString()).matches() && !caloriesEditText.getText().toString().equals("")) {
                caloriesWarning.setText("Calories count can only be numbers");
                caloriesWarning.setVisibility(View.VISIBLE);
                valid = false;
            } else {
                caloriesWarning.setVisibility(View.INVISIBLE);
            }
        }

        if (!valid) {
            return false;
        }
        else {
            return true;
        }
    }
}