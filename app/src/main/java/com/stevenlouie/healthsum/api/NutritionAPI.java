package com.stevenlouie.healthsum.api;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stevenlouie.healthsum.models.NutritionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NutritionAPI {

    private Context context;
    private int totalCalories = 0;
    private int totalCarbs = 0;
    private int totalFat = 0;
    private int totalProtein = 0;
    private int totalCaloriesBurned = 0;
    private String gender = "";
    private double weight = 0;
    private String height = "";
    private int age = 0;

    public NutritionAPI(Context context) {
        this.context = context;
    }

    public void fetchNutritionData(final String date, final String mealType, String foodName, final int numServings) {
        String url = "https://trackapi.nutritionix.com/v2/natural/nutrients";
        JSONObject obj = new JSONObject();
        try {
            obj.put("query", foodName);
            obj.put("num_servings", numServings);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<NutritionModel> models = new ArrayList<>();
                    JSONArray foods = response.getJSONArray("foods");
                    for (int i = 0; i < foods.length(); i++) {
                        JSONObject obj = (JSONObject) foods.get(i);
                        final NutritionModel model = new NutritionModel();
                        model.setFoodName(obj.getString("food_name"));
                        model.setServingQty(obj.getInt("serving_qty"));
                        model.setCalories((int) Math.round(obj.getDouble("nf_calories")));
                        model.setFat((int) Math.round(obj.getDouble("nf_total_fat")));
                        model.setCarbs((int) Math.round(obj.getDouble("nf_total_carbohydrate")));
                        model.setProtein((int) Math.round(obj.getDouble("nf_protein")));
                        models.add(model);
                    }

                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(date);
                    final String key = database.push().getKey();
                    final HashMap<String, Object> map = new HashMap<>();
                    map.put("id", key);
                    map.put("meal", "");
                    map.put("servings", numServings);
                    map.put("calories", 0);
                    map.put("fat", 0);
                    map.put("carbs", 0);
                    map.put("protein", 0);

                    for (int i = 0; i < models.size(); i++) {
                        map.put("meal", map.get("meal").toString() + " and " + models.get(i).getFoodName());
                        map.put("calories", ((int) map.get("calories")) + models.get(i).getCalories());
                        map.put("fat", ((int) map.get("fat")) + models.get(i).getFat());
                        map.put("carbs", ((int) map.get("carbs")) + models.get(i).getCarbs());
                        map.put("protein", ((int) map.get("protein")) + models.get(i).getProtein());
                    }

                    map.put("meal", map.get("meal").toString().substring(5));

                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int caloriesLeft = Integer.valueOf(dataSnapshot.child("caloriesLeft").getValue().toString());
                            database.child("caloriesLeft").setValue(caloriesLeft-((int)map.get("calories")));
                            if (dataSnapshot.hasChild("totalCalories")) {
                                if (dataSnapshot.child("totalCalories").hasChild(mealType)) {
                                    totalCalories = Integer.valueOf(dataSnapshot.child("totalCalories").child(mealType).getValue().toString());
                                }

                                if (dataSnapshot.child("totalCarbs").hasChild(mealType)) {
                                    totalCarbs = Integer.valueOf(dataSnapshot.child("totalCarbs").child(mealType).getValue().toString());
                                }

                                if (dataSnapshot.child("totalFat").hasChild(mealType)) {
                                    totalFat = Integer.valueOf(dataSnapshot.child("totalFat").child(mealType).getValue().toString());
                                }

                                if (dataSnapshot.child("totalProtein").hasChild(mealType)) {
                                    totalProtein = Integer.valueOf(dataSnapshot.child("totalProtein").child(mealType).getValue().toString());
                                }
                            }
                            database.child("totalCalories").child(mealType).setValue(totalCalories+((int) map.get("calories")));
                            database.child("totalCarbs").child(mealType).setValue(totalCarbs+((int) map.get("carbs")));
                            database.child("totalFat").child(mealType).setValue(totalFat+((int) map.get("fat")));
                            database.child("totalProtein").child(mealType).setValue(totalProtein+((int) map.get("protein")));
                            database.child(mealType).child(key).updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//                    database.child("totalCalories").child(mealType).setValue(totalCalories+((int) map.get("calories")));
//                    database.child("totalCarbs").child(mealType).setValue(totalCarbs+((int) map.get("carbs")));
//                    database.child("totalFat").child(mealType).setValue(totalFat+((int) map.get("fat")));
//                    database.child("totalProtein").child(mealType).setValue(totalProtein+((int) map.get("protein")));
//                    database.child(mealType).child(key).updateChildren(map);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Please enter a valid food name", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-app-id", "9787a77e");
                params.put("x-app-key", "40785cdacf0ad9c162ae95eb2f83aa9d");
                params.put("x-remote-user-id", "0");
                return params;
            }
        };

        queue.add(request);
    }

    public void fetchExerciseData(final String date, final String exercise) {
        // fetch user information
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gender = dataSnapshot.child("gender").getValue().toString();
                age = Integer.valueOf(dataSnapshot.child("age").getValue().toString());
//                weight = Double.valueOf(dataSnapshot.child("weight").getValue().toString());
                height = dataSnapshot.child("height").getValue().toString();

//                double wt = weight*(0.453592);
                final double ht = (Double.valueOf(height.substring(0,1))*(30.48)) + (Double.valueOf(height.substring(2))*(2.54));

                final ArrayList<Double> wt = new ArrayList<>();
                db.child("weight").child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            wt.add(Double.valueOf(snapshot.getValue().toString()));
                        }
