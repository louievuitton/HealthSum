package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SetGoalsFragment extends Fragment {

    private TextView stepsWarning, caloriesWarning;
    private EditText stepsEditText, caloriesEditText;
    private Button setGoalsBtn, datepicker;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String date;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;

    public SetGoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
        }

        View view = inflater.inflate(R.layout.fragment_set_goals, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        stepsEditText = view.findViewById(R.id.stepsEditText);
        caloriesEditText = view.findViewById(R.id.caloriesEditText);
        stepsWarning = view.findViewById(R.id.stepsWarning);
        caloriesWarning = view.findViewById(R.id.caloriesWarning);
        setGoalsBtn = view.findViewById(R.id.setGoalsButton);
        datepicker = view.findViewById(R.id.datepicker);

        calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        
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

                    database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child(date).updateChildren(map);
                    Toast.makeText(getActivity(), "Successfully set goals", Toast.LENGTH_SHORT).show();
                }
            }
        });

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDayOfMonth = dayOfMonth;

                        SimpleDateFormat timeStamp = new SimpleDateFormat("MM-dd-yyyy");
                        date = timeStamp.format(calendar.getTime());
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                            datepicker.setText(month_date.format(calendar.getTime()) + ", " + dayOfMonth);
                        }
                    }
                }, selectedYear, selectedMonth, selectedDayOfMonth);
                datePickerDialog.show();
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