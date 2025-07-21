package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.LoginResponse;
import com.example.recyclerequestapp.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String usernameOrEmail,
            @Field("password") String password
    );
}
