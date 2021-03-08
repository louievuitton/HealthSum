package com.stevenlouie.healthsum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DinnerActivity extends AppCompatActivity {

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

        dinnerRecView.setAdapter(adapter);
        dinnerRecView.setLayoutManager(new LinearLayoutManager(this));

        final ArrayList<Meal> list = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child(date);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("totalCalories")) {
                    database.child("totalCalories").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("dinner")) {
                                caloriesConsumed.setText(dataSnapshot.child("dinner").getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("dinner")) {
                    database.child("dinner").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list.clear();
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                list.add(snapshot.getValue(Meal.class));
                            }
                            if (list.size() != 0) {
                                listTextView.setText("What you had");
                                dinnerRecView.setVisibility(View.VISIBLE);
                                adapter.setMeals(list);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}