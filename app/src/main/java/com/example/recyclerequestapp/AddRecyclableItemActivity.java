package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.model.RecyclableItem;
import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RecyclableItemService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRecyclableItemActivity extends AppCompatActivity {

    private TextInputEditText etItemName, etPricePerKg;
    private Button btnAddItem;
    private TextView tvFormTitle;

    private RecyclableItemService itemService;
    private String authToken;
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recyclable_item);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Item");
        }

        etItemName = findViewById(R.id.et_item_name);
        etPricePerKg = findViewById(R.id.et_price_per_kg);
        btnAddItem = findViewById(R.id.btn_save_item);
        tvFormTitle = findViewById(R.id.tv_form_title);
        tvFormTitle.setText("Add New Recyclable Item");

        spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();

        if (user == null || user.getToken() == null || user.getToken().isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // In AddRecyclableItemActivity, inside onCreate or wherever you initialize itemService
        authToken = user.getToken();
        Log.d("AddRecyclableItem", "Auth Token from SPM: " + authToken);
        itemService = ApiUtils.getRecyclableItemService(authToken);

        btnAddItem.setOnClickListener(v -> addItem());
    }

    private void addItem() {
        String itemName = etItemName.getText().toString().trim();
        String pricePerKgStr = etPricePerKg.getText().toString().trim();

        if (TextUtils.isEmpty(itemName)) {
            etItemName.setError("Item Name is required");
            etItemName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pricePerKgStr)) {
            etPricePerKg.setError("Price per KG is required");
            etPricePerKg.requestFocus();
            return;
        }

        double pricePerKg;
        try {
            pricePerKg = Double.parseDouble(pricePerKgStr);
            if (pricePerKg < 0) {
                etPricePerKg.setError("Price cannot be negative");
                etPricePerKg.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPricePerKg.setError("Invalid price format");
            etPricePerKg.requestFocus();
            return;
        }

        RecyclableItem newItem = new RecyclableItem(itemName, pricePerKg);

        Log.d("API_CALL", "Adding item with token: " + authToken);

        itemService.addRecyclableItem(newItem).enqueue(new Callback<RecyclableItem>() {            @Override
        public void onResponse(@NonNull Call<RecyclableItem> call, @NonNull Response<RecyclableItem> response) {
            if (response.isSuccessful()) {
                Toast.makeText(AddRecyclableItemActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                handleApiError(response, "add item");
            }
        }

            @Override
            public void onFailure(@NonNull Call<RecyclableItem> call, @NonNull Throwable t) {
                Toast.makeText(AddRecyclableItemActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddRecyclableItem", "Add item failure", t);
            }
        });
    }

    private void handleApiError(Response<?> response, String operation) {
        String errorMessage = "Failed to " + operation + ": " + response.message();
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("AddRecyclableItem", "Error " + operation + ": Code " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
            if (response.code() == 401 || response.code() == 403) {
                errorMessage = "Session expired or unauthorized. Please log in again.";
                spm.logout();
                startActivity(new Intent(AddRecyclableItemActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        } catch (Exception e) {
            Log.e("AddRecyclableItem", "Error reading errorBody", e);
            errorMessage = "Failed to " + operation + " (Error: " + response.code() + ")";
        }
        Toast.makeText(AddRecyclableItemActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}