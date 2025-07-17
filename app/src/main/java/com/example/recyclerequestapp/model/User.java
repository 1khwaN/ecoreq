package com.example.recyclerequestapp.model;

public class User {
    private String user_id;
    private String user_name;
    private String user_email;
    private String user_phoneNum;
    private String role;
    private String token; // if your API returns a token

    // Getters
    public String getUser_id() { return user_id; }
    public String getUser_name() { return user_name; }
    public String getUser_email() { return user_email; }
    public String getUser_phoneNum() { return user_phoneNum; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}
