package com.example.bulgota.api;

public class ResponseLendModel {
    private final int status;
    private final boolean success;
    private final String message;
    private final LendData data;

    public ResponseLendModel(int status, boolean success, String message, LendData data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getStatus() { return status; }

    public boolean getSuccess() { return success; }

    public String getMessage() { return message; }

    public LendData getData() { return data; }

}
