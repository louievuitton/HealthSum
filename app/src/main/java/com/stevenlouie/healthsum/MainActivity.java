package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout infoLayout, infoLayout2;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout fab_full;
    private TextView info, info2;
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
        fab_full = findViewById(R.id.fab_full);
        infoLayout = findViewById(R.id.infoLayout);
        infoLayout2 = findViewById(R.id.infoLayout2);
        info = findViewById(R.id.info);
        info2 = findViewById(R.id.info2);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid()).child(date);

        final Bundle bundle = new Bundle();
        bundle.putString("date", date);

        info.bringToFront();
        info2.bringToFront();
        fab_full.setClickable(false);

        Fragment startFragment = new HomeFragment();
        startFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, startFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        hideInfoLayout();
                        hideInfoLayout2();
                        bundle.putString("date", date);
                        selectedFragment = new HomeFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.addActivityFragment:
                        hideInfoLayout();
                        hideInfoLayout2();
                        bundle.putString("date", date);
                        selectedFragment = new AddActivityFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                    case R.id.chartsFragment:
                        hideInfoLayout();
                        hideInfoLayout2();
                        bundle.putString("date", date);
                        selectedFragment = new ChartsFragment();
                        selectedFragment.setArguments(bundle);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

                return true;
            }
        });
    }

    public void showInfoLayout() {
        infoLayout.setVisibility(View.VISIBLE);
    }

    public void hideInfoLayout() {
        infoLayout.setVisibility(View.GONE);
    }

    public void showInfoLayout2() {
        infoLayout2.setVisibility(View.VISIBLE);
    }

    public void hideInfoLayout2() {
        infoLayout2.setVisibility(View.GONE);
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public void onBackPressed() {
    }
}
