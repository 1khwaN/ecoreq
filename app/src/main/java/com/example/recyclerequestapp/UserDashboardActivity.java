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
import com.example.recyclerequestapp.model.Request;
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
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        token = SharedPrefManager.getInstance(this).getUser().getToken();
        userId = SharedPrefManager.getInstance(this).getUser().getId();
        requestService = ApiUtils.getRequestService(token);

        spinnerItemType = findViewById(R.id.spinnerItemType);
        edtAddress = findViewById(R.id.edtAddress);
        edtDate = findViewById(R.id.edtDate);
        edtNotes = findViewById(R.id.edtNotes);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);

        loadItemsFromDatabase();

        edtDate.setOnClickListener(v -> new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> edtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show());

        btnSubmitRequest.setOnClickListener(v -> submitRequest());
        btnViewRequests.setOnClickListener(v -> startActivity(new Intent(this, ViewRequestsActivity.class)));
        btnCancelRequest.setOnClickListener(v -> startActivity(new Intent(this, CancelRequestActivity.class)));
    }

    private void loadItemsFromDatabase() {
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

                } else {
                    Toast.makeText(UserDashboardActivity.this,
                            "Failed to load items", Toast.LENGTH_LONG).show();
                    Log.e("LOAD_ITEMS", "Code=" + response.code() + " Msg=" + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        String formattedDate;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formattedDate = outputFormat.format(inputFormat.parse(dateInput));
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request(userId, selectedItem.getItemId(), address, formattedDate, "Pending", 0, 0, notes);

        requestService.submitRequest("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserDashboardActivity.this, "Request submitted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserDashboardActivity.this, "Submit failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
