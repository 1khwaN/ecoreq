package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.model.RecyclableItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RecyclableItemService {
    @GET("recyclable_items/{id}")
    Call<RecyclableItem> getRecyclableItemById(@Path("item_id") int itemId);

    @GET("recyclable_items")
    Call<List<RecyclableItem>> getAllRecyclableItems();
}