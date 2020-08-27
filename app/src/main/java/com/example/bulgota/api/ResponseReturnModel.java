package com.example.bulgota.api;

import java.util.ArrayList;

public class ResponseReturnModel {
    private final int status;
    private final boolean success;
    private final String message;
    private final Object data;

    public ResponseReturnModel(int status, boolean success, String message, Object data) {
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