//                        Toast.makeText(context, "weight: " + wt.get(wt.size()-1), Toast.LENGTH_SHORT).show();
                        addExercise(date, exercise, gender, age, Math.round(wt.get(wt.size()-1)*(0.453592)), Math.round(ht));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
                // fetch exercise activity from api and add to firebase
//                addExercise(date, exercise, gender, age, Math.round(wt), Math.round(ht));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        String url = "https://trackapi.nutritionix.com/v2/natural/exercise";
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("query", exercise);
//            obj.put("gender", "male");
//            obj.put("weight_kg", 72.5);
//            obj.put("height_cm", 167.64);
//            obj.put("age", 30);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray exercises = response.getJSONArray("exercises");
//                    for (int i = 0; i < exercises.length(); i++) {
//                        totalCaloriesBurned = ((JSONObject) exercises.get(i)).getInt("nf_calories");
//                    }
//
//                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(date);
//                    database.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            int caloriesBurned = Integer.valueOf(dataSnapshot.child("caloriesBurned").getValue().toString());
//                            database.child("caloriesBurned").setValue(caloriesBurned + totalCaloriesBurned);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                    String key = database.push().getKey();
//                    final HashMap<String, Object> map = new HashMap<>();
//                    map.put("id", key);
//                    map.put("exercise", exercise);
//                    map.put("caloriesBurned", totalCaloriesBurned);
//
//                    database.child("exercises").child(key).updateChildren(map);
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "Please enter a valid exercise", Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("x-app-id", "9787a77e");
//                params.put("x-app-key", "40785cdacf0ad9c162ae95eb2f83aa9d");
//                return params;
//            }
//        };
//
//        queue.add(request);
    }

    private void addExercise(final String date, final String exercise, String g, int a, double w, double h) {
        String url = "https://trackapi.nutritionix.com/v2/natural/exercise";
        JSONObject obj = new JSONObject();
        try {
            obj.put("query", exercise);
            obj.put("gender", g);
            obj.put("weight_kg", w);
            obj.put("height_cm", h);
            obj.put("age", a);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray exercises = response.getJSONArray("exercises");
                    for (int i = 0; i < exercises.length(); i++) {
                        totalCaloriesBurned = ((JSONObject) exercises.get(i)).getInt("nf_calories");
                    }

                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("DailyActivity").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(date);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int caloriesBurned = Integer.valueOf(dataSnapshot.child("caloriesBurned").getValue().toString());
                            database.child("caloriesBurned").setValue(caloriesBurned + totalCaloriesBurned);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    String key = database.push().getKey();
                    final HashMap<String, Object> map = new HashMap<>();
                    map.put("id", key);
                    map.put("exercise", exercise);
                    map.put("caloriesBurned", totalCaloriesBurned);

                    database.child("exercises").child(key).updateChildren(map);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Please enter a valid exercise", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-app-id", "9787a77e");
                params.put("x-app-key", "40785cdacf0ad9c162ae95eb2f83aa9d");
                return params;
            }
        };

        queue.add(request);
    }
}
