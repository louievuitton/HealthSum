package com.stevenlouie.healthsum;

public class Meal {

    private String type;
    private String name;
    private int servings;

    public Meal(String type, String name, int servings) {
        this.type = type;
        this.name = name;
        this.servings = servings;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }
}
