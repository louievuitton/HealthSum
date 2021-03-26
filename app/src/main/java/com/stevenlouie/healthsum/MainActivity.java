package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String date;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            date = intent.getStringExtra("date");
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date);

        final Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("activity", "main");

        Fragment startFragment = new HomeFragment();
        startFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, startFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        bundle.putString("date", date);
                        selectedFragment = new HomeFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.setGoalsFragment:
                        bundle.putString("date", date);
                        selectedFragment = new SetGoalsFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.weightInFragment:
                        selectedFragment = new WeightInFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.chartsFragment:
                        selectedFragment = new ChartsFragment();
                        selectedFragment.setArguments(bundle);
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

    @Override
    public void onBackPressed() {
    }
}
