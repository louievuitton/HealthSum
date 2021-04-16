package com.stevenlouie.healthsum.models;

public class NutritionModel {
    private String foodName;
    private int servingQty;
    private int calories;
    private int fat;
    private int carbs;
    private int protein;
    private String image;
    private int saturatedFat;
    private int cholesterol;
    private int sodium;
    private int dietaryFiber;
    private int sugars;

    public NutritionModel(String foodName, int servingQty, int calories, int fat, int carbs, int protein, String image, int saturatedFat, int cholesterol, int sodium, int dietaryFiber, int sugars) {
        this.foodName = foodName;
        this.servingQty = servingQty;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.image = image;
        this.saturatedFat = saturatedFat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.dietaryFiber = dietaryFiber;
        this.sugars = sugars;
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

    public int getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(int saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public int getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(int cholesterol) {
        this.cholesterol = cholesterol;
    }

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public int getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(int dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public int getSugars() {
        return sugars;
    }

    public void setSugars(int sugars) {
        this.sugars = sugars;
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
                ", image='" + image + '\'' +
                ", saturatedFat=" + saturatedFat +
                ", cholesterol=" + cholesterol +
                ", sodium=" + sodium +
                ", dietaryFiber=" + dietaryFiber +
                ", sugars=" + sugars +
                '}';
    }
}

