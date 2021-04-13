package com.stevenlouie.healthsum;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stevenlouie.healthsum.api.NutritionAPI;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddActivityFragment extends Fragment {

    private RelativeLayout addMealLayout, addExerciseLayout;
    private TextView foodWarning, exerciseWarning;
    private EditText mealEditText, exerciseEditText;
    private Button addBtn, datepicker;
    private RadioGroup radioGroup, selectActivityRG;
    private Spinner num_servings_spinner;
    private String mealType = "breakfast";
    private String date;
    private String selectedActivity = "meal";
    private int numServings = 1;
    private DatePickerDialog datePickerDialog;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;
    private Calendar calendar;
    private String parentActivity;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private boolean flag;

    public AddActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_activity, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
            parentActivity = bundle.getString("activity");
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(auth.getCurrentUser().getUid());

        addMealLayout = view.findViewById(R.id.addMealLayout);
        addExerciseLayout = view.findViewById(R.id.addExerciseLayout);
        exerciseWarning = view.findViewById(R.id.exerciseWarning);
        exerciseEditText = view.findViewById(R.id.exerciseEditText);
        foodWarning = view.findViewById(R.id.foodWarning);
        mealEditText = view.findViewById(R.id.mealEditText);
        addBtn = view.findViewById(R.id.addActivityBtn);
        datepicker = view.findViewById(R.id.datepicker);
        radioGroup = view.findViewById(R.id.radioGroup);
        selectActivityRG = view.findViewById(R.id.selectActivityRG);
        num_servings_spinner = view.findViewById(R.id.num_servings_spinner);

        calendar = Calendar.getInstance();
        selectedYear = Integer.valueOf(date.substring(6));
        selectedMonth = Integer.valueOf(date.substring(0, 2)) - 1;
        selectedDayOfMonth = Integer.valueOf(date.substring(3, 5));
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);

        final String[] daysArray = getActivity().getResources().getStringArray(R.array.dayOfWeek);

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
            datepicker.setText(daysArray[calendar.get(Calendar.DAY_OF_WEEK)-1] + ", " + (new DateFormatSymbols().getMonths()[Integer.valueOf(date.substring(0, 2))-1]).substring(0, 3) + " " + dom);
//            datepicker.setText((new DateFormatSymbols().getMonths()[Integer.valueOf(date.substring(0, 2))-1]).substring(0, 3) + ", " + dom);
        }

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
                        if (parentActivity.equals("main")) {
                            ((MainActivity) getActivity()).setDate(date);
                        }
                        else if (parentActivity.equals("breakfast")) {
                            ((BreakfastActivity) getActivity()).setDate(date);
                        }
                        if (timeStamp.format(Calendar.getInstance().getTime()).equals(date)) {
                            datepicker.setText("Today");
                        }
                        else {
                            datepicker.setText(daysArray[calendar.get(Calendar.DAY_OF_WEEK)-1] + ", " + month_date.format(calendar.getTime()) + " " + dayOfMonth);
//                            datepicker.setText(month_date.format(calendar.getTime()) + ", " + dayOfMonth);
                        }

                        foodWarning.setVisibility(View.GONE);
                        exerciseWarning.setVisibility(View.GONE);
                        mealEditText.getText().clear();
                        mealEditText.clearFocus();
                        exerciseEditText.getText().clear();
                        exerciseEditText.clearFocus();
                    }
                }, selectedYear, selectedMonth, selectedDayOfMonth);
                datePickerDialog.show();
            }
        });

        selectActivityRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                switch (checkedId) {
                    case R.id.mealRB:
                        selectedActivity = "meal";
                        addExerciseLayout.setVisibility(View.GONE);
                        mealEditText.getText().clear();
                        foodWarning.setVisibility(View.GONE);
                        addMealLayout.setVisibility(View.VISIBLE);
                        imm.hideSoftInputFromWindow(mealEditText.getWindowToken(), 0);
                        break;
                    case R.id.exerciseRB:
                        selectedActivity = "exercise";
                        addMealLayout.setVisibility(View.GONE);
                        exerciseEditText.getText().clear();
                        exerciseWarning.setVisibility(View.GONE);
                        addExerciseLayout.setVisibility(View.VISIBLE);
                        imm.hideSoftInputFromWindow(exerciseEditText.getWindowToken(), 0);
                        break;
                    default:
                        break;
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.breakfastRB:
                        mealType = "breakfast";
                        break;
                    case R.id.lunchRB:
                        mealType = "lunch";
                        break;
                    case R.id.dinnerRB:
                        mealType = "dinner";
                        break;
                    default:
                        break;
                }
            }
        });

        final Integer[] servings_array = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, servings_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        num_servings_spinner.setAdapter(adapter);
        num_servings_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numServings = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // api to get nutrition details
                if (validateInput()) {
                    foodWarning.setVisibility(View.GONE);
                    exerciseWarning.setVisibility(View.GONE);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(date)) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                NutritionAPI api = new NutritionAPI(getActivity());
                                if (selectedActivity.equals("meal")) {
                                    api.fetchNutritionData(date, mealType, mealEditText.getText().toString(), numServings);
                                    mealEditText.getText().clear();
                                    mealEditText.clearFocus();
                                    imm.hideSoftInputFromWindow(mealEditText.getWindowToken(), 0);
                                } else if (selectedActivity.equals("exercise")) {
                                    api.fetchExerciseData(date, exerciseEditText.getText().toString());
                                    exerciseEditText.getText().clear();
                                    exerciseEditText.clearFocus();
                                    imm.hideSoftInputFromWindow(exerciseEditText.getWindowToken(), 0);
                                }

                                Toast.makeText(getActivity(), "Successfully added activity", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), "Please set your calorie goal before adding an activity", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    NutritionAPI api = new NutritionAPI(getActivity());
//                    if (selectedActivity.equals("meal")) {
////                        imm.hideSoftInputFromWindow(mealEditText.getWindowToken(), 0);
//                        api.fetchNutritionData(date, mealType, mealEditText.getText().toString(), numServings);
//                        mealEditText.getText().clear();
//                        mealEditText.clearFocus();
//                    } else if (selectedActivity.equals("exercise")) {
////                        imm.hideSoftInputFromWindow(exerciseEditText.getWindowToken(), 0);
//                        api.fetchExerciseData(date, exerciseEditText.getText().toString());
//                        exerciseEditText.getText().clear();
//                        exerciseEditText.clearFocus();
//                    }
//                    Toast.makeText(getActivity(), "Successfully added activity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validateInput() {
        boolean valid = true;

        if (selectedActivity.equals("meal")) {
            if (mealEditText.getText().toString().equals("")) {
                foodWarning.setText("Field cannot be blank");
                foodWarning.setVisibility(View.VISIBLE);
                valid = false;
            }
        }
        else if (selectedActivity.equals("exercise")) {
            if (exerciseEditText.getText().toString().equals("")) {
                exerciseWarning.setText("Field cannot be blank");
                exerciseWarning.setVisibility(View.VISIBLE);
                valid = false;
            }
        }

        if (!valid) {
            return false;
        }
        else {
            return true;
        }
    }

    private boolean goalsSet() {
        flag = false;
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(date)) {
                    flag = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return flag;
    }
}