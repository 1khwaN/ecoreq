package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.Item;
import com.example.recyclerequestapp.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestService {

    @GET("recyclable_items")
    Call<List<Item>> getItems(@Header("Authorization") String token);



    @POST("requests")
    Call<Void> submitRequest(@Body Request request);
}
