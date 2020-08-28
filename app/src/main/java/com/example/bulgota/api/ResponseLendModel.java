package com.example.bulgota.api;

public class ResponseLendModel {
    private final int status;
    private final boolean success;
    private final String message;
    private final Object data;

    public ResponseLendModel(int status, boolean success, String message, Object data) {
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
