package com.example.recyclerequestapp.remote;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit getClient(String baseUrl, String token) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (token != null && !token.isEmpty()) {
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder()
                        .header("Authorization", "Bearer " + token) // 👈 this must be correct
                        .method(original.method(), original.body());
                return chain.proceed(builder.build());
            });
        }

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}


