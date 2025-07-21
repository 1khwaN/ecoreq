package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
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

    private TextInputEditText editTextItemName;
    private TextInputEditText editTextPricePerKg;
    private Button buttonAddItem;

    private RecyclableItemService recyclableItemService;
    private SharedPrefManager sharedPrefManager;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recyclable_item);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Recyclable Item");
        }

        // Initialize views
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextPricePerKg = findViewById(R.id.editTextPricePerKg); // Changed ID
        buttonAddItem = findViewById(R.id.buttonAddItem);

        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        User loggedInUser = sharedPrefManager.getUser();

        if (loggedInUser != null && loggedInUser.getToken() != null && !loggedInUser.getToken().isEmpty()) {
            authToken = loggedInUser.getToken();

            recyclableItemService = ApiUtils.getRecyclableItemService(authToken); // Pass token
        } else {
            Toast.makeText(this, "Authentication required. Please log in as an administrator.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        buttonAddItem.setOnClickListener(v -> attemptAddItem());
    }

    private void attemptAddItem() {
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

        // Create RecyclableItem. Note: For adding, ID is usually set by backend, so pass 0 or a placeholder.
        // If your backend assigns ID, it will return the object with the new ID.
        // Assuming your constructor can handle a dummy ID for new items
        RecyclableItem newItem = new RecyclableItem(0, itemName, pricePerKg);

        // Make the API call
        recyclableItemService.addRecyclableItem("Bearer " + authToken, newItem).enqueue(new Callback<RecyclableItem>() {
            @Override
            public void onResponse(@NonNull Call<RecyclableItem> call, @NonNull Response<RecyclableItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddRecyclableItemActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back
                } else {
                    String errorMessage = "Failed to add item: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Unauthorized. Please log in again.";
                        sharedPrefManager.logout();
                        startActivity(new Intent(AddRecyclableItemActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    Toast.makeText(AddRecyclableItemActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("AddRecyclableItem", "API Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecyclableItem> call, @NonNull Throwable t) {
                Toast.makeText(AddRecyclableItemActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("AddRecyclableItem", "Network Failure", t);
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