package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class UserDashboardActivity extends AppCompatActivity {

    // Declare buttons
    Button btnViewRequests, btnCancelRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Link buttons with layout
        btnViewRequests = findViewById(R.id.btnViewRequests);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);

        // View Requests
        btnViewRequests.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, ViewRequestsActivity.class);
            startActivity(intent);
        });

        // Cancel Request
        btnCancelRequest.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, CancelRequestActivity.class);
            startActivity(intent);
        });
    }

    // Show 3-dot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    // Handle logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // ✅ Clear session
            SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
            spm.logout();

            // Redirect to login
            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
