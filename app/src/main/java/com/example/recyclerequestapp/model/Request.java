package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("request_id")
    private int requestId;

    public int getRequestId() {
        return requestId;
    }

    @SerializedName("user_id")
    private int userId;

    @SerializedName("item_id")
    private int itemId;

    @SerializedName("address")
    private String address;

    @SerializedName("request_date")
    private String requestDate;

    @SerializedName("status")
    private String status;

    @SerializedName("weight")
    private double weight;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("notes")
    private String notes;

    // Constructor
    public Request(int userId, int itemId, String address, String requestDate,
                   String status, double weight, double totalPrice, String notes) {

        this.userId = userId;
        this.itemId = itemId;
        this.address = address;
        this.requestDate = requestDate;
        this.status = status;
        this.weight = weight;
        this.totalPrice = totalPrice;
        this.notes = notes;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getAddress() {
        return address;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getStatus() {
        return status;
    }

    public double getWeight() {
        return weight;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", address='" + address + '\'' +
                ", requestDate='" + requestDate + '\'' +
                ", status='" + status + '\'' +
                ", weight=" + weight +
                ", totalPrice=" + totalPrice +
                ", notes='" + notes + '\'' +
                '}';
    }
}
