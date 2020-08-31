package com.example.bulgota.api;

public class LendData {
    private String lendTime;
    private String password;

    public LendData(String lendTime, String password) {
        this.lendTime = lendTime;
        this.password = password;
    }

    public String getLendTime() {
        return lendTime;
    }

    public String getPassword() {
        return password;
    }
}
