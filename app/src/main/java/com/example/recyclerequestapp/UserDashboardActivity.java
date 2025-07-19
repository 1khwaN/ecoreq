package com.example.recyclerequestapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.model.Item;
import com.example.recyclerequestapp.model.Request; // Make sure this imports the correct Request model
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashboardActivity extends AppCompatActivity {

    private Spinner spinnerItemType;
    private EditText edtAddress, edtDate, edtNotes;
    private Button btnSubmitRequest, btnViewRequests, btnCancelRequest;
    private List<Item> itemList = new ArrayList<>();
    private RequestService requestService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Get user token
        token = SharedPrefManager.getInstance(this).getUser().getToken();
        requestService = ApiUtils.getRequestService(token);

        // Bind UI
        spinnerItemType = findViewById(R.id.spinnerItemType);
        edtAddress = findViewById(R.id.edtAddress);
        edtDate = findViewById(R.id.edtDate);
        edtNotes = findViewById(R.id.edtNotes);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);

        // View Requests
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, ViewAllRequestsActivity.class);
            startActivity(intent);
        });
        // Load dropdown items from API
        loadItemsFromDatabase();

        // Date picker
        edtDate.setOnClickListener(v -> new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> edtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show());

        // Button actions
        btnSubmitRequest.setOnClickListener(v -> submitRequest());
        btnCancelRequest.setOnClickListener(v -> startActivity(new Intent(this, CancelRequestActivity.class)));
    }

    private void loadItemsFromDatabase() {
        // Assuming getItems is part of your RequestService, or you have another service for items
        // If items are fetched via a different service, adjust ApiUtils.getRequestService(token).getItems
        requestService.getItems("Bearer " + token).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList = response.body();
                    List<String> names = new ArrayList<>();
                    for (Item i : itemList) {
                        names.add(i.getItemName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UserDashboardActivity.this,
                            android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerItemType.setAdapter(adapter);

                    Log.d("ITEM_LIST", "Items loaded: " + names);
                } else {
                    Toast.makeText(UserDashboardActivity.this,
                            "Failed to load items: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("LOAD_ITEMS", "Code=" + response.code() + " Msg=" + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LOAD_ITEMS", "Error", t);
            }
        });
    }

    private void submitRequest() {
        int pos = spinnerItemType.getSelectedItemPosition();
        if (pos < 0 || pos >= itemList.size()) {
            Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Item selectedItem = itemList.get(pos);
        String address = edtAddress.getText().toString().trim();
        String dateInput = edtDate.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();

        if (address.isEmpty() || dateInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert to correct date format
        String formattedDate;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formattedDate = outputFormat.format(inputFormat.parse(dateInput));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = SharedPrefManager.getInstance(this).getUser().getId();

        // Use the new constructor for submission (6 parameters)
        Request request = new Request(userId, selectedItem.getItemId(), address, formattedDate, "Pending", notes);

        // Submit to the base 'requests' table, NOT the view
        // Ensure your RequestService has a @POST("/requests") method
        // If not, you'll need to add it: Call<Void> submitRequest(@Body Request request);
        requestService.submitRequest(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserDashboardActivity.this, "Request submitted!", Toast.LENGTH_SHORT).show();
                    // Optional: Clear fields after successful submission
                    edtAddress.setText("");
                    edtDate.setText("");
                    edtNotes.setText("");
                    spinnerItemType.setSelection(0);
                } else {
                    Toast.makeText(UserDashboardActivity.this,
                            "Failed to submit: " + response.code(), Toast.LENGTH_LONG).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("SUBMIT_REQUEST", "Code: " + response.code() + " Msg: " + response.message() + " Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("SUBMIT_REQUEST", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SUBMIT_REQUEST", "Error", t);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}