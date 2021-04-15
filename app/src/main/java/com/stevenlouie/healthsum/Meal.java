package com.stevenlouie.healthsum;

public class Meal {

    private int calories;
    private int carbs;
    private int fat;
    private String id;
    private String image;
    private String meal;
    private int protein;
    private int servings;

    public Meal() {

    }

    public Meal(int calories, int carbs, int fat, String id, String image, String meal, int protein, int servings) {
        this.calories = calories;
        this.carbs = carbs;
        this.fat = fat;
        this.id = id;
        this.image = image;
        this.meal = meal;
        this.protein = protein;
        this.servings = servings;
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

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }
}
