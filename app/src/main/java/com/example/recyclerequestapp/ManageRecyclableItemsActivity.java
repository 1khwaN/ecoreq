package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.adapter.RecyclableItemAdapter;
import com.example.recyclerequestapp.model.RecyclableItem;
import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RecyclableItemService;

import java.io.Serializable; // Import Serializable
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageRecyclableItemsActivity extends AppCompatActivity implements RecyclableItemAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RecyclableItemAdapter adapter;
    private List<RecyclableItem> itemList;
    private RecyclableItemService itemService;
    private String authToken;
    private SharedPrefManager spm;

    private static final int UPDATE_ITEM_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recyclable_items);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Items");
        }

        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new RecyclableItemAdapter(this, itemList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();

        if (user == null || user.getToken() == null || user.getToken().isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        authToken = user.getToken();
        itemService = ApiUtils.getRecyclableItemService(authToken);

        loadRecyclableItems();
    }

    private void loadRecyclableItems() {
        Log.d("API_CALL", "Loading items with token: " + authToken);

        itemService.getAllRecyclableItems().enqueue(new Callback<List<RecyclableItem>>() {            @Override
            public void onResponse(@NonNull Call<List<RecyclableItem>> call, @NonNull Response<List<RecyclableItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList.clear();
                    itemList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("ManageRecyclableItems", "Items fetched successfully. Count: " + itemList.size());
                    if (itemList.isEmpty()) {
                        Toast.makeText(ManageRecyclableItemsActivity.this, "No recyclable items found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleApiError(response, "load items");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RecyclableItem>> call, @NonNull Throwable t) {
                Toast.makeText(ManageRecyclableItemsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ManageRecyclableItems", "Load items failure for recyclable items", t);
            }
        });
    }

    @Override
    public void onItemClick(RecyclableItem item) {
        Intent intent = new Intent(this, UpdateRecyclableItemActivity.class);
        intent.putExtra("item_object", (Serializable) item); // Pass as Serializable
        startActivityForResult(intent, UPDATE_ITEM_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
            loadRecyclableItems(); // Refresh the list
        }
    }

    private void handleApiError(Response<?> response, String operation) {
        String errorMessage = "Failed to " + operation + ": " + response.message();
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("ManageRecyclableItems", "Error " + operation + ": Code " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
            if (response.code() == 401 || response.code() == 403) {
                errorMessage = "Session expired or unauthorized. Please log in again.";
                spm.logout();
                startActivity(new Intent(ManageRecyclableItemsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        } catch (Exception e) {
            Log.e("ManageRecyclableItems", "Error reading errorBody", e);
            errorMessage = "Failed to " + operation + " (Error: " + response.code() + ")";
        }
        Toast.makeText(ManageRecyclableItemsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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