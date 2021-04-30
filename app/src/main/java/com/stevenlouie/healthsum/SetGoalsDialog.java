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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stevenlouie.healthsum.api.NutritionAPI;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SetGoalsDialog extends AppCompatDialogFragment {

    private TextView caloriesWarning;
    private EditText caloriesEditText;
    private Button setGoalsBtn, cancelBtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String date;

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

        caloriesEditText = view.findViewById(R.id.caloriesEditText);
        caloriesWarning = view.findViewById(R.id.caloriesWarning);
        setGoalsBtn = view.findViewById(R.id.setGoalsButton);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        setGoalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateGoals()) {
                    caloriesWarning.setVisibility(View.INVISIBLE);

                    final HashMap<String, Object> map = new HashMap<>();
                    map.put("calorieGoal", Integer.valueOf(caloriesEditText.getText().toString()));

                    database.getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(date)) {
                                int caloriesConsumed = Integer.valueOf(dataSnapshot.child(date).child("calorieGoal").getValue().toString()) - Integer.valueOf(dataSnapshot.child(date).child("caloriesLeft").getValue().toString());
                                if (Integer.valueOf(caloriesEditText.getText().toString()) - caloriesConsumed < 0) {
                                    map.put("caloriesLeft", 0);
                                }
                                else {
                                    map.put("caloriesLeft", Integer.valueOf(caloriesEditText.getText().toString()) - caloriesConsumed);
                                }
                                map.put("caloriesBurned", Integer.valueOf(dataSnapshot.child(date).child("caloriesBurned").getValue().toString()));
                            }
                            else {
                                map.put("caloriesLeft", Integer.valueOf(caloriesEditText.getText().toString()));
                                map.put("caloriesBurned", 0);
                            }
                            database.getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date).updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(caloriesEditText.getWindowToken(), 0);

                    Toast.makeText(getActivity(), "Successfully set goals", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
