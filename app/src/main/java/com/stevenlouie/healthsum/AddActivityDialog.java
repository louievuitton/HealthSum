package com.stevenlouie.healthsum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stevenlouie.healthsum.api.NutritionAPI;

import java.util.HashMap;

public class AddActivityDialog extends AppCompatDialogFragment {

    private RelativeLayout addMealLayout, addExerciseLayout;
    private TextView foodWarning, exerciseWarning;
    private EditText mealEditText, exerciseEditText;
    private Button addBtn, cancelBtn;
    private RadioGroup radioGroup, selectActivityRG;
    private String mealType = "breakfast";
    private String date;
    private String selectedActivity = "meal";
//    private FirebaseAuth auth;
//    private int totalCalories = 0;
//    private int totalCarbs = 0;
//    private int totalFat = 0;
//    private int totalProtein = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_activity, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
        }

        addMealLayout = view.findViewById(R.id.addMealLayout);
        addExerciseLayout = view.findViewById(R.id.addExerciseLayout);
        exerciseWarning = view.findViewById(R.id.exerciseWarning);
        exerciseEditText = view.findViewById(R.id.exerciseEditText);
        foodWarning = view.findViewById(R.id.foodWarning);
        mealEditText = view.findViewById(R.id.mealEditText);
        addBtn = view.findViewById(R.id.addActivityBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        radioGroup = view.findViewById(R.id.radioGroup);
        selectActivityRG = view.findViewById(R.id.selectActivityRG);
        selectActivityRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mealRB:
                        selectedActivity = "meal";
                        addExerciseLayout.setVisibility(View.GONE);
                        mealEditText.getText().clear();
                        foodWarning.setVisibility(View.GONE);
                        addMealLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.exerciseRB:
                        selectedActivity = "exercise";
                        addMealLayout.setVisibility(View.GONE);
                        exerciseEditText.getText().clear();
                        exerciseWarning.setVisibility(View.GONE);
                        addExerciseLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.breakfastRB:
                        mealType = "breakfast";
                        break;
                    case R.id.lunchRB:
                        mealType = "lunch";
                        break;
                    case R.id.dinnerRB:
                        mealType = "dinner";
                        break;
                    default:
                        break;
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // api to get nutrition details
                if (validateInput()) {
                    NutritionAPI api = new NutritionAPI(getActivity());
                    if (selectedActivity.equals("meal")) {
                        foodWarning.setVisibility(View.GONE);
                        api.fetchNutritionData(date, mealType, mealEditText.getText().toString());
                    }
                    else if (selectedActivity.equals("exercise")) {
                        exerciseWarning.setVisibility(View.GONE);
                        api.fetchExerciseData(date, exerciseEditText.getText().toString());
                    }
                    Toast.makeText(getActivity(), "Successfully added activity", Toast.LENGTH_SHORT).show();
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

    private boolean validateInput() {
        boolean valid = true;

        if (selectedActivity.equals("meal")) {
            if (mealEditText.getText().toString().equals("")) {
                foodWarning.setText("Field cannot be blank");
                foodWarning.setVisibility(View.VISIBLE);
                valid = false;
            }
        }
        else if (selectedActivity.equals("exercise")) {
            if (exerciseEditText.getText().toString().equals("")) {
                exerciseWarning.setText("Field cannot be blank");
                exerciseWarning.setVisibility(View.VISIBLE);
                valid = false;
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