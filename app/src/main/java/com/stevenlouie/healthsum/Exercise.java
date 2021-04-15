package com.stevenlouie.healthsum;

public class Exercise {

    private int caloriesBurned;
    private String exercise;
    private String id;
    private String image;

    public Exercise(int caloriesBurned, String exercise, String id, String image) {
        this.caloriesBurned = caloriesBurned;
        this.exercise = exercise;
        this.id = id;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
