package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View; // Import View
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    // Declare buttons
    Button btnViewAllRequests, btnUpdateRequestStatus, btnAddRecyclableItem, btnUpdateRecyclableItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Link buttons with layout
        btnViewAllRequests = findViewById(R.id.btnViewAllRequests);
        btnUpdateRequestStatus = findViewById(R.id.btnUpdateRequestStatus);
        btnAddRecyclableItem = findViewById(R.id.btnAddRecyclableItem);
        btnUpdateRecyclableItem = findViewById(R.id.btnUpdateRecyclableItem);

        // Button: View All Requests
         btnViewAllRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewAllRequestsActivity.class);
                startActivity(intent);
            }
        });

        // Button: Update Request Status
        btnUpdateRequestStatus.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UpdateRequestStatusActivity.class);
            startActivity(intent);
        });

        // Button: Add New Recyclable Item
        btnAddRecyclableItem.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddRecyclableItemActivity.class);
            startActivity(intent);
        });

        // Button: Update Recyclable Item
        btnUpdateRecyclableItem.setOnClickListener(v -> {
            startActivity(intent);
        });
    }

    // Show 3-dot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu); // Load res/menu/admin_menu.xml
        return true;
    }

    // Handle menu actions (like logout)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Clear session
            SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
            spm.logout();

            // Redirect to login
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}