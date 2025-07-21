package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.Request;
import java.util.List;
import com.example.recyclerequestapp.model.RequestUpdateBody;
import com.example.recyclerequestapp.model.Item;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestService {

    // NOW uses the backend VIEW for enriched data
    @GET("requests") // IMPORTANT: Use the actual name of your pRESTige VIEW
    Call<List<Request>> getAllRequests(@Header("Authorization")String token);

    @GET("api/requests/user/{userId}") // Double-check this exact API endpoint with your backend
    Call<List<Request>> getRequestsByUserId(@Header("Authorization") String authToken, @Path("userId") int userId);
//    Call<List<Request>> getRequestsByUserId(@Path("userId") int userId);

    // NOW uses the backend VIEW for enriched data
    @GET("requests/{id}") // IMPORTANT: Use the actual name of your pRESTige VIEW
    Call<Request> getRequestsById(
            @Header("Authorization") String token,
            @Path("request_id") int requestId);

    // This still updates the original 'requests' table, not the VIEW
    @PATCH("requests/{id}")
    Call<ResponseBody> updateRequestStatus(
            @Header("Authorization") String token,
            @Path("request_id") int requestId,
            @Body RequestUpdateBody requestUpdate
    );

    @GET("recyclable_items")
    Call<List<Item>> getItems(@Header("Authorization") String token);


    @POST("requests")
    Call<Void> submitRequest(@Header("Authorization") String token, @Body Request request);

    @GET("requests")
    Call<List<Request>> getMyRequests();

    @FormUrlEncoded
    @POST("requests")
    Call<Request> cancelRequest(
            @Header("Authorization") String token,
            @Field("request_id") int requestId,
            @Field("status") String status
    );

}
