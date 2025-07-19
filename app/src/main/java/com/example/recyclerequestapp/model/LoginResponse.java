package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("user_id")
    private int userId;

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }
}
