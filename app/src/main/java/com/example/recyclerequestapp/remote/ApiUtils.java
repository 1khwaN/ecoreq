package com.example.recyclerequestapp.remote;

import com.example.recyclerequestapp.remote.RetrofitClient;
import com.example.recyclerequestapp.remote.UserService;

public class ApiUtils {
    private static final String BASE_URL = "http://your-server-url.com/api/";

    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
