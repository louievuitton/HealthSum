package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.stevenlouie.healthsum.api.NutritionAPI;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SetGoalsDialog extends AppCompatDialogFragment {

    private TextView stepsWarning, caloriesWarning;
    private EditText stepsEditText, caloriesEditText;
    private Button setGoalsBtn, cancelBtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String date;
    private String parentActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_set_goals, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

//        stepsEditText = view.findViewById(R.id.stepsEditText);
        caloriesEditText = view.findViewById(R.id.caloriesEditText);
//        stepsWarning = view.findViewById(R.id.stepsWarning);
        caloriesWarning = view.findViewById(R.id.caloriesWarning);
        setGoalsBtn = view.findViewById(R.id.setGoalsButton);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        setGoalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateGoals()) {
//                    stepsWarning.setVisibility(View.INVISIBLE);
                    caloriesWarning.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(stepsEditText.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(caloriesEditText.getWindowToken(), 0);

                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("setSteps", Integer.valueOf(stepsEditText.getText().toString()));
                    map.put("caloriesLeft", Integer.valueOf(caloriesEditText.getText().toString()));
                    map.put("calorieGoal", Integer.valueOf(caloriesEditText.getText().toString()));
                    map.put("caloriesBurned", 0);

                    database.getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date).updateChildren(map);
                    Toast.makeText(getActivity(), "Successfully set goals", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Cancel button clicked", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    private boolean validateGoals() {
        boolean valid = true;
//        if (stepsEditText.getText().toString().equals("")) {
//            stepsWarning.setText("Steps count cannot be empty");
//            stepsWarning.setVisibility(View.VISIBLE);
//            valid = false;
//        }
//        else {
//            stepsWarning.setVisibility(View.INVISIBLE);
//        }

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
//            if (!Pattern.compile(regex).matcher(stepsEditText.getText().toString()).matches() && !stepsEditText.getText().toString().equals("")) {
//                stepsWarning.setText("Steps count can only be numbers");
//                stepsWarning.setVisibility(View.VISIBLE);
//                valid = false;
//            } else {
//                stepsWarning.setVisibility(View.INVISIBLE);
//            }

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
