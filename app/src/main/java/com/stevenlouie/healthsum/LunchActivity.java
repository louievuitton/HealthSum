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

public class LunchActivity extends AppCompatActivity {

    private LinearLayout fab_full;
    private TextView caloriesConsumed, listTextView, proteinConsumed, carbsConsumed, fatConsumed;
    private RecyclerView lunchRecView;
    private MealRecViewAdapter adapter;
    private FirebaseAuth auth;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
        }

        listTextView = findViewById(R.id.listTextView);
        caloriesConsumed = findViewById(R.id.caloriesConsumed);
        adapter = new MealRecViewAdapter(this);
        lunchRecView = findViewById(R.id.lunchRecView);
        fab_full = findViewById(R.id.fab_full);
        proteinConsumed = findViewById(R.id.proteinConsumed);
        carbsConsumed = findViewById(R.id.carbsConsumed);
        fatConsumed = findViewById(R.id.fatConsumed);

        fab_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        fetchData();
    }

    private void openDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        AddActivityDialog dialog = new AddActivityDialog("lunch");
        dialog.setArguments(bundle);
        dialog.show(LunchActivity.this.getSupportFragmentManager(), "Add Lunch Dialog");
    }

    private void fetchData() {
        auth = FirebaseAuth.getInstance();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("lunch")) {
                    caloriesConsumed.setText(dataSnapshot.child("totalCalories").child("lunch").getValue().toString());
                    proteinConsumed.setText(dataSnapshot.child("totalProtein").child("lunch").getValue().toString() + "g");
                    carbsConsumed.setText(dataSnapshot.child("totalCarbs").child("lunch").getValue().toString() + "g");
                    fatConsumed.setText(dataSnapshot.child("totalFat").child("lunch").getValue().toString() + "g");

                    ArrayList<Meal> list = new ArrayList<>();

                    list.clear();
                    for (DataSnapshot snapshot: dataSnapshot.child("lunch").getChildren()) {
                        list.add(snapshot.getValue(Meal.class));
                    }
                    if (list.size() != 0) {
                        lunchRecView.setAdapter(adapter);
                        lunchRecView.setLayoutManager(new LinearLayoutManager(LunchActivity.this));

                        listTextView.setText("What you had");
                        lunchRecView.setVisibility(View.VISIBLE);
                        adapter.setUserId(auth.getCurrentUser().getUid());
                        adapter.setDate(date);
                        adapter.setMealType("lunch");
                        adapter.setMeals(list);
                    }
                }
                else {
                    lunchRecView.setVisibility(View.GONE);
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