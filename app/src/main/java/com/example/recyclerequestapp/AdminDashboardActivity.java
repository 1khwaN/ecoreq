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
    Button btnViewAllRequests, btnComplete, btnAddItem, btnUpdateItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Link buttons to layout (you must have these IDs in the XML)
        btnViewAllRequests = findViewById(R.id.btnViewAllRequests);
        btnComplete = findViewById(R.id.btnComplete);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);

        // Set OnClickListener for View All Requests button
        btnViewAllRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewAllRequestsActivity.class);
                startActivity(intent);
            }
        });

        // TODO: Add setOnClickListeners for other admin functions
        // Example for btnComplete (assuming it leads to an activity for completing requests)
        /*
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, CompleteRequestActivity.class);
                startActivity(intent);
            }
        });
        */
        // And similarly for btnAddItem and btnUpdateItem
    }

    // Inflate the menu (with Logout option)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu); // reuse user_menu
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // End current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}