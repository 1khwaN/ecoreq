package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.adapter.UserRequestListAdapter; // IMPORTANT: Use UserRequestListAdapter here
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;
import com.example.recyclerequestapp.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This is the USER-SIDE activity, it does NOT implement OnItemClickListener from RequestAdapter
public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserRequestListAdapter adapter; // Changed to UserRequestListAdapter
    private List<Request> requestList;
    private RequestService requestService;
    private String authToken;
    private int currentUserId;

    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests); // Assuming this is your layout for user requests list

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Requests"); // User-friendly title
        }

        recyclerView = findViewById(R.id.recyclerViewRequests); // Make sure this ID matches your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the authentication token and user ID from SharedPrefManager
        spm = SharedPrefManager.getInstance(getApplicationContext());
        User loggedInUser = spm.getUser();
        if (loggedInUser != null) {
            authToken = loggedInUser.getToken();
            currentUserId = loggedInUser.getId();
        }

        if (authToken == null || authToken.isEmpty() || currentUserId == 0) {
            Toast.makeText(this, "Authentication required. Please log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        requestService = ApiUtils.getRequestService(authToken);
        requestList = new ArrayList<>();

        // Initialize UserRequestListAdapter. It handles its own clicks.
        adapter = new UserRequestListAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        fetchUserRequests(currentUserId);
    }

    private void fetchUserRequests(int userId) {
        // Fetch requests specific to the logged-in user
// Assuming RetrofitClient adds "Bearer " prefix already
        requestService.getRequestsByUserId(authToken, userId).enqueue(new Callback<List<Request>>() {            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    requestList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    String errorMessage = "Failed to load requests: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired or unauthorized. Please log in again.";
                        SharedPrefManager.getInstance(ViewRequestsActivity.this).logout();
                        startActivity(new Intent(ViewRequestsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    Toast.makeText(ViewRequestsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("ViewRequestsActivity", "Fetch Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(ViewRequestsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ViewRequestsActivity", "Network error during fetch", t);
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