package com.example.bulgota.api;

public class Marker_list {
    private final int id;
    private final String modelNum;
    private final int battery;
    private final String time;
    private final Double latitude;
    private final Double longitude;

    public Marker_list(int id, String modelNum, int battery, String time, Double latitude, Double longitude) {
        this.id = id;
        this.modelNum = modelNum;
        this.battery = battery;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }

    public String getModelNum() {
        return modelNum;
    }

    public int getBattery() {
        return battery;
    }

    public String getTime() {
        return time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
