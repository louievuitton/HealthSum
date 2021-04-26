package com.stevenlouie.healthsum;

public class Meal {

    private int calories;
    private int carbs;
    private int cholesterol;
    private int dietaryFiber;
    private int fat;
    private String id;
    private String image;
    private String meal;
    private int protein;
    private int saturatedFat;
    private int servings;
    private int sodium;
    private int sugars;
    private String timestamp;

    public Meal() {

    }

    public Meal(int calories, int carbs, int cholesterol, int dietaryFiber, int fat, String id, String image, String meal, int protein, int saturatedFat, int servings, int sodium, int sugars, String timestamp) {
        this.calories = calories;
        this.carbs = carbs;
        this.cholesterol = cholesterol;
        this.dietaryFiber = dietaryFiber;
        this.fat = fat;
        this.id = id;
        this.image = image;
        this.meal = meal;
        this.protein = protein;
        this.saturatedFat = saturatedFat;
        this.servings = servings;
        this.sodium = sodium;
        this.sugars = sugars;
        this.timestamp = timestamp;
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

    public int getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(int cholesterol) {
        this.cholesterol = cholesterol;
    }

    public int getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(int dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public int getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(int saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public int getSugars() {
        return sugars;
    }

    public void setSugars(int sugars) {
        this.sugars = sugars;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
