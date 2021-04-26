package com.stevenlouie.healthsum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ExerciseRecViewAdapter extends RecyclerView.Adapter<ExerciseRecViewAdapter.ViewHolder> {

    private ArrayList<Exercise> exercises = new ArrayList<>();
    private Context context;
    private FirebaseAuth auth;
    private String date;
    private String userId;

    public ExerciseRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_exercise, parent, false);
        ExerciseRecViewAdapter.ViewHolder holder = new ExerciseRecViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exerciseName.setText(exercises.get(position).getExercise());
        holder.caloriesBurned.setText(exercises.get(position).getCaloriesBurned() + " calories burned");
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, meals.get(position).getMeal() + " Selected", Toast.LENGTH_SHORT).show();
            }
        });
        holder.timestamp.setText("Burned at: " + exercises.get(position).getTimestamp());

        Glide.with(context)
                .load("https://static.thenounproject.com/png/118627-200.png")
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parent;
        private TextView exerciseName, caloriesBurned, timestamp;
//        private Button deleteBtn;
        private ImageView itemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.cardView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            caloriesBurned = itemView.findViewById(R.id.caloriesBurned);
            itemImage = itemView.findViewById(R.id.itemImage);
            timestamp = itemView.findViewById(R.id.timestamp);
//            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
