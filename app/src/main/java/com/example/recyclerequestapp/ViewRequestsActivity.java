package com.example.recyclerequestapp;

import static com.example.recyclerequestapp.remote.ApiUtils.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.recyclerequestapp.adapter.UserRequestListAdapter;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.RequestService;
import com.example.recyclerequestapp.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserRequestListAdapter adapter;
    private List<Request> requestList;
    private RequestService requestService;
    private String authToken;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Requests");
        }

        // Initialize UI components
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get user token and ID
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User loggedInUser = spm.getUser();
        if (loggedInUser != null) {
            authToken = loggedInUser.getToken();
            currentUserId = loggedInUser.getId();
        }

        // Check for missing auth
        if (authToken == null || authToken.isEmpty() || currentUserId == 0) {
            Toast.makeText(this, "Authentication required. Please log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // Initialize Retrofit service with token
        requestService = RetrofitClient.getClient(BASE_URL, authToken)
                .create(RequestService.class);
        Call<List<Request>> call = requestService.getMyRequests();

        // Initialize RecyclerView adapter
        requestList = new ArrayList<>();
        adapter = new UserRequestListAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        // Set up swipe-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> fetchUserRequests(currentUserId));

        // Load requests initially
        fetchUserRequests(currentUserId);
    }

    private void fetchUserRequests(int userId) {
        swipeRefreshLayout.setRefreshing(true); // Show spinner

        requestService.getRequestsByUserId("Bearer " + authToken, userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                swipeRefreshLayout.setRefreshing(false); // Hide spinner

                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    requestList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    String errorMessage = "Failed to load requests: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired. Please log in again.";
                        SharedPrefManager.getInstance(ViewRequestsActivity.this).logout();
                        startActivity(new Intent(ViewRequestsActivity.this, LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    Toast.makeText(ViewRequestsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("ViewRequestsActivity", "Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false); // Hide spinner
                Toast.makeText(ViewRequestsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ViewRequestsActivity", "Network error", t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
