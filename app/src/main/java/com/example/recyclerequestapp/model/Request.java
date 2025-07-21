package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("request_id")
    private int requestId;

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
    private Double weight;

    @SerializedName("total_price")
    private Double totalPrice;

    @SerializedName("notes")
    private String notes;

    @SerializedName("username")
    private String username;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price_per_kg") // Assuming your JOIN/VIEW provides this directly
    private double pricePerKg;

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

    public int getRequestId() {
        return requestId;
    }
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

    public Double getWeight() {
        return weight;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getNotes() {
        return notes;
    }

    public String getUsername()
    {
        return username;
    } // Re-added
    public String getItemName() {
        return itemName;
    } // Re-added
    public Double getPricePerKg() {
        return pricePerKg;
    } // Re-added

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisplayWeight() {
        return (weight != null) ? String.format("%.2f kg", weight) : "N/A";
    }
    public String getDisplayTotalPrice() {
        return (totalPrice != null) ? String.format("RM %.2f", totalPrice) : "N/A";
    }
    public String getDisplayNotes() {
        return (notes != null && !notes.isEmpty()) ? notes : "None";
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
