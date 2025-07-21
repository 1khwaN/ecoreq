package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.Item;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.RequestUpdateBody;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestService {

    @GET("recyclable_items")
    Call<List<Item>> getItems(@Header("Authorization") String token);

    @POST("requests")
    Call<Void> submitRequest(@Header("Authorization") String token, @Body Request request);

    @GET("requests")
    Call<List<Request>> getMyRequests(@Header("Authorization") String token);

    @GET("requests") // IMPORTANT: Use the actual name of your pRESTige VIEW
    Call<List<Request>> getAllRequests();

    @PATCH("requests/{id}")
    Call<ResponseBody> updateRequestStatus(
            @Header("Authorization") String token,
            @Path("request_id") int requestId,
            @Body RequestUpdateBody requestUpdate
    );

    @GET("requests/{request_id}") //
    Call<Request> getRequestById(@Header("Authorization") String token,
                                 @Path("request_id") int requestId);
    @FormUrlEncoded
    @POST("requests")
    Call<Request> cancelRequest(
            @Header("Authorization") String token,
            @Field("request_id") int requestId,
            @Field("status") String status
    );

}
