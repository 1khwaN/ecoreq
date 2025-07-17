package com.example.recyclerequestapp.remote;
import com.example.recyclerequestapp.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestService {

    // Admin: Get all requests
    @FormUrlEncoded
    @GET("requests") // Assuming your pRESTige endpoint for requests is /requests
    Call<List<Request>> getAllRequests();


    // User: Get requests for a specific user
    // Option 1: Using query parameter (common with pRESTige for filtering)
    @GET("requests")
    Call<List<Request>> getMyRequests(@Query("user_id") int userId);

    // Option 2: If your backend has a dedicated endpoint like /users/{id}/requests
    // @GET("users/{id}/requests")
    // Call<List<Request>> getMyRequests(@Path("id") int userId);
}
