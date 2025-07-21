package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.RecyclableItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RecyclableItemService {

    // Get all recyclable items (optional, but useful for admin dashboard)
    @GET("recyclable_items")
    Call<List<RecyclableItem>> getRecyclableItems(@Header("Authorization") String authorization);

    // Get a single recyclable item by ID
    @GET("recyclable_items/{id}")
    Call<RecyclableItem> getRecyclableItemById(@Header("Authorization") String authorization, @Path("id") int itemId);

    // Add a new recyclable item
    // The @Body will automatically serialize RecyclableItem (itemName, pricePerKg)
    @POST("recyclable_items")
    Call<RecyclableItem> addRecyclableItem(@Header("Authorization") String authorization, @Body RecyclableItem item);

    // Update an existing recyclable item
    // The @Body will automatically serialize RecyclableItem (id, itemName, pricePerKg)
    @PUT("recyclable_items/{id}")
    Call<RecyclableItem> updateRecyclableItem(@Header("Authorization") String authorization, @Path("id") int itemId, @Body RecyclableItem item);

    // Delete a recyclable item (optional, but common for admin)
    @DELETE("recyclable_items/{id}")
    Call<Void> deleteRecyclableItem(@Header("Authorization") String authorization, @Path("id") int itemId);
}