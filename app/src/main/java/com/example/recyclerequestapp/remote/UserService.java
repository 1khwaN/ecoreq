package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("login.php") // your PHP login endpoint
    Call<User> login(@Field("username") String username,
                     @Field("password") String password);
}
