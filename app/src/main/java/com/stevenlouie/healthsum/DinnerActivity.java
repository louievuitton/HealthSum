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

public class DinnerActivity extends AppCompatActivity {

    private LinearLayout fab_full;
    private TextView caloriesConsumed, listTextView;
    private RecyclerView dinnerRecView;
    private MealRecViewAdapter adapter;
    private FirebaseAuth auth;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
        }

        listTextView = findViewById(R.id.listTextView);
        caloriesConsumed = findViewById(R.id.caloriesConsumed);
        adapter = new MealRecViewAdapter(this);
        dinnerRecView = findViewById(R.id.dinnerRecView);
        fab_full = findViewById(R.id.fab_full);

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
        AddActivityDialog dialog = new AddActivityDialog("dinner");
        dialog.setArguments(bundle);
        dialog.show(DinnerActivity.this.getSupportFragmentManager(), "Add Dinner Dialog");
    }

    private void fetchData() {
        auth = FirebaseAuth.getInstance();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("dinner")) {
                    caloriesConsumed.setText(dataSnapshot.child("totalCalories").child("dinner").getValue().toString());

                    ArrayList<Meal> list = new ArrayList<>();

                    list.clear();
                    for (DataSnapshot snapshot: dataSnapshot.child("dinner").getChildren()) {
                        list.add(snapshot.getValue(Meal.class));
                    }
                    if (list.size() != 0) {
                        dinnerRecView.setAdapter(adapter);
                        dinnerRecView.setLayoutManager(new LinearLayoutManager(DinnerActivity.this));

                        listTextView.setText("What you had");
                        dinnerRecView.setVisibility(View.VISIBLE);
                        adapter.setUserId(auth.getCurrentUser().getUid());
                        adapter.setDate(date);
                        adapter.setMealType("dinner");
                        adapter.setMeals(list);
                    }
                }
                else {
                    dinnerRecView.setVisibility(View.GONE);
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