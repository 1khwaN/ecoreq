// com.example.recyclerequestapp.ViewAllRequestsActivity.java
package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // Import MenuItem for back button
import android.widget.Toast;

import androidx.annotation.Nullable; // For onActivityResult if you use it
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration; // Added for visual separation
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.recyclerequestapp.adapter.RequestAdapter;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.User; // Import User model
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;
import com.example.recyclerequestapp.SharedPrefManager; // Import SharedPrefManager

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllRequestsActivity extends AppCompatActivity implements RequestAdapter.OnItemClickListener {

    private RecyclerView rvRequestList; // Standardized ID
    private RequestAdapter requestAdapter;
    private List<Request> requestList;
    private RequestService requestService;
    private String token; // Declare token variable
    private SwipeRefreshLayout swipeRefreshLayout; // Declare SwipeRefreshLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_requests); // Ensure this layout exists and has rvRequestList and swipeRefreshLayout

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Requests");
        }

        rvRequestList = findViewById(R.id.rvRequestList); // Standardized ID
        rvRequestList.setLayoutManager(new LinearLayoutManager(this));
        rvRequestList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); // Add item decoration
        registerForContextMenu(rvRequestList); // Register for context menu

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Make sure you have this ID in your layout

        requestList = new ArrayList<>();
        // Corrected RequestAdapter constructor call: pass Context, list, and listener
        requestAdapter = new RequestAdapter(this,requestList, this);
        rvRequestList.setAdapter(requestAdapter);

        // --- FIX START HERE ---
        // 1. Retrieve the token from SharedPrefManager
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User currentUser = spm.getUser();

        if (currentUser == null || currentUser.getToken() == null || currentUser.getToken().isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show();
            // Redirect to login activity and clear task stack
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return; // Stop execution if no token
        }
        token = currentUser.getToken();
        Log.d("AUTH_DEBUG", "Token retrieved from SharedPref: " + token);

        // 2. Initialize RequestService with the retrieved token
        requestService = ApiUtils.getRequestService(token);
        // --- FIX END HERE ---

        // Set up refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::fetchRequests);

        // Initial fetch of requests
        fetchRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always refresh the list when the activity comes to foreground
        // This ensures updates from RequestDetailsActivity are reflected
        fetchRequests();
    }

    private void fetchRequests() {
        // Show refreshing indicator
        if (!swipeRefreshLayout.isRefreshing()) { // Prevent infinite refresh if already refreshing
            swipeRefreshLayout.setRefreshing(true);
        }

        // Pass the authentication token to the API call
        requestService.getAllRequests("Bearer " + token).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                // Hide refreshing indicator
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Request> fetchedRequests = response.body();
                    requestAdapter.setRequestList(fetchedRequests); // Use setter to update adapter's list
                    Log.d("ViewAllRequests", "Requests fetched successfully: " + fetchedRequests.size());
                } else {
                    String errorMessage = "Failed to fetch requests: " + response.message();
                    // Specific handling for Unauthorized (401) or Forbidden (403)
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired or unauthorized. Please log in again.";
                        // Invalidate token and redirect to login
                        SharedPrefManager.getInstance(ViewAllRequestsActivity.this).logout();
                        startActivity(new Intent(ViewAllRequestsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    Toast.makeText(ViewAllRequestsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("ViewAllRequests", "Error response: " + response.code() + " " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("ViewAllRequests", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("ViewAllRequests", "Failed to read error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                // Hide refreshing indicator
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ViewAllRequestsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ViewAllRequests", "Network error fetching requests", t);
            }
        });
    }

    @Override
    public void onItemClick(Request request) {
        Intent intent = new Intent(ViewAllRequestsActivity.this, RequestDetailsActivity.class);
        // Ensure you pass the correct key name, "request_id" from previous context
        intent.putExtra("request_id", request.getRequestId());
        // If you need to pass more details for display in RequestDetailsActivity without fetching again,
        // you can put the whole Request object if it's Serializable or Parcelable
        // intent.putExtra("request_object", request); // if Request implements Serializable/Parcelable
        startActivity(intent);
    }

    // Use onOptionsItemSelected for the home button (up arrow)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Navigate back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}