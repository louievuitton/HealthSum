package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BreakfastActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ConstraintLayout statisticsLayout;
    private TextView caloriesConsumed, listTextView;
    private RecyclerView breakfastRecView;
    private MealRecViewAdapter adapter;
    private FirebaseAuth auth;
//    private DatabaseReference database;
    private String date;
    private boolean goalsSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast);

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
            goalsSet = intent.getBooleanExtra("goalsSet", false);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        statisticsLayout = findViewById(R.id.statisticsLayout);
        listTextView = findViewById(R.id.listTextView);
        caloriesConsumed = findViewById(R.id.caloriesConsumed);
        adapter = new MealRecViewAdapter(this);
        breakfastRecView = findViewById(R.id.breakfastRecView);

        final Bundle bundle = new Bundle();
        bundle.putString("date", date);
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
                        selectedFragment.setArguments(bundle);
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

        if (!goalsSet) {
            statisticsLayout.setVisibility(View.GONE);
            breakfastRecView.setVisibility(View.GONE);
            listTextView.setText("You haven't set your goals for this day.\nPlease set your goals first in the Set Goals page.");
        }
        else {
            statisticsLayout.setVisibility(View.VISIBLE);
            breakfastRecView.setAdapter(adapter);
            breakfastRecView.setLayoutManager(new LinearLayoutManager(this));

            final ArrayList<Meal> list = new ArrayList<>();

            auth = FirebaseAuth.getInstance();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child(date);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("breakfast")) {
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
}