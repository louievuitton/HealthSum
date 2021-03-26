package com.stevenlouie.healthsum;

public class Exercise {

    private int caloriesBurned;
    private String exercise;
    private String id;

    public Exercise(int caloriesBurned, String exercise, String id) {
        this.caloriesBurned = caloriesBurned;
        this.exercise = exercise;
        this.id = id;
    }

    public Exercise() {}

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
