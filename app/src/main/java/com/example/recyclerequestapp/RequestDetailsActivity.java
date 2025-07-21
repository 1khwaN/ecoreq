// com.example.recyclerequestapp.RequestDetailsActivity.java
package com.example.recyclerequestapp;

import android.content.Intent; // Added for LoginActivity redirect
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.RequestUpdateBody;
import com.example.recyclerequestapp.model.User; // Import User model
import com.example.recyclerequestapp.model.RecyclableItem; // Import RecyclableItem model
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;
import com.example.recyclerequestapp.SharedPrefManager; // Corrected Import: Assuming SharedPrefManager is in 'sharedpref' package

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDetailsActivity extends AppCompatActivity {

    private TextView tvRequestId, tvRequestUser, tvRequestItem, tvRequestAddress, tvRequestDate, tvRequestNotes;
    private Spinner spinnerStatus;
    private EditText edtWeight;
    private TextView tvCalculatedPrice;
    private Button btnUpdateStatus;

    private int requestId;
    private double itemUnitPrice = 0.0; // Price per kg for the specific item
    private RequestService requestService;
    private Request currentRequest; // To store the fetched request details
    private String authToken; // To store the authentication token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Request Details");
        }

        // Initialize views
        tvRequestId = findViewById(R.id.tvRequestIdDetail);
        tvRequestUser = findViewById(R.id.tvRequestUserDetail);
        tvRequestItem = findViewById(R.id.tvRequestItemDetail);
        tvRequestAddress = findViewById(R.id.tvRequestAddressDetail);
        tvRequestDate = findViewById(R.id.tvRequestDateDetail);
        tvRequestNotes = findViewById(R.id.tvRequestNotesDetail);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        edtWeight = findViewById(R.id.edtWeightDetail);
        tvCalculatedPrice = findViewById(R.id.tvCalculatedPriceDetail);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        // Get the authentication token from SharedPrefManager
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User loggedInUser = spm.getUser();
        if (loggedInUser != null) {
            authToken = loggedInUser.getToken();
        }

        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show();
            // Redirect to login activity and clear task stack
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // Initialize requestService with the token
        requestService = ApiUtils.getRequestService(authToken);

        // Get request ID from Intent
        // Ensure "request_id" is the correct key used when starting this activity (e.g., from ViewAllRequestsActivity)
        if (getIntent().hasExtra("request_id")) {
            requestId = getIntent().getIntExtra("request_id", -1);
            if (requestId != -1) {
                fetchRequestDetails(requestId);
            } else {
                Toast.makeText(this, "Invalid Request ID", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No Request ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.request_statuses, // Make sure you have this array in your strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Set up weight input listener for price calculation
        edtWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePrice();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnUpdateStatus.setOnClickListener(v -> updateRequest());
    }

    private void fetchRequestDetails(int id) {
        // Pass the authentication token to the API call
        requestService.getRequestsById("Bearer " + authToken, id).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentRequest = response.body();
                    displayRequestDetails(currentRequest); // Display all details directly from the enriched Request object
                } else {
                    String errorMessage = "Failed to load request: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired or unauthorized. Please log in again.";
                        SharedPrefManager.getInstance(RequestDetailsActivity.this).logout();
                        startActivity(new Intent(RequestDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    Toast.makeText(RequestDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("RequestDetails", "Fetch Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("RequestDetails", "Could not read error body", e);
                    }
                    Log.e("RequestDetails", "Fetch Error: " + response.code() + " " + response.message());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                Toast.makeText(RequestDetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RequestDetails", "Network error during fetch", t);
                finish();
            }
        });
    }

    private void displayRequestDetails(Request request) {
        tvRequestId.setText("Request ID: #" + request.getRequestId());

        // CORRECTED: Access username from the nested User object
        String usernameToDisplay = "N/A";
        // Assuming your Request model has a getUser() method that returns a User object
        if (request.getUser() != null && request.getUser().getUsername() != null) {
            usernameToDisplay = request.getUser().getUsername();
        } else {
            usernameToDisplay = "ID: " + request.getUserId(); // Fallback if User object or its username is null
        }
        tvRequestUser.setText("User: " + usernameToDisplay);

        // CORRECTED: Access item name from the nested RecyclableItem object
        String itemNameToDisplay = "N/A";
        // Assuming your Request model has a getRecyclableItem() method that returns a RecyclableItem object
        if (request.getItem() != null && request.getItem().getItemName() != null) {
            itemNameToDisplay = request.getItem().getItemName();
        } else {
            itemNameToDisplay = "ID: " + request.getItemId(); // Fallback if RecyclableItem object or its name is null
        }
        tvRequestItem.setText("Item: " + itemNameToDisplay);

        tvRequestAddress.setText("Address: " + request.getAddress());
        tvRequestDate.setText("Date: " + request.getRequestDate());
        tvRequestNotes.setText("Notes: " + (request.getDisplayNotes() != null ? request.getDisplayNotes() : "N/A"));

        // Set initial status in spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerStatus.getAdapter();
        int spinnerPosition = adapter.getPosition(request.getStatus());
        spinnerStatus.setSelection(spinnerPosition);

        // Pre-fill weight and set item unit price for calculation
        if (request.getWeight() != null && request.getWeight() > 0) {
            edtWeight.setText(String.valueOf(request.getWeight()));
        } else {
            edtWeight.setText(""); // Ensure it's empty if weight is null or zero
        }

        // Use the pricePerKg directly from the Request model, or from nested RecyclableItem if preferred
        // Assuming Request has getPricePerKg() directly OR getItem().getPricePerKg()
        if (request.getPricePerKg() != null) {
            itemUnitPrice = request.getItem().getPricePerKg();
        } else if (request.getItem() != null && request.getItem().getPricePerKg() != null) {
            itemUnitPrice = request.getItem().getPricePerKg(); // Fallback to nested item's price
        } else {
            itemUnitPrice = 0.0; // Default if no price is available
        }
        Log.d("RequestDetails", "Item Unit Price: " + itemUnitPrice);

        calculatePrice(); // Calculate initial price based on pre-filled weight and itemUnitPrice
    }

    private void calculatePrice() {
        String weightStr = edtWeight.getText().toString();
        if (!weightStr.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightStr);
                double calculatedPrice = weight * itemUnitPrice;
                tvCalculatedPrice.setText(String.format("RM %.2f", calculatedPrice));
            } catch (NumberFormatException e) {
                tvCalculatedPrice.setText("Invalid Weight");
            }
        } else {
            tvCalculatedPrice.setText("RM 0.00"); // Display 0.00 if weight is empty
        }
    }

    private void updateRequest() {
        if (currentRequest == null) {
            Toast.makeText(this, "No request data to update.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Authentication token missing for update. Please log in again.", Toast.LENGTH_LONG).show();
            // Redirect to login activity and clear task stack
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        String newStatus = spinnerStatus.getSelectedItem().toString();
        String weightStr = edtWeight.getText().toString();
        Double newWeight = null;
        Double newTotalPrice = null;

        if (!weightStr.isEmpty()) {
            try {
                newWeight = Double.parseDouble(weightStr);
                // Ensure itemUnitPrice is correctly set before calculating
                if (itemUnitPrice == 0.0) { // If itemUnitPrice wasn't loaded or is 0
                    // Try to get price from currentRequest in case it wasn't set or changed
                    if (currentRequest.getItem().getPricePerKg() != null) {
                        itemUnitPrice = currentRequest.getItem().getPricePerKg();
                    } else if (currentRequest.getItem() != null || currentRequest.getItem().getPricePerKg() != null) {
                        itemUnitPrice = currentRequest.getItem().getPricePerKg();
                    }
                }
                newTotalPrice = newWeight * itemUnitPrice;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // If weight is cleared, send null or 0.0 as appropriate for your backend
            newWeight = 0.0; // Or null, depending on your backend's expectation for empty weight
            newTotalPrice = 0.0; // Or null
        }

        // Ensure currentRequest.getRequestId() is used
        int idToUpdate = currentRequest.getRequestId();

        RequestUpdateBody updateBody = new RequestUpdateBody(newStatus, newWeight, newTotalPrice);

        // Pass the authentication token to the API call
        requestService.updateRequestStatus("Bearer " + authToken, idToUpdate, updateBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestDetailsActivity.this, "Request updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Inform the calling activity (ViewAllRequestsActivity) that an update occurred
                    finish(); // Go back to ViewAllRequestsActivity to refresh the list
                } else {
                    String errorMessage = "Failed to update request: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired or unauthorized. Please log in again.";
                        SharedPrefManager.getInstance(RequestDetailsActivity.this).logout();
                        startActivity(new Intent(RequestDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    Toast.makeText(RequestDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("RequestDetails", "Update Error Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("RequestDetails", "Could not read error body", e);
                    }
                    Log.e("RequestDetails", "Update Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RequestDetailsActivity.this, "Network error during update: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RequestDetails", "Network update error", t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}