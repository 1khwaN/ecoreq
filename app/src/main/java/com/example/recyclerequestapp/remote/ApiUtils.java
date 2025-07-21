package com.example.recyclerequestapp.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient; // Make sure OkHttpClient is imported if you're using it here

public class ApiUtils {

    // Use only one BASE_URL definition
    private static final String BASE_URL = "http://178.128.220.20/2024769923/api/";

    // No need for a separate getRetrofitInstance() if RetrofitClient handles it
    // public static RequestService getRequestService() {
    //     return getRetrofitInstance().create(RequestService.class);
    // }

    // Corrected calls to RetrofitClient
    public static RequestService getRequestService(String token) {
        // Pass the token to get an authenticated client
        return RetrofitClient.getClient(BASE_URL, token).create(RequestService.class);
    }

    public static UserService getUserService() {
        // Login service likely doesn't need a token initially
        return RetrofitClient.getClient(BASE_URL, "").create(UserService.class);
    }

    public static RecyclableItemService getRecyclableItemService(String token) {
        // Pass the token to get an authenticated client
        return RetrofitClient.getClient(BASE_URL, token).create(RecyclableItemService.class);
    }
}