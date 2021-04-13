package com.stevenlouie.healthsum.models;

public class WeightModel {

    private String timestamp;
    private int weight;

    private WeightModel(String timestamp, int weight) {
        this.timestamp = timestamp;
        this.weight = weight;
    }

    public WeightModel() {

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "WeightModel{" +
                "timestamp='" + timestamp + '\'' +
                ", weight=" + weight +
                '}';
    }
}
