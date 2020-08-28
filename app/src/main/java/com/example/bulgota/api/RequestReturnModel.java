package com.example.bulgota.api;

public class RequestReturnModel {
    /**
     * {
     *     "latitude": {latitude},
     *     "longitude": {longitude}
     * }
     */

    private double latitude;
    private double longitude;

    public RequestReturnModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
