package com.example.recyclerequestapp.remote;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {
    private static final String BASE_URL = "http://178.128.220.20/2024769923/api/";

//    public static RequestService getRequestService(String token) {
//        return RetrofitClient.getClient(BASE_URL, token).create(RequestService.class);
//    }

    public static RequestService getRequestService(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", token)  // This must be set
                            .method(original.method(), original.body());
                    return chain.proceed(requestBuilder.build());
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RequestService.class);
    }


    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL, "").create(UserService.class);
    }

    public static RecyclableItemService getRecyclableItemService(String token) {
        // Pass the token to get an authenticated client
        return RetrofitClient.getClient(BASE_URL, token).create(RecyclableItemService.class);
    }
}
