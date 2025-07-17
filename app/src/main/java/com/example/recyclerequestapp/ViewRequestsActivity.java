// app/src/main/java/com/example/recyclerequestapp/ViewAllRequestsActivity.java

package com.example.recyclerequestapp; // Adjust your package name

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerequestapp.adapter.RequestAdapter; // Ensure this path is correct
import com.example.recyclerequestapp.remote.RequestService; // Ensure this path is correct
import com.example.recyclerequestapp.model.Request;   // Ensure this path is correct

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRequests;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;

    // IMPORTANT: Replace with your actual pRESTige base URL
    private static final String BASE_URL = "http://YOUR_SERVER_IP_OR_DOMAIN:YOUR_PORT/"; // e.g., http://192.168.1.100:3000/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests); // Link to the new layout

        // Optional: Add a back button to the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Requests");
        }

        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestList);
        recyclerViewRequests.setAdapter(requestAdapter);

        fetchRequests();
    }

    private void fetchRequests() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestService requestService = retrofit.create(RequestService.class);

        requestService.getAllRequests().enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Request> fetchedRequests = response.body();
                    // You can choose to filter requests here (e.g., only "Pending")
                    // Or display all for "View All Requests"
                    requestAdapter.setRequestList(fetchedRequests); // Display all fetched requests
                    Log.d("ViewAllRequests", "Requests fetched successfully: " + fetchedRequests.size());
                } else {
                    Toast.makeText(ViewRequestsActivity.this, "Failed to fetch requests: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("ViewAllRequests", "Error response: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(ViewRequestsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ViewAllRequests", "Network error fetching requests", t);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back arrow in the action bar
        finish();
        return true;
    }
}
