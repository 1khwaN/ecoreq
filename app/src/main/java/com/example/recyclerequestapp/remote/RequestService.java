package com.example.recyclerequestapp.remote;
import com.example.recyclerequestapp.model.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

public interface RequestService {

    @FormUrlEncoded
    @GET("requests") // Assuming your pRESTige endpoint for requests is /requests
    Call<List<Request>> getAllRequests();
}
