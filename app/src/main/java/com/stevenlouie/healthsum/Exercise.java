package com.stevenlouie.healthsum;

public class Exercise {

    private int caloriesBurned;
    private String exercise;
    private String id;
    private String image;
    private String timestamp;

    public Exercise(int caloriesBurned, String exercise, String id, String image, String timestamp) {
        this.caloriesBurned = caloriesBurned;
        this.exercise = exercise;
        this.id = id;
        this.image = image;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
