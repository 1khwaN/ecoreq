package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.Item;
import com.example.recyclerequestapp.model.Request;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestService {

    @GET("recyclable_items")
    Call<List<Item>> getItems(@Header("Authorization") String token);

    @POST("requests")
    Call<Void> submitRequest(@Header("Authorization") String token, @Body Request request);

    @GET("requests")
    Call<List<Request>> getMyRequests(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("requests")
    Call<Request> cancelRequest(
            @Header("Authorization") String token,
            @Field("request_id") int requestId,
            @Field("status") String status
    );

}
