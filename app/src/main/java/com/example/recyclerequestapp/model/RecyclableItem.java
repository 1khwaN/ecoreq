package com.example.recyclerequestapp.model;

import com.google.gson.annotations.SerializedName;


public class RecyclableItem {
    @SerializedName("item_id")
    private int id;
    @SerializedName("item_name")
    private String itemName;
    @SerializedName("price_per_kg")
    private Double pricePerKg;

    public RecyclableItem(int id, String itemName, Double pricePerKg) {
        this.id = id;
        this.itemName = itemName;
        this.pricePerKg = pricePerKg;
    }

    public int getId() { return id; }
    public String getItemName() { return itemName; }
    public Double getPricePerKg() { return pricePerKg; }
}