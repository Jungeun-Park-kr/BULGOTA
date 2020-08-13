package com.example.bulgota.api;

import java.util.ArrayList;

public class ResponseWithMarkerData {
    private final int status;
    private final boolean success;
    private final String message;
    private final ArrayList<Marker_list> data;

    public ResponseWithMarkerData(int status, boolean success, String message, ArrayList<Marker_list> data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { return status; }

    public boolean getSuccess() { return success; }

    public String getMessage() { return message; }

    public ArrayList<Marker_list> getData() { return data; }
}
