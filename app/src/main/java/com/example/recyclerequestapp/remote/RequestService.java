package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.RequestUpdateBody;
import com.example.recyclerequestapp.model.Item;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RequestService {

    // NOW uses the backend VIEW for enriched data
    @GET("requests") // IMPORTANT: Use the actual name of your pRESTige VIEW
    Call<List<Request>> getAllRequests();

    // NOW uses the backend VIEW for enriched data
    @GET("requests/{id}") //
    Call<Request> getRequestById(@Path("request_id") int requestId);

    // This still updates the original 'requests' table, not the VIEW
    @PATCH("requests/{id}")
    Call<ResponseBody> updateRequestStatus(

            @Path("request_id") int requestId,
            @Body RequestUpdateBody requestUpdate
    );

    @GET("recyclable_items")
    Call<List<Item>> getItems(@Header("Authorization") String token);


    @POST("requests")
    Call<Void> submitRequest(@Body Request request);
}
