package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Request implements Serializable {

    // IMPORTANT: Ensure your backend VIEW (e.g., 'requests_with_details') outputs 'request_id' or 'id' for the primary key
    @SerializedName("request_id") // Or "request_id" if your VIEW names it this way
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

    // --- ENRICHED FIELDS from backend JOINs/VIEW ---
    @SerializedName("username")
    private String username;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price_per_kg") // Assuming your JOIN/VIEW provides this directly
    private Double pricePerKg;


    // Constructor (updated to include enriched fields)
    public Request(int requestId, int userId, int itemId, String address, String requestDate,
                   String status, Double weight, Double totalPrice, String notes,
                   String username, String itemName, Double pricePerKg) {
        this.requestId = requestId;
        this.userId = userId;
        this.itemId = itemId;
        this.address = address;
        this.requestDate = requestDate;
        this.status = status;
        this.weight = weight;
        this.totalPrice = totalPrice;
        this.notes = notes;
        this.username = username;
        this.itemName = itemName;
        this.pricePerKg = pricePerKg;
    }

    // Getters
    public int getRequestId() { return requestId; }
    public int getUserId() { return userId; }
    public int getItemId() { return itemId; }
    public String getAddress() { return address; }
    public String getRequestDate() { return requestDate; }
    public String getStatus() { return status; }
    public Double getWeight() { return weight; }
    public Double getTotalPrice() { return totalPrice; }
    public String getNotes() { return notes; }
    public String getUsername() { return username; } // Re-added
    public String getItemName() { return itemName; } // Re-added
    public Double getPricePerKg() { return pricePerKg; } // Re-added

    // Setters (if needed for internal logic)
    public void setStatus(String status) { this.status = status; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    // Helper methods for display formatting
    public String getDisplayWeight() { return (weight != null) ? String.format("%.2f kg", weight) : "N/A"; }
    public String getDisplayTotalPrice() { return (totalPrice != null) ? String.format("RM %.2f", totalPrice) : "N/A"; }
    public String getDisplayNotes() { return (notes != null && !notes.isEmpty()) ? notes : "None"; }
}