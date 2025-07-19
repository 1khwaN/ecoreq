package com.example.recyclerequestapp.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    // // IMPORTANT: REPLACE WITH YOUR ACTUAL pRESTige BASE URL
    // // Example: "http://192.168.1.100:3000/" or "http://yourdomain.com:3000/"
    // public static final String BASE_URL = "http://178.128.220.20/2024988895/api/";

     private static Retrofit getRetrofitInstance() {
         return new Retrofit.Builder()
                 .baseUrl(BASE_URL)
                 .addConverterFactory(GsonConverterFactory.create())
                 .build();
     }

    // public static UserService getUserService() {
    //     return getRetrofitInstance().create(UserService.class);
    private static final String BASE_URL = "http://178.128.220.20/2024769923/api/";

    // Accept token as a parameter
    public static RequestService getRequestService(String token) {
        return RetrofitClient.getClient(BASE_URL, token).create(RequestService.class);
    }

    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL, "").create(UserService.class);
    }

    public static RequestService getRequestService() {
        return getRetrofitInstance().create(RequestService.class);
    }

    public static RecyclableItemService getRecyclableItemService() {
        return getRetrofitInstance().create(RecyclableItemService.class);
    }
}