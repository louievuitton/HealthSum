package com.stevenlouie.healthsum;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoDialog extends AppCompatDialogFragment {

    private TextView carbsInfo, fatInfo, proteinInfo;
    private Button closeBtn;
    private boolean goalsSet;
    private String date;

    public InfoDialog() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.info_dialog, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
            goalsSet = bundle.getBoolean("set");
        }

        carbsInfo = view.findViewById(R.id.carbsInfo);
        fatInfo = view.findViewById(R.id.fatInfo);
        proteinInfo = view.findViewById(R.id.proteinInfo);
        closeBtn = view.findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setData();

        return builder.create();
    }

    private void setData() {
        if (goalsSet) {
            FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(date).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int recLowCarbs = (int) (Double.valueOf(dataSnapshot.child("calorieGoal").getValue().toString()) * .45) / 4;
                    int recHighCarbs = (int) (Double.valueOf(dataSnapshot.child("calorieGoal").getValue().toString()) * .65) / 4;
                    int recLowFat = (int) (Double.valueOf(dataSnapshot.child("calorieGoal").getValue().toString()) * .20) / 9;
                    int recHighFat = (int) (Double.valueOf(dataSnapshot.child("calorieGoal").getValue().toString()) * .35) / 9;
                    int recLowProtein = (int) (Double.valueOf(dataSnapshot.child("calorieGoal").getValue().toString()) * .10) / 4;
                    int recHighProtein = (int) (Double.valueOf(dataSnapshot.child("calorieGoal").getValue().toString()) * .35) / 4;

                    carbsInfo.setText("Recommend " + recLowCarbs + "-" + recHighCarbs + " grams of carbs");
                    fatInfo.setText("Recommend " + recLowFat + "-" + recHighFat + " grams of fat");
                    proteinInfo.setText("Recommend " + recLowProtein + "-" + recHighProtein + " grams of protein");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            carbsInfo.setText("45-65% of calories from carbohydrates");
            fatInfo.setText("20-35% of calories from fats");
            proteinInfo.setText("10-35% of calories from protein");
        }
    }
}
