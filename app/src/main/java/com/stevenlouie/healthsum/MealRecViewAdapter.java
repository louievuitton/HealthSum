package com.stevenlouie.healthsum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealRecViewAdapter extends RecyclerView.Adapter<MealRecViewAdapter.ViewHolder> {

    private ArrayList<Meal> meals = new ArrayList<>();
    private Context context;

    public MealRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_meal, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mealName.setText(meals.get(position).getMeal());
        if (meals.get(position).getServings() == 1) {
            holder.numServings.setText(meals.get(position).getServings() + " serving,");
        }
        else {
            holder.numServings.setText(meals.get(position).getServings() + " servings");
        }
        holder.numCalories.setText(meals.get(position).getCalories() + " calories");
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, meals.get(position).getMeal() + " Selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parent;
        private TextView mealName, numServings, numCalories;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.cardView);
            mealName = itemView.findViewById(R.id.mealName);
            numServings = itemView.findViewById(R.id.numServings);
            numCalories = itemView.findViewById(R.id.numCalories);
        }
    }
}
