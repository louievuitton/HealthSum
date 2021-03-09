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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BreakfastActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RelativeLayout statisticsLayout, breakfastContents;
    private TextView caloriesConsumed, listTextView, textView3;
    private ProgressBar caloriesProgressBar;
    private RecyclerView breakfastRecView;
    private MealRecViewAdapter adapter;
    private FirebaseAuth auth;
//    private DatabaseReference database;
    private String date;
    private boolean breakfastSet;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;
    private Button datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast);

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
            breakfastSet = intent.getBooleanExtra("breakfastSet", false);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        statisticsLayout = findViewById(R.id.statisticsLayout);
        listTextView = findViewById(R.id.listTextView);
        textView3 = findViewById(R.id.textView3);
        caloriesProgressBar = findViewById(R.id.caloriesProgressBar);
        caloriesConsumed = findViewById(R.id.caloriesConsumed);
        adapter = new MealRecViewAdapter(this);
        breakfastRecView = findViewById(R.id.breakfastRecView);
        datepicker = findViewById(R.id.datepicker);
        breakfastContents = findViewById(R.id.breakfastContents);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final Bundle bundle = new Bundle();
                bundle.putString("date", date);

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
                getSupportFragmentManager().beginTransaction().replace(R.id.breakfastContainer, selectedFragment).commit();
                breakfastContents.setVisibility(View.GONE);
                return true;
            }
        });

        calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(BreakfastActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDayOfMonth = dayOfMonth;

                        SimpleDateFormat timeStamp = new SimpleDateFormat("MM-dd-yyyy");
                        date = timeStamp.format(calendar.getTime());
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                            datepicker.setText(month_date.format(calendar.getTime()) + ", " + dayOfMonth);
                        }

                        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(date)) {
                                    breakfastSet = true;
                                }
                                else {
                                    breakfastSet = false;
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

//        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild(date)) {
//                    breakfastSet = true;
//                }
//                else {
//                    breakfastSet = false;
//                }
//                fetchData();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        fetchData();
    }

    private void fetchData() {
        if (!breakfastSet) {
            textView3.setVisibility(View.GONE);
            caloriesConsumed.setVisibility(View.GONE);
            caloriesProgressBar.setVisibility(View.GONE);
            breakfastRecView.setVisibility(View.GONE);
            listTextView.setText("You haven't logged any meals yet.\nStart by adding your first meal.");
        }
        else {
//            statisticsLayout.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            caloriesConsumed.setVisibility(View.VISIBLE);
            caloriesProgressBar.setVisibility(View.VISIBLE);
            breakfastRecView.setAdapter(adapter);
            breakfastRecView.setLayoutManager(new LinearLayoutManager(this));

            final ArrayList<Meal> list = new ArrayList<>();

            auth = FirebaseAuth.getInstance();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child(date);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.hasChild("breakfast")) {
                        caloriesConsumed.setText(dataSnapshot.child("totalCalories").child("breakfast").getValue().toString());

                        list.clear();
                        for (DataSnapshot snapshot: dataSnapshot.child("breakfast").getChildren()) {
                            list.add(snapshot.getValue(Meal.class));
                        }
                        if (list.size() != 0) {
                            listTextView.setText("What you had");
                            breakfastRecView.setVisibility(View.VISIBLE);
                            adapter.setMeals(list);
                        }
//                    }
//                    else {
//                        listTextView.setText("You haven't logged any meals yet.\nPlease add your first meal.");
//                    }
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
}