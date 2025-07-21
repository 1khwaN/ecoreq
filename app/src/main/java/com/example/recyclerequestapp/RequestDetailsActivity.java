package com.example.recyclerequestapp;

import android.content.Intent; // Added for login redirection
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
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;

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
    private double itemUnitPrice = 0.0;
    private RequestService requestService;
    private Request currentRequest;
    private String authToken; // Declare authToken
    private SharedPrefManager spm; // Declare SharedPrefManager

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

        // Initialize SharedPrefManager and get token
        spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();

        if (user == null || user.getToken() == null || user.getToken().isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        authToken = user.getToken();
        requestService = ApiUtils.getRequestService(authToken); // <--- Pass authToken here

        // Get request ID from Intent
        if (getIntent().hasExtra("REQUEST_ID")) {
            requestId = getIntent().getIntExtra("REQUEST_ID", -1);
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
                R.array.request_statuses,
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
        requestService.getRequestById(id).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentRequest = response.body();
                    displayRequestDetails(currentRequest);
                } else {
                    // Handle API errors here as well, similar to AddRecyclableItemActivity
                    handleApiError(response, "fetch request details");
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                Toast.makeText(RequestDetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RequestDetails", "Network error", t);
                finish();
            }
        });
    }

    private void displayRequestDetails(Request request) {
        tvRequestId.setText("Request ID: #" + request.getRequestId());
        tvRequestUser.setText("User: " + (request.getUsername() != null ? request.getUsername() : "ID: " + request.getUserId()));
        tvRequestItem.setText("Item: " + (request.getItemName() != null ? request.getItemName() : "ID: " + request.getItemId()));
        tvRequestAddress.setText("Address: " + request.getAddress());
        tvRequestDate.setText("Date: " + request.getRequestDate());
        tvRequestNotes.setText("Notes: " + request.getDisplayNotes());

        ArrayAdapter adapter = (ArrayAdapter) spinnerStatus.getAdapter();
        int spinnerPosition = adapter.getPosition(request.getStatus());
        spinnerStatus.setSelection(spinnerPosition);

        if (request.getWeight() != null && request.getWeight() > 0) {
            edtWeight.setText(String.valueOf(request.getWeight()));
        }
        if (request.getPricePerKg() != null) {
            itemUnitPrice = request.getPricePerKg();
        }

        calculatePrice();
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
            tvCalculatedPrice.setText("0.00");
        }
    }

    private void updateRequest() {
        if (currentRequest == null) {
            Toast.makeText(this, "No request data to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newStatus = spinnerStatus.getSelectedItem().toString();
        String weightStr = edtWeight.getText().toString();
        Double newWeight = null;
        Double newTotalPrice = null;

        if (!weightStr.isEmpty()) {
            try {
                newWeight = Double.parseDouble(weightStr);
                if (itemUnitPrice == 0.0 && currentRequest.getPricePerKg() != null) {
                    itemUnitPrice = currentRequest.getPricePerKg();
                }
                newTotalPrice = newWeight * itemUnitPrice;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            newWeight = null;
            newTotalPrice = null;
        }

        RequestUpdateBody updateBody = new RequestUpdateBody(newStatus, newWeight, newTotalPrice);

        // Add authToken to updateRequestStatus if your RequestService still expects it
        // based on previous analysis, if RequestService also has its headers managed by RetrofitClient
        // then you wouldn't pass token here. Let's assume for now, it's consistent with RecyclableItemService
        requestService.updateRequestStatus(requestId, updateBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RequestDetailsActivity.this, "Request updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // To notify the previous activity to refresh
                    finish();
                } else {
                    handleApiError(response, "update request"); // Centralize error handling
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RequestDetailsActivity.this, "Network error during update: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("RequestDetails", "Network update error", t);
            }
        });
    }

    // Centralized API error handling (copied from AddRecyclableItemActivity)
    private void handleApiError(Response<?> response, String operation) {
        String errorMessage = "Failed to " + operation + ": " + response.message();
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("RequestDetails", "Error " + operation + ": Code " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
            if (response.code() == 401 || response.code() == 403) {
                errorMessage = "Session expired or unauthorized. Please log in again.";
                spm.logout();
                startActivity(new Intent(RequestDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        } catch (Exception e) {
            Log.e("RequestDetails", "Error reading errorBody", e);
            errorMessage = "Failed to " + operation + " (Error: " + response.code() + ")";
        }
        Toast.makeText(RequestDetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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