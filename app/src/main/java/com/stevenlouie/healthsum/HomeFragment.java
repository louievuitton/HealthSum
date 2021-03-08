package com.stevenlouie.healthsum;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private LinearLayout mealsLayout;
    private RelativeLayout noDataLayout;
    private TextView totalCaloriesLeft, totalCaloriesGained, totalCaloriesBurned, totalProteinConsumed, totalCarbsConsumed, totalFatConsumed;
    private ProgressBar caloriesProgressBar;
    private LinearLayout fab_full;
    private FloatingActionButton fab;
    private TextView fab_text;
    private ScrollView scrollView;
    private CardView breakfastCardView, lunchCardView, dinnerCardView, activityCardView;
    private String date;
    private FirebaseAuth auth;
    private DatabaseReference database;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mealsLayout = view.findViewById(R.id.mealsLayout);
        noDataLayout = view.findViewById(R.id.noDataLayout);
        totalCaloriesLeft = view.findViewById(R.id.caloriesConsumed);
        totalCaloriesGained = view.findViewById(R.id.totalCaloriesGained);
        totalCaloriesBurned = view.findViewById(R.id.totalCaloriesBurned);
        totalProteinConsumed = view.findViewById(R.id.totalProteinConsumed);
        totalCarbsConsumed = view.findViewById(R.id.totalCarbsConsumed);
        totalFatConsumed = view.findViewById(R.id.totalFatConsumed);
        caloriesProgressBar = view.findViewById(R.id.caloriesProgressBar);
        scrollView = view.findViewById(R.id.layoutScrollView);
        breakfastCardView = view.findViewById(R.id.breakfast_cardview);
        lunchCardView = view.findViewById(R.id.lunch_cardview);
        dinnerCardView = view.findViewById(R.id.dinner_cardview);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child(date);

        fetchData();

        breakfastCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BreakfastActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("goalsSet", true);
                startActivity(intent);
            }
        });

        lunchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LunchActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("goalsSet", false);
                startActivity(intent);
            }
        });

        dinnerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DinnerActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("goalsSet", false);
                startActivity(intent);
            }
        });

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
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        AddMealDialog dialog = new AddMealDialog();
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "Add Dialog");
    }

    private void fetchData() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("setCalories").getValue().toString().equals("0")) {
                    totalCaloriesLeft.setText(dataSnapshot.child("setCalories").getValue().toString());

                    if (dataSnapshot.hasChild("totalCalories")) {
                        int totalCalories = 0;
                        for (DataSnapshot snapshot: dataSnapshot.child("totalCalories").getChildren()) {
                            totalCalories += Integer.valueOf(snapshot.getValue().toString());
                        }

                        int totalCarbs = 0;
                        for (DataSnapshot snapshot: dataSnapshot.child("totalCarbs").getChildren()) {
                            totalCarbs += Integer.valueOf(snapshot.getValue().toString());
                        }

                        int totalFat = 0;
                        for (DataSnapshot snapshot: dataSnapshot.child("totalFat").getChildren()) {
                            totalFat += Integer.valueOf(snapshot.getValue().toString());
                        }

                        int totalProtein = 0;
                        for (DataSnapshot snapshot: dataSnapshot.child("totalProtein").getChildren()) {
                            totalProtein += Integer.valueOf(snapshot.getValue().toString());
                        }
                        totalCaloriesGained.setText(String.valueOf(totalCalories));
                        totalCarbsConsumed.setText(String.valueOf(totalCarbs) + "g");
                        totalFatConsumed.setText(String.valueOf(totalFat) + "g");
                        totalProteinConsumed.setText(String.valueOf(totalProtein) + "g");
                    }
                }
                else {
                    mealsLayout.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        if (mealsLayout.getVisibility() == View.VISIBLE) {
//            database.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.hasChild("totalCalories")) {
//                        int totalCalories = 0;
//                        for (DataSnapshot snapshot: dataSnapshot.child("totalCalories").getChildren()) {
//                            totalCalories += Integer.valueOf(snapshot.getValue().toString());
//                        }
//
//                        int totalCarbs = 0;
//                        for (DataSnapshot snapshot: dataSnapshot.child("totalCarbs").getChildren()) {
//                            totalCarbs += Integer.valueOf(snapshot.getValue().toString());
//                        }
//
//                        int totalFat = 0;
//                        for (DataSnapshot snapshot: dataSnapshot.child("totalFat").getChildren()) {
//                            totalFat += Integer.valueOf(snapshot.getValue().toString());
//                        }
//
//                        int totalProtein = 0;
//                        for (DataSnapshot snapshot: dataSnapshot.child("totalProtein").getChildren()) {
//                            totalProtein += Integer.valueOf(snapshot.getValue().toString());
//                        }
//                        totalCaloriesGained.setText(String.valueOf(totalCalories));
//                        totalCarbsConsumed.setText(String.valueOf(totalCarbs) + "g");
//                        totalFatConsumed.setText(String.valueOf(totalFat) + "g");
//                        totalProteinConsumed.setText(String.valueOf(totalProtein) + "g");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }
}