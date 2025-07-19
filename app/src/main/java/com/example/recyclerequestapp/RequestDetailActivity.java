package com.example.recyclerequestapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class RequestDetailActivity extends AppCompatActivity {

    TextView tvStatus, tvTotalPrice, tvDate, tvAddress, tvNotes, tvWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        tvStatus = findViewById(R.id.tvStatus);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvDate = findViewById(R.id.tvDateDetail);
        tvAddress = findViewById(R.id.tvAddress);
        tvNotes = findViewById(R.id.tvNotes);
        tvWeight = findViewById(R.id.tvWeight);

        String status = getIntent().getStringExtra("status");
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
        String date = getIntent().getStringExtra("date");
        String address = getIntent().getStringExtra("address");
        String notes = getIntent().getStringExtra("notes");
        double weight = getIntent().getDoubleExtra("weight", 0.0);

        DecimalFormat df = new DecimalFormat("0.00");

        tvStatus.setText(status);
        tvTotalPrice.setText("RM " + df.format(totalPrice));
        tvDate.setText(date);
        tvAddress.setText(address);
        tvNotes.setText(notes);
        tvWeight.setText(weight + " kg");
    }
}
