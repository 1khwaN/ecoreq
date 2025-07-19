package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // Import MenuItem for back button
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // Import SwipeRefreshLayout if you intend to use it

import com.example.recyclerequestapp.adapter.RequestAdapter;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllRequestsActivity extends AppCompatActivity implements RequestAdapter.OnItemClickListener {

    private RecyclerView recyclerViewRequests;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;
    private RequestService requestService;
    private String token; // Declare token variable
    private SwipeRefreshLayout swipeRefreshLayout; // Declare SwipeRefreshLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_requests);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Requests");
        }

        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Make sure you have this ID in your layout

        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestList, this); // Pass 'this' as the listener since activity implements it
        recyclerViewRequests.setAdapter(requestAdapter);

        // --- FIX START HERE ---
        // 1. Retrieve the token from SharedPrefManager
        token = SharedPrefManager.getInstance(this).getUser().getToken();
        Log.d("AUTH_DEBUG", "Token retrieved from SharedPref: " + token);

        // 2. Check if token is available
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show();
            // Redirect to login activity or handle appropriately
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return; // Stop execution if no token
        }

        // 3. Initialize RequestService with the retrieved token
        requestService = ApiUtils.getRequestService(token);
        // --- FIX END HERE ---

        fetchRequests();

        // Set up refresh listener
        swipeRefreshLayout.setOnRefreshListener(this::fetchRequests);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRequests(); // Refresh the list when returning from RequestDetailsActivity
    }

    private void fetchRequests() {
        // Show refreshing indicator
        if (!swipeRefreshLayout.isRefreshing()) { // Prevent infinite refresh if already refreshing
            swipeRefreshLayout.setRefreshing(true);
        }

        requestService.getAllRequests().enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                // Hide refreshing indicator
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Request> fetchedRequests = response.body();
                    requestAdapter.setRequestList(fetchedRequests);
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
        intent.putExtra("REQUEST_ID", request.getRequestId());
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

    // Removed onSupportNavigateUp() as onOptionsItemSelected handles the back button more universally
}