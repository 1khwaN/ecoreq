package com.example.recyclerequestapp.model;

public class RequestStatusUpdate {
    private int request_id;
    private String status;

    public RequestStatusUpdate(int request_id, String status) {
        this.request_id = request_id;
        this.status = status;
    }

    // Optional: Getters and Setters
    public int getRequestId() {
        return request_id;
    }

    public void setRequestId(int request_id) {
        this.request_id = request_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

