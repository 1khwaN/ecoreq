package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("users/login")
    Call<LoginResponse> login(
            @Field("username") String usernameOrEmail,
            @Field("password") String password
    );
}
