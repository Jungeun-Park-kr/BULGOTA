package com.example.bulgota.api;

import java.util.ArrayList;

public class ResponseReturnModel {
    private final int status;
    private final boolean success;
    private final String message;
    private final int data;

    public ResponseReturnModel(int status, boolean success, String message, int data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { return status; }

    public boolean getSuccess() { return success; }

    public String getMessage() { return message; }

    public int getData() { return data; }

}
