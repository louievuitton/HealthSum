package com.stevenlouie.healthsum.models;

public class ExerciseModel {

    private String exercise;
    private String image;
    private int caloriesBurned;

    private ExerciseModel(String exercise, String image, int caloriesBurned) {
        this.exercise = exercise;
        this.image = image;
        this.caloriesBurned = caloriesBurned;
    }

    public ExerciseModel() {

    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
}
