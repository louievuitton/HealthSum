package com.stevenlouie.healthsum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private TextView totalCaloriesLeft, totalCaloriesGained, totalCaloriesBurned, totalProteinConsumed, totalCarbsConsumed, totalFatConsumed;
    private ProgressBar caloriesProgressBar;
    private LinearLayout fab_full;
    private FloatingActionButton fab;
    private TextView fab_text;
    private ScrollView scrollView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        totalCaloriesLeft = view.findViewById(R.id.totalCaloriesLeft);
        totalCaloriesGained = view.findViewById(R.id.totalCaloriesGained);
        totalCaloriesBurned = view.findViewById(R.id.totalCaloriesBurned);
        totalProteinConsumed = view.findViewById(R.id.totalProteinConsumed);
        totalCarbsConsumed = view.findViewById(R.id.totalCarbsConsumed);
        totalFatConsumed = view.findViewById(R.id.totalFatConsumed);
        caloriesProgressBar = view.findViewById(R.id.caloriesProgressBar);
        scrollView = view.findViewById(R.id.layoutScrollView);

        fab_full = view.findViewById(R.id.fab_full);
        fab = view.findViewById(R.id.fab);
        fab_text = view.findViewById(R.id.fab_text);
        handleFab();

        fab_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        return view;
    }

    private void handleFab() {
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab_text.setVisibility(View.GONE);
                } else if (scrollX == scrollY) {
                    fab_text.setVisibility(View.VISIBLE);
                } else {
                    fab_text.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void openDialog() {
        AddMealDialog dialog = new AddMealDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }
}