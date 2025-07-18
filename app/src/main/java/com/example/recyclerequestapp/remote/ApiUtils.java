package com.example.recyclerequestapp.remote;

public class ApiUtils {
    private static final String BASE_URL = "http://178.128.220.20/2024769923/api/";

    // Accept token as a parameter
    public static RequestService getRequestService(String token) {
        return RetrofitClient.getClient(BASE_URL, token).create(RequestService.class);
    }

    public static RequestService getRequestService() {
        return RetrofitClient.getClient(BASE_URL).create(RequestService.class);
    }
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL, "").create(UserService.class);
    }
}
