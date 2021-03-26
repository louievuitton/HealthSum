package com.stevenlouie.healthsum;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
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

public class DeleteMealDialog extends AppCompatDialogFragment {

    private Button deleteBtn, cancelBtn;
    private String date;
    private String userId;
    private String id;
    private String mealType;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog, null);
        builder.setView(view);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
            id = bundle.getString("id");
            userId = bundle.getString("userId");
            mealType = bundle.getString("mealType");
        }

        deleteBtn = view.findViewById(R.id.deleteBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child(date);
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int totalCalories = Integer.valueOf(dataSnapshot.child("totalCalories").child(mealType).getValue().toString());
                        int totalCarbs = Integer.valueOf(dataSnapshot.child("totalCarbs").child(mealType).getValue().toString());
                        int totalFat = Integer.valueOf(dataSnapshot.child("totalFat").child(mealType).getValue().toString());
                        int totalProtein = Integer.valueOf(dataSnapshot.child("totalProtein").child(mealType).getValue().toString());
                        database.child("totalCalories").child(mealType).setValue(totalCalories - bundle.getInt("calories"));
                        database.child("totalCarbs").child(mealType).setValue(totalCarbs - bundle.getInt("carbs"));
                        database.child("totalFat").child(mealType).setValue(totalFat - bundle.getInt("fat"));
                        database.child("totalProtein").child(mealType).setValue(totalProtein - bundle.getInt("protein"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
}
