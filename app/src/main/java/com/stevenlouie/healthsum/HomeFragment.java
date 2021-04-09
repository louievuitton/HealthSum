package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private LinearLayout activityLayout;
    private RelativeLayout noDataLayout;
    private TextView totalCaloriesLeft, totalCaloriesGained, totalCaloriesBurned, totalProteinConsumed, totalCarbsConsumed, totalFatConsumed, breakfast_details, breakfast_calories, exerciseDetails, exerciseCaloriesBurned, fab_text;
    private ProgressBar caloriesProgressBar;
    private LinearLayout fab_full;
    private FloatingActionButton fab;
    private ScrollView scrollView;
    private CardView breakfastCardView, lunchCardView, dinnerCardView, exerciseCardView;
    private Button datepicker;
    private String date;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private String parentActivity;
    private boolean goalsSet = false;
    private int totalCalories = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
            parentActivity = bundle.getString("activity");
        }

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activityLayout = view.findViewById(R.id.activityLayout);
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
        exerciseCardView = view.findViewById(R.id.exerciseCardView);
        datepicker = view.findViewById(R.id.datepicker);
        breakfast_details = view.findViewById(R.id.breakfast_details);
        exerciseDetails = view.findViewById(R.id.exerciseDetails);
        breakfast_calories = view.findViewById(R.id.breakfast_calories);
        exerciseCaloriesBurned = view.findViewById(R.id.exerciseCaloriesBurned);

        final SimpleDateFormat timeStamp = new SimpleDateFormat("MM-dd-yyyy");
        final SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
            datepicker.setText("Today");
        }
        else {
            String dom = date.substring(3, 5);
            if (dom.charAt(0) == '0') {
                dom = dom.substring(1);
            }
            datepicker.setText((new DateFormatSymbols().getMonths()[Integer.valueOf(date.substring(0, 2))-1]).substring(0, 3) + ", " + dom);
        }

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(userId);

        fetchData();

        calendar = Calendar.getInstance();
        selectedYear = Integer.valueOf(date.substring(6));
        selectedMonth = Integer.valueOf(date.substring(0, 2)) - 1;
        selectedDayOfMonth = Integer.valueOf(date.substring(3, 5));

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDayOfMonth = dayOfMonth;

                        date = timeStamp.format(calendar.getTime());
                        if (parentActivity.equals("main")) {
                            ((MainActivity) getActivity()).setDate(date);
                        }
                        else if (parentActivity.equals("breakfast")) {
                            ((BreakfastActivity) getActivity()).setDate(date);
                        }
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            datepicker.setText(month_date.format(calendar.getTime()) + ", " + dayOfMonth);
                        }

                        fetchData();
                    }
                }, selectedYear, selectedMonth, selectedDayOfMonth);
                datePickerDialog.show();
            }
        });

        breakfastCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                database.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @SuppressLint("ResourceType")
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Intent intent = new Intent(getActivity(), BreakfastActivity.class);
//                        intent.putExtra("date", date);
//                        if (dataSnapshot.hasChild("breakfast")) {
//                            intent.putExtra("breakfastSet", true);
//                        }
//                        else {
//                            intent.putExtra("breakfastSet", false);
//                        }
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                Intent intent = new Intent(getActivity(), BreakfastActivity.class);
                intent.putExtra("date", date);
//                intent.putExtra("breakfastSet", true);
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

        exerciseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                intent.putExtra("date", date);
//                intent.putExtra("exerciseSet", true);
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
        SetGoalsDialog dialog = new SetGoalsDialog();
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "Set Goals Dialog");
    }

    private void fetchData() {
        breakfast_details.setText("");
        breakfast_calories.setText("");
        exerciseDetails.setText("");
        exerciseCaloriesBurned.setText("");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(date)) {
                    goalsSet = true;
                    database.child(date).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child("calorieGoal").getValue().toString().equals("0")) {
                                activityLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                fab_full.setVisibility(View.VISIBLE);
                                totalCalories = Integer.valueOf(dataSnapshot.child("calorieGoal").getValue().toString());
                                totalCaloriesLeft.setText(dataSnapshot.child("caloriesLeft").getValue().toString());
                                totalCaloriesBurned.setText(dataSnapshot.child("caloriesBurned").getValue().toString());
                                caloriesProgressBar.setMax(totalCalories);
                                caloriesProgressBar.setProgress(totalCalories - Integer.valueOf(totalCaloriesLeft.getText().toString()));

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

                                    if (dataSnapshot.hasChild("breakfast")) {
                                        String breakfastMeals = "";
                                        for (DataSnapshot snapshot: dataSnapshot.child("breakfast").getChildren()) {
                                            breakfastMeals += snapshot.child("meal").getValue().toString() + ", ";
                                        }
                                        if (breakfastMeals.substring(breakfastMeals.length()-2).equals(", ")) {
                                            breakfastMeals = breakfastMeals.substring(0, breakfastMeals.length()-2);
                                        }
                                        if (breakfastMeals.length() > 40) {
                                            breakfastMeals = breakfastMeals.substring(0, 40) + "...";
                                        }
                                        breakfast_details.setText(breakfastMeals);
                                        breakfast_calories.setText("Total Calories: " + dataSnapshot.child("totalCalories").child("breakfast").getValue().toString());
                                    }
                                }
                                else {
                                    totalCaloriesGained.setText("0");
                                    totalCarbsConsumed.setText("0g");
                                    totalFatConsumed.setText("0g");
                                    totalProteinConsumed.setText("0g");
                                }

                                if (dataSnapshot.hasChild("exercises")) {
                                    String exercises = "";
                                    for (DataSnapshot snapshot: dataSnapshot.child("exercises").getChildren()) {
                                        exercises += snapshot.child("exercise").getValue().toString() + ", ";
                                    }
                                    if (exercises.substring(exercises.length()-2).equals(", ")) {
                                        exercises = exercises.substring(0, exercises.length()-2);
                                    }
                                    if (exercises.length() > 40) {
                                        exercises = exercises.substring(0, 40) + "...";
                                    }
                                    exerciseDetails.setText(exercises);
                                    exerciseCaloriesBurned.setText("Calories Burned: " + dataSnapshot.child("caloriesBurned").getValue().toString());
                                }
                            }
                            else {
                                activityLayout.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                totalCaloriesBurned.setText(dataSnapshot.child("caloriesBurned").getValue().toString());
//                                fab_full.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    goalsSet = false;
                    totalCaloriesLeft.setText("0");
                    totalCaloriesGained.setText("0");
                    totalCarbsConsumed.setText("0g");
                    totalFatConsumed.setText("0g");
                    totalProteinConsumed.setText("0g");
                    totalCaloriesBurned.setText("0");
                    activityLayout.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                    caloriesProgressBar.setMax(0);
                    caloriesProgressBar.setProgress(0);
//                    fab_full.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.child("setCalories").getValue().toString().equals("0")) {
//                    totalCaloriesLeft.setText(dataSnapshot.child("setCalories").getValue().toString());
//
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
//                else {
//                    mealsLayout.setVisibility(View.GONE);
//                    noDataLayout.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

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

    private void updateProgressBar() {
        caloriesProgressBar.setMax(totalCalories);
        caloriesProgressBar.setProgress(Integer.valueOf(totalCaloriesLeft.getText().toString()));
    }
}