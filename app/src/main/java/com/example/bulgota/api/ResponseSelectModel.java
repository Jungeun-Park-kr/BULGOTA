package com.example.bulgota.api;

public class ResponseSelectModel {
    private final int status;
    private final boolean success;
    private final String message;
    private final boolean data;

    public ResponseSelectModel(int status, boolean success, String message, boolean data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { return status; }

    public boolean getSuccess() { return success; }

    public String getMessage() { return message; }

    public boolean getData() { return data; }

}
