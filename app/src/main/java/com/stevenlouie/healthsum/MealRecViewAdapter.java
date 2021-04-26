package com.stevenlouie.healthsum;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MealRecViewAdapter extends RecyclerView.Adapter<MealRecViewAdapter.ViewHolder> {

    private ArrayList<Meal> meals = new ArrayList<>();
    private Context context;
    private FirebaseAuth auth;
    private String date;
    private String mealType;
    private String userId;
    private int totalCalories;
    private int totalCarbs;
    private int totalFat;
    private int totalProtein;
    private int counter;

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
            holder.numServings.setText(meals.get(position).getServings() + " servings,");
        }
        holder.numCalories.setText(meals.get(position).getCalories() + " calories");
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NutritionFactsDialog dialog = new NutritionFactsDialog(meals.get(position));
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Show nutritional facts dialog");
            }
        });
        holder.timestamp.setText("Consumed at: " + meals.get(position).getTimestamp());

        Glide.with(context)
                .load(meals.get(position).getImage())
                .into(holder.itemImage);

//        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                counter = 0;
////                Bundle bundle = new Bundle();
////                bundle.putString("date", date);
////                bundle.putString("id", meals.get(position).getId());
////                bundle.putString("userId", auth.getCurrentUser().getUid());
////                bundle.putString("mealType", mealType);
////                bundle.putInt("calories", meals.get(position).getCalories());
////                bundle.putInt("carbs", meals.get(position).getCarbs());
////                bundle.putInt("fat", meals.get(position).getFat());
////                bundle.putInt("protein", meals.get(position).getProtein());
////                DeleteMealDialog dialog = new DeleteMealDialog();
////                dialog.setArguments(bundle);
////                dialog.show(context.getSupportFragmentManager(), "Delete dialog");
//
//                final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(userId).child(date);
////                database.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        int calories = Integer.valueOf(dataSnapshot.child("setCalories").getValue().toString());
////                        database.child("setCalories").setValue(calories + 100);
////                        int totalCalories = Integer.valueOf(dataSnapshot.child("totalCalories").child(mealType).getValue().toString());
////                        int totalCarbs = Integer.valueOf(dataSnapshot.child("totalCarbs").child(mealType).getValue().toString());
////                        int totalFat = Integer.valueOf(dataSnapshot.child("totalFat").child(mealType).getValue().toString());
////                        int totalProtein = Integer.valueOf(dataSnapshot.child("totalProtein").child(mealType).getValue().toString());
////                        database.child("totalCalories").child(mealType).setValue(totalCalories - meals.get(position).getCalories());
////                        database.child("totalCarbs").child(mealType).setValue(totalCarbs - meals.get(position).getCarbs());
////                        database.child("totalFat").child(mealType).setValue(totalFat - meals.get(position).getFat());
////                        database.child("totalProtein").child(mealType).setValue(totalProtein - meals.get(position).getProtein());
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                    }
////                });
////                database.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        database.child("setCalories").setValue(Integer.valueOf(dataSnapshot.child("setCalories").getValue().toString())+meals.get(position).getCalories());
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                    }
////                });
////                if (meals.size() == 1) {
////                    database.child("totalCalories").child(mealType).getRef().removeValue();
////                    database.child("totalCarbs").child(mealType).getRef().removeValue();
////                    database.child("totalFat").child(mealType).getRef().removeValue();
////                    database.child("totalProtein").child(mealType).getRef().removeValue();
////                }
////                else {
////                    database.addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                            totalCalories = Integer.valueOf(dataSnapshot.child("totalCalories").child(mealType).getValue().toString());
////                            totalCarbs = Integer.valueOf(dataSnapshot.child("totalCarbs").child(mealType).getValue().toString());
////                            totalFat = Integer.valueOf(dataSnapshot.child("totalFat").child(mealType).getValue().toString());
////                            totalProtein = Integer.valueOf(dataSnapshot.child("totalProtein").child(mealType).getValue().toString());
////                        }
////
////                        @Override
////                        public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                        }
////                    });
////                    database.child("totalCalories").child(mealType).setValue(totalCalories - meals.get(position).getCalories());
////                    database.child("totalCarbs").child(mealType).setValue(totalCarbs - meals.get(position).getCarbs());
////                    database.child("totalFat").child(mealType).setValue(totalFat - meals.get(position).getFat());
////                    database.child("totalProtein").child(mealType).setValue(totalProtein - meals.get(position).getProtein());
////                }
//                database.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        counter = 0;
//                        if (counter == 0) {
//                            counter = 1;
//                            database.child("setCalories").setValue(Integer.valueOf(dataSnapshot.child("setCalories").getValue().toString())+meals.get(position).getCalories());
//                            if (meals.size() == 1) {
//                                database.child("totalCalories").child(mealType).getRef().removeValue();
//                                database.child("totalCarbs").child(mealType).getRef().removeValue();
//                                database.child("totalFat").child(mealType).getRef().removeValue();
//                                database.child("totalProtein").child(mealType).getRef().removeValue();
//                            } else {
//                                totalCalories = Integer.valueOf(dataSnapshot.child("totalCalories").child(mealType).getValue().toString());
//                                totalCarbs = Integer.valueOf(dataSnapshot.child("totalCarbs").child(mealType).getValue().toString());
//                                totalFat = Integer.valueOf(dataSnapshot.child("totalFat").child(mealType).getValue().toString());
//                                totalProtein = Integer.valueOf(dataSnapshot.child("totalProtein").child(mealType).getValue().toString());
//                                database.child("totalCalories").child(mealType).setValue(totalCalories - meals.get(position).getCalories());
//                                database.child("totalCarbs").child(mealType).setValue(totalCarbs - meals.get(position).getCarbs());
//                                database.child("totalFat").child(mealType).setValue(totalFat - meals.get(position).getFat());
//                                database.child("totalProtein").child(mealType).setValue(totalProtein - meals.get(position).getProtein());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//                database.child(mealType).child(meals.get(position).getId()).getRef().removeValue();
//                meals.remove(position);
//                notifyDataSetChanged();
////                context.getApplicationContext().getResources().
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parent;
        private TextView mealName, numServings, numCalories, timestamp;
        private ImageView itemImage;
//        private Button deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.cardView);
            mealName = itemView.findViewById(R.id.mealName);
            numServings = itemView.findViewById(R.id.numServings);
            numCalories = itemView.findViewById(R.id.numCalories);
            itemImage = itemView.findViewById(R.id.itemImage);
            timestamp = itemView.findViewById(R.id.timestamp);
//            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
