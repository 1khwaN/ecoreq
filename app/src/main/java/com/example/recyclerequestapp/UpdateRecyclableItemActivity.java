package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.model.RecyclableItem;
import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RecyclableItemService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRecyclableItemActivity extends AppCompatActivity {

    private TextInputEditText editTextItemName;
    private TextInputEditText editTextPricePerKg; // Changed ID
    private Button buttonUpdateItem;
    private Button buttonDeleteItem;

    private RecyclableItemService recyclableItemService;
    private SharedPrefManager sharedPrefManager;
    private String authToken;
    private int itemId; // ID of the item to update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recyclable_item);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Recyclable Item");
        }

        // Initialize views
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg); // Changed ID
        buttonUpdateItem = findViewById(R.id.buttonUpdateItem);
        buttonDeleteItem = findViewById(R.id.buttonDeleteItem);

        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        User loggedInUser = sharedPrefManager.getUser();

        if (loggedInUser != null && loggedInUser.getToken() != null && !loggedInUser.getToken().isEmpty()) {
            authToken = loggedInUser.getToken();
            // Optional: Add admin role check here if needed
            // if (!"admin".equalsIgnoreCase(loggedInUser.getRole())) {
            //     Toast.makeText(this, "Unauthorized access. Admins only.", Toast.LENGTH_SHORT).show();
            //     finish();
            //     return;
            // }
            recyclableItemService = ApiUtils.getRecyclableItemService(authToken); // Pass token
        } else {
            Toast.makeText(this, "Authentication required. Please log in as an administrator.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // Get item ID from intent extras
        if (getIntent().hasExtra("itemId")) {
            itemId = getIntent().getIntExtra("itemId", -1);
            if (itemId != -1) {
                fetchItemDetails(itemId); // Fetch existing data
            } else {
                Toast.makeText(this, "Invalid Item ID.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No Item ID provided for update.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonUpdateItem.setOnClickListener(v -> attemptUpdateItem());
        buttonDeleteItem.setOnClickListener(v -> confirmDeleteItem());
    }

    private void fetchItemDetails(int id) {
        recyclableItemService.getRecyclableItemById("Bearer " + authToken, id).enqueue(new Callback<RecyclableItem>() {
            @Override
            public void onResponse(@NonNull Call<RecyclableItem> call, @NonNull Response<RecyclableItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecyclableItem item = response.body();
                    editTextItemName.setText(item.getItemName());
                    editTextPricePerKg.setText(String.valueOf(item.getPricePerKg()));
                } else {
                    String errorMessage = "Failed to load item details: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Unauthorized. Please log in again.";
                        sharedPrefManager.logout();
                        startActivity(new Intent(UpdateRecyclableItemActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    Toast.makeText(UpdateRecyclableItemActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UpdateRecyclableItem", "Fetch Error: " + response.code() + " " + response.message());
                    finish(); // Close activity if details can't be loaded
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecyclableItem> call, @NonNull Throwable t) {
                Toast.makeText(UpdateRecyclableItemActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("UpdateRecyclableItem", "Network Failure during fetch", t);
                finish(); // Close activity on network error
            }
        });
    }

    private void attemptUpdateItem() {
        String itemName = editTextItemName.getText().toString().trim();
        String pricePerKgStr = editTextPricePerKg.getText().toString().trim();

        if (itemName.isEmpty() || pricePerKgStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Double pricePerKg;
        try {
            pricePerKg = Double.parseDouble(pricePerKgStr);
            if (pricePerKg < 0) {
                Toast.makeText(this, "Price per KG cannot be negative.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price per KG.", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclableItem updatedItem = new RecyclableItem(itemId, itemName, pricePerKg);

        recyclableItemService.updateRecyclableItem("Bearer " + authToken, itemId, updatedItem).enqueue(new Callback<RecyclableItem>() {
            @Override
            public void onResponse(@NonNull Call<RecyclableItem> call, @NonNull Response<RecyclableItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateRecyclableItemActivity.this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back
                } else {
                    String errorMessage = "Failed to update item: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Unauthorized. Please log in again.";
                        sharedPrefManager.logout();
                        startActivity(new Intent(UpdateRecyclableItemActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    Toast.makeText(UpdateRecyclableItemActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UpdateRecyclableItem", "API Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecyclableItem> call, @NonNull Throwable t) {
                Toast.makeText(UpdateRecyclableItemActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("UpdateRecyclableItem", "Network Failure during update", t);
            }
        });
    }

    private void confirmDeleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> attemptDeleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void attemptDeleteItem() {
        recyclableItemService.deleteRecyclableItem("Bearer " + authToken, itemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateRecyclableItemActivity.this, "Item deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back after deletion
                } else {
                    String errorMessage = "Failed to delete item: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Unauthorized. Please log in again.";
                        sharedPrefManager.logout();
                        startActivity(new Intent(UpdateRecyclableItemActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    Toast.makeText(UpdateRecyclableItemActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UpdateRecyclableItem", "Delete Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(UpdateRecyclableItemActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("UpdateRecyclableItem", "Network Failure during delete", t);
            }
        });
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