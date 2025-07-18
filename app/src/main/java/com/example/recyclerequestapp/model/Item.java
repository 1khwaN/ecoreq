package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("item_id")
    private int itemId;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("price_per_kg")
    private double pricePerKg;

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public double getPricePerKg() { return pricePerKg; }
}
