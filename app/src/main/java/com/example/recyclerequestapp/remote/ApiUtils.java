package com.example.recyclerequestapp.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    private static final String BASE_URL = "http://178.128.220.20/2024769923/api/";

    // This is the correct way to get RequestService with a token
    public static RequestService getRequestService(String token) {
        // RetrofitClient.getClient handles adding the "Bearer " prefix internally
        return RetrofitClient.getClient(BASE_URL, token).create(RequestService.class);
    }

    // Use this for services that do NOT require a token (e.g., login, registration)
    public static UserService getUserService() {
        // Pass an empty string or null for the token if no auth is needed, RetrofitClient handles it
        return RetrofitClient.getClient(BASE_URL, "").create(UserService.class);
    }

    // You can also add specific methods for other services if they don't require a token
    // Example: For RecyclableItemService if it's publicly accessible without a token
    public static RecyclableItemService getRecyclableItemService(String token) {
        return RetrofitClient.getClient(BASE_URL, token).create(RecyclableItemService.class);
    }

    // Removed the problematic getRequestService() (no-arg) and getRetrofitInstance() methods
    // to ensure all authenticated calls go through RetrofitClient's interceptor.
}