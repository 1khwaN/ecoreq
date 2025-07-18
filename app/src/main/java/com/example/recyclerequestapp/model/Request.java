package com.example.recyclerequestapp.model;

public class Request {
    private int userId;
    private int itemId;
    private String address;
    private String requestDate;
    private String status;
    private double weight;
    private double totalPrice;
    private String notes;

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
}
