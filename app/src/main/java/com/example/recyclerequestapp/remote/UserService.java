package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(@Field("email") String email,
                          @Field("password") String password);

    @GET("users/{id}")
    Call<User> getUserById(@Path("user_id") int userId);
}
