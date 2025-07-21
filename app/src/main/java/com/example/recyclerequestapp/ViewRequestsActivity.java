package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.adapter.UserRequestListAdapter;
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

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserRequestListAdapter adapter;
    private List<Request> requestList;
    private RequestService requestService;
    private String authToken;
    private int currentUserId;

    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Requests");
        }

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spm = SharedPrefManager.getInstance(getApplicationContext());
        User loggedInUser = spm.getUser();
        if (loggedInUser != null) {
            authToken = loggedInUser.getToken();
            currentUserId = loggedInUser.getId();

            // *** ADDED LOG HERE ***
            Log.d("TOKEN_DEBUG", "Retrieved token from SharedPrefManager: " + authToken);
            Log.d("TOKEN_DEBUG", "Retrieved User ID from SharedPrefManager: " + currentUserId);

        }

        if (authToken == null || authToken.isEmpty() || currentUserId == 0) {
            Toast.makeText(this, "Authentication required. Please log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // ApiUtils.getRequestService(authToken) already configures Retrofit to add "Bearer "
        requestService = ApiUtils.getRequestService(authToken);
        requestList = new ArrayList<>();

        adapter = new UserRequestListAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        fetchUserRequests(currentUserId);
    }

    private void fetchUserRequests(int userId) {
        // Log the token just before making the API call (without "Bearer " here)
        Log.d("API_CALL_DEBUG", "Making API call for user " + userId + " with token: " + authToken);

        // --- FIX IS HERE: REMOVED "Bearer " prefix ---
        requestService.getRequestsByUserId(authToken, userId).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    requestList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d("ViewRequestsActivity", "Requests fetched successfully. Count: " + requestList.size());
                } else {
                    String errorMessage = "Failed to load requests: " + response.message();
                    if (response.code() == 401 || response.code() == 403) {
                        errorMessage = "Session expired or unauthorized. Please log in again.";
                        SharedPrefManager.getInstance(ViewRequestsActivity.this).logout();
                        startActivity(new Intent(ViewRequestsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish(); // Finish this activity so the user doesn't come back to it
                    }
                    Toast.makeText(ViewRequestsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("ViewRequestsActivity", "Fetch Error: " + response.code() + " " + response.message() + " " + (response.errorBody() != null ? response.errorBody().toString() : ""));
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
    protected void onResume() {
        super.onResume();
        // Re-fetch data if needed, or if the token might have changed (e.g., after login)
        if (spm.isLoggedIn() && currentUserId != 0) {
            User loggedInUser = spm.getUser();
            if (loggedInUser != null && !loggedInUser.getToken().equals(authToken)) {
                authToken = loggedInUser.getToken();
                requestService = ApiUtils.getRequestService(authToken); // Re-initialize service with new token
            }
            fetchUserRequests(currentUserId);
        } else {
            // If not logged in on resume, redirect to login
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
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