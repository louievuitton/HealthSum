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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddMealDialog extends AppCompatDialogFragment {

    private EditText mealEditText;
    private Button addBtn, cancelBtn;
    private RadioGroup radioGroup;
    private String mealType = "breakfast";
    private String date;
    private FirebaseAuth auth;

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

        auth = FirebaseAuth.getInstance();

        mealEditText = view.findViewById(R.id.mealEditText);
        addBtn = view.findViewById(R.id.addActivityBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        radioGroup = view.findViewById(R.id.radioGroup);
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

                // add the results to firebase
                HashMap<String, Object> map = new HashMap<>();
                map.put("meal", "chicken soup");
                map.put("servings", 1);
                map.put("calories", 100);
                map.put("protein", 9);
                map.put("carbs", 54);
                map.put("fat", 2);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String key = database.getReference("Users").push().getKey();
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Mar-05-2021").child(mealType).child(key).updateChildren(map);
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Mar-05-2021").child("totalCalories").child(mealType).setValue(100);
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Mar-05-2021").child("totalProtein").child(mealType).setValue(9);
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Mar-05-2021").child("totalFat").child(mealType).setValue(2);
                database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("Mar-05-2021").child("totalCarbs").child(mealType).setValue(54);

                Toast.makeText(getActivity(), "Add button clicked", Toast.LENGTH_SHORT).show();
                dismiss();
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
}
