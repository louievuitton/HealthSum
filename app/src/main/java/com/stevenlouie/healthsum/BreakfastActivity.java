package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class BreakfastActivity extends AppCompatActivity {

    private LinearLayout fab_full;
    private TextView caloriesConsumed, listTextView, proteinConsumed, carbsConsumed, fatConsumed, fab_text;
    private RecyclerView breakfastRecView;
    private MealRecViewAdapter adapter;
    private FirebaseAuth auth;
    private String date;
    private AddActivityDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast);
        
        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
        }

        listTextView = findViewById(R.id.listTextView);
        caloriesConsumed = findViewById(R.id.caloriesConsumed);
        adapter = new MealRecViewAdapter(this);
        breakfastRecView = findViewById(R.id.breakfastRecView);
        fab_full = findViewById(R.id.fab_full);
        fab_text = findViewById(R.id.fab_text);
        proteinConsumed = findViewById(R.id.proteinConsumed);
        carbsConsumed = findViewById(R.id.carbsConsumed);
        fatConsumed = findViewById(R.id.fatConsumed);

        fab_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        handleFab();

        fetchData();
    }

    private void handleFab() {
        breakfastRecView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
        dialog = new AddActivityDialog("breakfast");
        dialog.setArguments(bundle);
        dialog.show(BreakfastActivity.this.getSupportFragmentManager(), "Add Breakfast Dialog");
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    private void fetchData() {
        auth = FirebaseAuth.getInstance();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("breakfast")) {
                    caloriesConsumed.setText(dataSnapshot.child("totalCalories").child("breakfast").getValue().toString());
                    proteinConsumed.setText(dataSnapshot.child("totalProtein").child("breakfast").getValue().toString() + "g");
                    carbsConsumed.setText(dataSnapshot.child("totalCarbs").child("breakfast").getValue().toString() + "g");
                    fatConsumed.setText(dataSnapshot.child("totalFat").child("breakfast").getValue().toString() + "g");

                    ArrayList<Meal> list = new ArrayList<>();

                    list.clear();
                    for (DataSnapshot snapshot: dataSnapshot.child("breakfast").getChildren()) {
                        list.add(snapshot.getValue(Meal.class));
                    }
                    if (list.size() != 0) {
                        breakfastRecView.setAdapter(adapter);
                        breakfastRecView.setLayoutManager(new LinearLayoutManager(BreakfastActivity.this));

                        listTextView.setText("What you had");
                        breakfastRecView.setVisibility(View.VISIBLE);
                        adapter.setUserId(auth.getCurrentUser().getUid());
                        adapter.setDate(date);
                        adapter.setMealType("breakfast");
                        adapter.setMeals(list);
                    }
                }
                else {
                    breakfastRecView.setVisibility(View.GONE);
                    listTextView.setText("You haven't logged any meals yet.\nPlease add your first meal.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setDate(String date) {
        this.date = date;
    }
}