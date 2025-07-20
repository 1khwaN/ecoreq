// com.example.recyclerequestapp.model.RequestUpdateBody.java
package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestUpdateBody implements Serializable {
    @SerializedName("status")
    private String status;
    @SerializedName("weight")
    private Double weight;
    @SerializedName("total_price")
    private Double totalPrice;
    // Potentially include notes, etc., if they can be updated

    public RequestUpdateBody(String status, Double weight, Double totalPrice) {
        this.status = status;
        this.weight = weight;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters (if needed by Gson or for other logic)
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}