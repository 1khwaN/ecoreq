package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.RecyclableItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
// import retrofit2.http.Header; // No longer needed here
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RecyclableItemService {

    @GET("recyclable_items/{id}")
    Call<RecyclableItem> getRecyclableItemById(
            // @Header("Authorization") String authorization, // REMOVE THIS LINE
            @Path("id") int itemId
    );

    @GET("recyclable_items")
    Call<List<RecyclableItem>> getAllRecyclableItems(
            // @Header("Authorization") String authorization // REMOVE THIS LINE
    );

    @POST("recyclable_items")
    Call<RecyclableItem> addRecyclableItem(
            // @Header("Authorization") String authorization, // REMOVE THIS LINE
            @Body RecyclableItem item
    );

    @PUT("recyclable_items/{id}")
    Call<RecyclableItem> updateRecyclableItem(
            // @Header("Authorization") String authorization, // REMOVE THIS LINE
            @Path("id") int itemId,
            @Body RecyclableItem item
    );

    @DELETE("recyclable_items/{id}")
    Call<Void> deleteRecyclableItem(
            // @Header("Authorization") String authorization, // REMOVE THIS LINE
            @Path("id") int itemId
    );
}