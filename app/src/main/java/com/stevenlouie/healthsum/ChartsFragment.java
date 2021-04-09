package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChartsFragment extends Fragment {

    private TextView currentWeightTextView;
    private Button setWeightBtn, datepicker;
    private String date;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;

    public ChartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts, container, false);
        currentWeightTextView = view.findViewById(R.id.currentWeightTextView);
        setWeightBtn = view.findViewById(R.id.setWeightBtn);
        datepicker = view.findViewById(R.id.datepicker);

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

        calendar = Calendar.getInstance();
        selectedYear = Integer.valueOf(date.substring(6));
        selectedMonth = Integer.valueOf(date.substring(0, 2)) - 1;
        selectedDayOfMonth = Integer.valueOf(date.substring(3, 5));

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

                        date = timeStamp.format(calendar.getTime());
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            datepicker.setText(month_date.format(calendar.getTime()) + ", " + dayOfMonth);
                        }

                        fetchData();
                    }
                }, selectedYear, selectedMonth, selectedDayOfMonth);
                datePickerDialog.show();
            }
        });

        fetchData();

        setWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        return view;
    }

    private void fetchData() {
        final ArrayList<Double> weights = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(date)) {
                    for (DataSnapshot snapshot: dataSnapshot.child(date).getChildren()) {
                        weights.add(Double.valueOf(snapshot.getValue().toString()));
                    }

                    currentWeightTextView.setText(""+Math.round(weights.get(weights.size()-1)));
                }
                else {
                    currentWeightTextView.setText("Weight has not been set for this date");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        SetWeightDialog dialog = new SetWeightDialog();
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "Set Weight Dialog");
    }
}