package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExerciseActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RelativeLayout statisticsLayout, exerciseContents;
    private TextView totalCaloriesBurned, listTextView, textView3;
    private ProgressBar caloriesProgressBar;
    private RecyclerView exerciseRecView;
    private ExerciseRecViewAdapter adapter;
    private FirebaseAuth auth;
    //    private DatabaseReference database;
    private String date;
    private boolean exerciseSet;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;
    private Button datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
            exerciseSet = intent.getBooleanExtra("exerciseSet", false);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        statisticsLayout = findViewById(R.id.statisticsLayout);
        listTextView = findViewById(R.id.listTextView);
        textView3 = findViewById(R.id.textView3);
        totalCaloriesBurned = findViewById(R.id.totalCaloriesBurned);
        adapter = new ExerciseRecViewAdapter(this);
        exerciseRecView = findViewById(R.id.exerciseRecView);
        datepicker = findViewById(R.id.datepicker);
        exerciseContents = findViewById(R.id.exerciseContents);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("activity", "breakfast");

                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.homeFragment:
//                        Intent intent = new Intent(BreakfastActivity.this, MainActivity.class);
//                        intent.putExtra("date", "Mar-05-2021");
//                        intent.putExtra("fragmentSelected", "home");
//                        startActivity(intent);
                        selectedFragment = new HomeFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.setGoalsFragment:
                        selectedFragment = new SetGoalsFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.weightInFragment:
                        selectedFragment = new WeightInFragment();
//                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.chartsFragment:
                        selectedFragment = new ChartsFragment();
//                        selectedFragment.setArguments(bundle);
                        break;
                }
//                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
//                frameLayout.removeAllViews();
                exerciseContents.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.exerciseContainer, selectedFragment).commit();
                return true;
            }
        });

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

        calendar = Calendar.getInstance();
        selectedYear = Integer.valueOf(date.substring(6));
        selectedMonth = Integer.valueOf(date.substring(0, 2)) - 1;
        selectedDayOfMonth = Integer.valueOf(date.substring(3, 5));

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(ExerciseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDayOfMonth = dayOfMonth;

                        date = timeStamp.format(calendar.getTime());
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            datepicker.setText(month_date.format(calendar.getTime()) + ", " + dayOfMonth);
                        }

                        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(date)) {
                                    exerciseSet = true;
                                }
                                else {
                                    exerciseSet = false;
                                }
                                fetchData();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }, selectedYear, selectedMonth, selectedDayOfMonth);
                datePickerDialog.show();
            }
        });

        fetchData();

//        if (adapter.getItemCount() == 0) {
//            breakfastSet = false;
//            fetchData();
//        }
    }

    private void fetchData() {
        if (!exerciseSet) {
//            textView3.setVisibility(View.GONE);
//            caloriesConsumed.setVisibility(View.GONE);
            exerciseRecView.setVisibility(View.GONE);
//            listTextView.setText("You haven't logged any meals yet.\nStart by adding your first meal.");
        }
        else {
//            statisticsLayout.setVisibility(View.VISIBLE);
//            textView3.setVisibility(View.VISIBLE);
//            totalCaloriesBurned.setVisibility(View.VISIBLE);
            exerciseRecView.setAdapter(adapter);
            exerciseRecView.setLayoutManager(new LinearLayoutManager(this));

            final ArrayList<Exercise> list = new ArrayList<>();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date);
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("exercises")) {
                        totalCaloriesBurned.setText(dataSnapshot.child("caloriesBurned").getValue().toString());

                        list.clear();
                        for (DataSnapshot snapshot: dataSnapshot.child("exercises").getChildren()) {
                            list.add(snapshot.getValue(Exercise.class));
                        }
                        if (list.size() != 0) {
                            listTextView.setText("What you did");
                            exerciseRecView.setVisibility(View.VISIBLE);
                            adapter.setUserId(auth.getCurrentUser().getUid());
                            adapter.setDate(date);
                            adapter.setExercises(list);
                        }
                    }
                    else {
                        listTextView.setText("You haven't logged any meals yet.\nPlease add your first meal.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void initBottomNav() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.homeFragment:
//                        Intent intent = new Intent(BreakfastActivity.this, MainActivity.class);
//                        intent.putExtra("date", "Mar-05-2021");
//                        intent.putExtra("fragmentSelected", "home");
//                        startActivity(intent);
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.setGoalsFragment:
                        selectedFragment = new SetGoalsFragment();
//                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.weightInFragment:
                        selectedFragment = new WeightInFragment();
//                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.chartsFragment:
                        selectedFragment = new ChartsFragment();
//                        selectedFragment.setArguments(bundle);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

                return true;
            }
        });
    }

    public void setDate(String date) {
        this.date = date;
    }
}