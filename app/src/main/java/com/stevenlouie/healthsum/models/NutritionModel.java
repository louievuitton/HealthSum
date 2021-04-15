package com.stevenlouie.healthsum.models;

public class NutritionModel {
    private String foodName;
    private int servingQty;
    private int calories;
    private int fat;
    private int carbs;
    private int protein;
    private String image;

    public NutritionModel(String foodName, int servingQty, int calories, int fat, int carbs, int protein, String image) {
        this.foodName = foodName;
        this.servingQty = servingQty;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.image = image;
    }

    public NutritionModel() {

    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getServingQty() {
        return servingQty;
    }

    public void setServingQty(int servingQty) {
        this.servingQty = servingQty;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "NutritionModel{" +
                "foodName='" + foodName + '\'' +
                ", servingQty=" + servingQty +
                ", calories=" + calories +
                ", fat=" + fat +
                ", carbs=" + carbs +
                ", protein=" + protein +
                '}';
    }
}

