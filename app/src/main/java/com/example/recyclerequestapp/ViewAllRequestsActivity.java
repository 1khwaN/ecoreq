package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.adapter.RequestAdapter;
import com.example.recyclerequestapp.model.Request;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.RequestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRequests;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;
    private RequestService requestService;

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

        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestList, new RequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Request request) {
                Intent intent = new Intent(ViewAllRequestsActivity.this, RequestDetailsActivity.class);
                intent.putExtra("REQUEST_ID", request.getRequestId());
                startActivity(intent);
            }
        });
        recyclerViewRequests.setAdapter(requestAdapter);

        requestService = ApiUtils.getRequestService();

        fetchRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRequests(); // Refresh the list when returning from RequestDetailsActivity
    }

    private void fetchRequests() {
        requestService.getAllRequests().enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> fetchedRequests = response.body();
                    requestAdapter.setRequestList(fetchedRequests);
                    Log.d("ViewAllRequests", "Requests fetched successfully: " + fetchedRequests.size());
                } else {
                    Toast.makeText(ViewAllRequestsActivity.this, "Failed to fetch requests: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("ViewAllRequests", "Error response: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(ViewAllRequestsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ViewAllRequests", "Network error fetching requests", t);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}