package com.stevenlouie.healthsum;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SetWeightDialog extends AppCompatDialogFragment {

    private TextView weightWarning;
    private EditText weightEditText;
    private Button setWeightBtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String date;
    private String time;

    public SetWeightDialog(String time) {
        this.time = time;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.set_weight_dialog, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
        }

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        weightEditText = view.findViewById(R.id.weightEditText);
        weightWarning = view.findViewById(R.id.weightWarning);
        setWeightBtn = view.findViewById(R.id.setWeightBtn);

        setWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    weightWarning.setVisibility(View.INVISIBLE);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("weight", Integer.valueOf(weightEditText.getText().toString()));
                    map.put("timestamp", time);

                    database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("weight").child(date).child(database.getReference().child("Users").push().getKey()).updateChildren(map);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(weightEditText.getWindowToken(), 0);

                    Toast.makeText(getActivity(), "Successfully set weight", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        return builder.create();
    }

    private boolean validate() {
        boolean valid = true;
        if (weightEditText.getText().toString().equals("")) {
            weightWarning.setVisibility(View.VISIBLE);
            valid = false;
        }
        else {
            weightWarning.setVisibility(View.INVISIBLE);
        }

        if (valid) {
            String regex = "^[0-9]+$";

            if (!Pattern.compile(regex).matcher(weightEditText.getText().toString()).matches() && !weightEditText.getText().toString().equals("")) {
                weightWarning.setVisibility(View.VISIBLE);
                valid = false;
            } else {
                weightWarning.setVisibility(View.INVISIBLE);
            }
        }

        if (!valid) {
            return false;
        }
        else {
            return true;
        }
    }
}
