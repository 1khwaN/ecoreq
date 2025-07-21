package com.example.recyclerequestapp.remote;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl, String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Add the Interceptor only if a token is provided
        if (token != null && !token.isEmpty()) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                // Add Authorization header
                requestBuilder.header("Authorization", "Bearer " + token);
                requestBuilder.method(original.method(), original.body());
                return chain.proceed(requestBuilder.build());
            });
        }

        OkHttpClient client = httpClient.build();

        // Ensure this is using the client with the interceptor
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // <-- Make sure this line is present
                .build();

        return retrofit;
    }
}


