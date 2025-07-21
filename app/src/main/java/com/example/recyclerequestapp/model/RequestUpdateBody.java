package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

public class RequestUpdateBody {
    @SerializedName("status")
    private String status;

    @SerializedName("weight")
    private Double weight;

    @SerializedName("total_price")
    private Double totalPrice;

    public RequestUpdateBody(String status, Double weight, Double totalPrice) {
        this.status = status;
        this.weight = weight;
        this.totalPrice = totalPrice;
    }

    public String getStatus() { return status; }
    public Double getWeight() { return weight; }
    public Double getTotalPrice() { return totalPrice; }
}