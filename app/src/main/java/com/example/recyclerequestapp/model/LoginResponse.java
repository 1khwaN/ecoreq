package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;
    @SerializedName("id") // Assuming your backend uses "id" for user ID in login response
    private int userId; // Changed to userId for clarity in client-side code
    @SerializedName("role")
    private String role;
    @SerializedName("username") // Add if your login response includes username
    private String username;
    @SerializedName("email")    // Add if your login response includes email
    private String email;

    // Getters
    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() { // Getter for username
        return username;
    }

    public String getEmail() { // Getter for email
        return email;
    }

    // You might need setters if you're constructing this object manually in some cases,
    // but for Retrofit, only getters are strictly necessary if it's just deserializing.
}