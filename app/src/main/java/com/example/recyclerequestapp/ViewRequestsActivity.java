package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.adapter.UserRequestAdapter;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserRequestAdapter adapter;
    private RequestService requestService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        recyclerView = findViewById(R.id.recyclerViewRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        User user = SharedPrefManager.getInstance(this).getUser();
        if (user == null || user.getToken() == null || user.getToken().isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        token = user.getToken();
        requestService = ApiUtils.getRequestService(token);

        loadUserRequests();
    }

    private void loadUserRequests() {
        Log.d("AUTH_TOKEN", "Bearer " + token);

        requestService.getMyRequests("Bearer " + token).enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> requestList = response.body();
                    adapter = new UserRequestAdapter(ViewRequestsActivity.this, requestList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ViewRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    Log.e("REQUEST_ERROR", "Code: " + response.code());
                    try {
                        Log.e("REQUEST_ERROR", "Error Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("REQUEST_ERROR", "Error reading errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(ViewRequestsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("REQUEST_FAIL", "Throwable: ", t);
            }
        });
    }
}
