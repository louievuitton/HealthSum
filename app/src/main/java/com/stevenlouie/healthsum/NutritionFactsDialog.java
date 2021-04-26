package com.stevenlouie.healthsum;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NutritionFactsDialog extends AppCompatDialogFragment {

    private Meal meal;
    private TextView mealNameTextView, numServingsValue, numCaloriesValue, totalFatsValue, saturatedFatValue, cholesterolValue, sodiumValue, totalCarbsValue, dietaryFiberValue, totalSugarsValue, proteinValue;
    private FloatingActionButton fab;

    public NutritionFactsDialog(Meal meal) {
        this.meal = meal;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.nutrition_facts_layout, null);
        builder.setView(view);

        mealNameTextView = view.findViewById(R.id.mealNameTextView);
        numServingsValue = view.findViewById(R.id.numServingsValue);
        numCaloriesValue = view.findViewById(R.id.numCaloriesValue);
        totalFatsValue = view.findViewById(R.id.totalFatsValue);
        saturatedFatValue = view.findViewById(R.id.saturatedFatValue);
        cholesterolValue = view.findViewById(R.id.cholesterolValue);
        sodiumValue = view.findViewById(R.id.sodiumValue);
        totalCarbsValue = view.findViewById(R.id.totalCarbsValue);
        dietaryFiberValue = view.findViewById(R.id.dietaryFiberValue);
        totalSugarsValue = view.findViewById(R.id.totalSugarsValue);
        proteinValue = view.findViewById(R.id.proteinValue);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mealNameTextView.setText("for " + meal.getMeal());
        numServingsValue.setText(meal.getServings() + "");
        numCaloriesValue.setText(meal.getCalories() + "");
        totalFatsValue.setText(meal.getFat() + "g");
        saturatedFatValue.setText(meal.getSaturatedFat() + "g");
        cholesterolValue.setText(meal.getCholesterol() + "mg");
        sodiumValue.setText(meal.getSodium() + "mg");
        totalCarbsValue.setText(meal.getCarbs() + "g");
        dietaryFiberValue.setText(meal.getDietaryFiber() + "g");
        totalSugarsValue.setText(meal.getSugars() + "g");
        proteinValue.setText(meal.getProtein() + "g");

        return builder.create();
    }

}
