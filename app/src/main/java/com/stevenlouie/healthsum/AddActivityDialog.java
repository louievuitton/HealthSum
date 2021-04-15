package com.stevenlouie.healthsum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
    private Spinner num_servings_spinner;
    private String mealType = "breakfast";
    private String date;
    private int numServings = 1;
    private String type;

    public AddActivityDialog(String type) {
        this.type = type;
    }

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
        num_servings_spinner = view.findViewById(R.id.num_servings_spinner);

        if (type.equals("exercise")) {
            addMealLayout.setVisibility(View.GONE);
            addExerciseLayout.setVisibility(View.VISIBLE);
        }
        else {
            addExerciseLayout.setVisibility(View.GONE);
            addMealLayout.setVisibility(View.VISIBLE);
        }

        final Integer[] servings_array = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, servings_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        num_servings_spinner.setAdapter(adapter);
        num_servings_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numServings = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // api to get nutrition details
                if (validateInput()) {
                    foodWarning.setVisibility(View.GONE);
                    exerciseWarning.setVisibility(View.GONE);
                    NutritionAPI api = new NutritionAPI(getActivity());
                    if (type.equals("exercise")) {
                        api.fetchExerciseData(date, exerciseEditText.getText().toString());
                        exerciseEditText.getText().clear();
                        exerciseEditText.clearFocus();
                    }
                    else if (type.equals("breakfast")) {
                        api.fetchNutritionData(date, "breakfast", mealEditText.getText().toString(), numServings);
                        mealEditText.getText().clear();
                        mealEditText.clearFocus();
                    }
                    else if (type.equals("lunch")) {
                        api.fetchNutritionData(date, "lunch", mealEditText.getText().toString(), numServings);
                        mealEditText.getText().clear();
                        mealEditText.clearFocus();
                    }
                    else if (type.equals("dinner")) {
                        api.fetchNutritionData(date, "dinner", mealEditText.getText().toString(), numServings);
                        mealEditText.getText().clear();
                        mealEditText.clearFocus();
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

        if (type.equals("exercise")) {
            if (exerciseEditText.getText().toString().equals("")) {
                exerciseWarning.setText("Field cannot be blank");
                exerciseWarning.setVisibility(View.VISIBLE);
                valid = false;
            }
        }
        else {
            if (mealEditText.getText().toString().equals("")) {
                foodWarning.setText("Field cannot be blank");
                foodWarning.setVisibility(View.VISIBLE);
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
