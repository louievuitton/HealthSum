package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stevenlouie.healthsum.models.WeightModel;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChartsFragment extends Fragment {

    private RelativeLayout chartsContent;
    private LineChart lineChart;
    private TextView currentWeightTextView, weightTimestamp;
    private Button setWeightBtn, datepicker, dailyBtn, weeklyBtn;
    private String date;
    private boolean dailyToggled = true;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;
    private ArrayList<WeightModel> weights = new ArrayList<>();
    private ArrayList<WeightModel> averageWeights = new ArrayList<>();

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
        chartsContent = view.findViewById(R.id.chartContent);
        currentWeightTextView = view.findViewById(R.id.currentWeightTextView);
        setWeightBtn = view.findViewById(R.id.setWeightBtn);
        datepicker = view.findViewById(R.id.datepicker);
        lineChart = view.findViewById(R.id.lineChart);
        dailyBtn = view.findViewById(R.id.dailyBtn);
        weeklyBtn = view.findViewById(R.id.weeklyBtn);
        weightTimestamp = view.findViewById(R.id.weightTimestamp);

        calendar = Calendar.getInstance();
        selectedYear = Integer.valueOf(date.substring(0, 4));
        selectedMonth = Integer.valueOf(date.substring(5, 7)) - 1;
        selectedDayOfMonth = Integer.valueOf(date.substring(8));
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);

        final String[] daysArray = getActivity().getResources().getStringArray(R.array.dayOfWeek);

        final SimpleDateFormat timeStamp = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
            datepicker.setText("Today");
        }
        else {
            String dom = date.substring(8);
            if (dom.charAt(0) == '0') {
                dom = dom.substring(1);
            }
            datepicker.setText(daysArray[calendar.get(Calendar.DAY_OF_WEEK)-1] + ", " + (new DateFormatSymbols().getMonths()[Integer.valueOf(date.substring(5, 7))-1]).substring(0, 3) + " " + dom);
        }

        fetchData();

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
                        ((MainActivity) getActivity()).setDate(date);
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            datepicker.setText(daysArray[calendar.get(Calendar.DAY_OF_WEEK)-1] + ", " + month_date.format(calendar.getTime()) + " " + dayOfMonth);
                        }

                        fetchData();
                    }
                }, selectedYear, selectedMonth, selectedDayOfMonth);
                datePickerDialog.show();
            }
        });

        setWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hour = calendar.getTime().toString().substring(11, 13);
                String minutes = calendar.getTime().toString().substring(14, 16);

                Date date = null;
                try {
                    date = new SimpleDateFormat("hhmm").parse(String.format("%04d", Integer.valueOf(hour+minutes+"")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                openDialog(sdf.format(date));
            }
        });

        dailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dailyToggled) {
                    dailyToggled = true;
                    dailyBtn.setBackgroundResource(R.color.toggleBtn);
                    weeklyBtn.setBackgroundResource(R.color.cancelBtn);
                    fetchData();
                }
            }
        });

        weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (dailyToggled) {
                    dailyToggled = false;
                    weeklyBtn.setBackgroundResource(R.color.toggleBtn);
                    dailyBtn.setBackgroundResource(R.color.cancelBtn);
                    fetchAverageWeights();
                }
            }
        });

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        return view;
    }

    private void fetchData() {
        lineChart.invalidate();
        lineChart.clear();
        weights.clear();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(date)) {
                    for (DataSnapshot snapshot: dataSnapshot.child(date).getChildren()) {
                        WeightModel model = new WeightModel();
                        model.setTimestamp(snapshot.child("timestamp").getValue().toString());
                        model.setWeight(Integer.valueOf(snapshot.child("weight").getValue().toString()));
                        weights.add(model);
                    }

                    weightTimestamp.setText("Last weighed: " + weights.get(weights.size()-1).getTimestamp());
                    currentWeightTextView.setText("Your weight: " + Math.round(weights.get(weights.size()-1).getWeight()) +" lbs");
                    ArrayList<Entry> values = new ArrayList<>();
                    for (int i = 0; i < weights.size(); i++) {
                        values.add(new Entry(i, weights.get(i).getWeight()));
                    }

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setValueFormatter(new XAxisValueFormatter(weights));

                    LineDataSet set = new LineDataSet(values, "Data Set");
                    set.setFillAlpha(110);
                    set.setLineWidth(3f);
                    set.setColor(Color.GREEN);
                    set.setValueTextSize(20f);
                    set.setValueTextColor(Color.RED);
                    ArrayList<ILineDataSet> dataset = new ArrayList<>();
                    dataset.add(set);
                    lineChart.getAxisRight().setEnabled(false);
                    lineChart.getLegend().setEnabled(false);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.setData(new LineData(dataset));
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                    weightTimestamp.setVisibility(View.VISIBLE);
                    chartsContent.setVisibility(View.VISIBLE);
                }
                else {
                    chartsContent.setVisibility(View.GONE);
                    weightTimestamp.setVisibility(View.GONE);
                    lineChart.invalidate();
                    lineChart.clear();
                    currentWeightTextView.setText("Please set your weight");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void test() {
        final SimpleDateFormat timeStamp = new SimpleDateFormat("MM-dd-yyyy");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchAverageWeights() {
        lineChart.invalidate();
        lineChart.clear();
        averageWeights.clear();

        LocalDate start = LocalDate.parse(date);
        final ArrayList<String> dates = new ArrayList<>();
        int counter = 0;
        while (counter < 8) {
            dates.add(0, start.toString());
            start = start.minusDays(1);
            counter++;
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("weight").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (int i = 0; i < dates.size(); i++) {
                    WeightModel model = new WeightModel();
                    model.setTimestamp(dates.get(i));
                    if (dataSnapshot.hasChild(dates.get(i))) {
                        ArrayList<Integer> wt = new ArrayList<>();
                        for (DataSnapshot snapshot: dataSnapshot.child(date).getChildren()) {
                            wt.add(Integer.valueOf(snapshot.child("weight").getValue().toString()));
                        }

                        int sum = 0;
                        for (Integer weight: wt) {
                            sum += weight;
                        }
                        model.setWeight(sum/wt.size());
                        found = true;
                    }
                    else {
                        model.setWeight(0);
                    }
                    averageWeights.add(model);
                }

                if (found) {
                    ArrayList<Entry> values = new ArrayList<>();
                    for (int i = 0; i < averageWeights.size(); i++) {
                        values.add(new Entry(i, averageWeights.get(i).getWeight()));
                    }

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setValueFormatter(new XAxisValueFormatter(averageWeights));

                    LineDataSet set = new LineDataSet(values, "Data Set");
                    set.setFillAlpha(110);
                    set.setLineWidth(3f);
                    set.setColor(Color.GREEN);
                    set.setValueTextSize(20f);
                    set.setValueTextColor(Color.RED);
                    ArrayList<ILineDataSet> dataset = new ArrayList<>();
                    dataset.add(set);
                    lineChart.getAxisRight().setEnabled(false);
                    lineChart.getLegend().setEnabled(false);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.setData(new LineData(dataset));
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
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

    private void openDialog(String time) {
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        SetWeightDialog dialog = new SetWeightDialog(time);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "Set Weight Dialog");
    }

    private class XAxisValueFormatter extends IndexAxisValueFormatter {

        private ArrayList<WeightModel> weights;

        public XAxisValueFormatter(ArrayList<WeightModel> weights) {
            this.weights = weights;
        }

        @Override
        public String getFormattedValue(float value) {
            return weights.get((int) value).getTimestamp()+"";
        }
    }
}