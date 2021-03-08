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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddMealDialog extends AppCompatDialogFragment {

    private EditText mealEditText;
    private Button addBtn, cancelBtn;
    private RadioGroup radioGroup;
    private String mealType = "breakfast";
    private String date;
    private FirebaseAuth auth;
    private int totalCalories = 0;
    private int totalCarbs = 0;
    private int totalFat = 0;
    private int totalProtein = 0;

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
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child(date);
                String key = database.push().getKey();
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", key);
                map.put("meal", "steak bowl");
                map.put("servings", 1);
                map.put("calories", 100);
                map.put("protein", 19);
                map.put("carbs", 84);
                map.put("fat", 24);

                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int setCalories = Integer.valueOf(dataSnapshot.child("setCalories").getValue().toString());
                        database.child("setCalories").setValue(setCalories-100);
                        if (dataSnapshot.hasChild("totalCalories")) {
                            if (dataSnapshot.child("totalCalories").hasChild(mealType)) {
                                totalCalories = Integer.valueOf(dataSnapshot.child("totalCalories").child(mealType).getValue().toString());
                            }

                            if (dataSnapshot.child("totalCarbs").hasChild(mealType)) {
                                totalCarbs = Integer.valueOf(dataSnapshot.child("totalCarbs").child(mealType).getValue().toString());
                            }

                            if (dataSnapshot.child("totalFat").hasChild(mealType)) {
                                totalFat = Integer.valueOf(dataSnapshot.child("totalFat").child(mealType).getValue().toString());
                            }

                            if (dataSnapshot.child("totalProtein").hasChild(mealType)) {
                                totalProtein = Integer.valueOf(dataSnapshot.child("totalProtein").child(mealType).getValue().toString());
                            }

                            database.child("totalCalories").child(mealType).setValue(totalCalories+100);
                            database.child("totalCarbs").child(mealType).setValue(totalCarbs+84);
                            database.child("totalFat").child(mealType).setValue(totalFat+24);
                            database.child("totalProtein").child(mealType).setValue(totalProtein+19);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                database.child(mealType).child(key).updateChildren(map);

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
