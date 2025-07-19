package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.Item;
import com.example.recyclerequestapp.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestService {

    // GET all recyclable items (requires Authorization)
    @GET("recyclable_items")
    Call<List<Item>> getItems(@Header("Authorization") String token);

    // Submit a new request (also requires Authorization)
    @POST("requests")
    Call<Void> submitRequest(
            @Header("Authorization") String token,
            @Body Request request
    );

    // Get all requests by user ID (requires Authorization)
    @GET("requests")
    Call<List<Request>> getMyRequests(
            @Header("Authorization") String token
    );

}
