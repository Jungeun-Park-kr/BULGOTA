package com.example.bulgota.api;

public class ResponseSelectModel {
    private final int status;
    private final boolean success;
    private final String message;
    private final Object data;

    public ResponseSelectModel(int status, boolean success, String message, Object data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { return status; }

    public boolean getSuccess() { return success; }

    public String getMessage() { return message; }

    public Object getData() { return data; }

}
