package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BreakfastFragment extends Fragment {

    private RelativeLayout statisticsLayout;
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

    public BreakfastFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
            breakfastSet = bundle.getBoolean("breakfastSet", false);
        }

        View view = inflater.inflate(R.layout.fragment_breakfast, container, false);

        statisticsLayout = view.findViewById(R.id.statisticsLayout);
        listTextView = view.findViewById(R.id.listTextView);
        textView3 = view.findViewById(R.id.textView3);
        caloriesProgressBar = view.findViewById(R.id.caloriesProgressBar);
        caloriesConsumed = view.findViewById(R.id.caloriesConsumed);
        adapter = new MealRecViewAdapter(getActivity());
        breakfastRecView = view.findViewById(R.id.breakfastRecView);
        datepicker = view.findViewById(R.id.datepicker);

        calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

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

        return view;
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
            breakfastRecView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
}
