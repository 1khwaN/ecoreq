package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast; // Added for Toast messages

import androidx.annotation.Nullable; // Added for @Nullable annotation
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.SharedPrefManager;

public class AdminDashboardActivity extends AppCompatActivity {

    // Declare buttons
    Button btnViewAllRequests, btnAddRecyclableItem, btnUpdateRecyclableItem;

    // Define request codes for startActivityForResult
    private static final int ADD_ITEM_REQUEST_CODE = 1;
    private static final int MANAGE_ITEMS_REQUEST_CODE = 2; // For launching ManageRecyclableItemsActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Link buttons with layout
        btnViewAllRequests = findViewById(R.id.btnViewAllRequests);
        btnAddRecyclableItem = findViewById(R.id.btnAddRecyclableItem);
        btnUpdateRecyclableItem = findViewById(R.id.btnUpdateRecyclableItem);

        // Button: View All Requests (Navigates to a list of all requests)
        btnViewAllRequests.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ViewAllRequestsActivity.class);
            startActivity(intent); // No result expected here normally
        });

        // Button: Add New Recyclable Item (Navigates to a form to add new items)
        btnAddRecyclableItem.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddRecyclableItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE); // Use startActivityForResult
        });

        // Button: Update Recyclable Item (Navigates to a screen to modify existing items)
        // This should now launch ManageRecyclableItemsActivity
        btnUpdateRecyclableItem.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageRecyclableItemsActivity.class);
            startActivityForResult(intent, MANAGE_ITEMS_REQUEST_CODE); // Use startActivityForResult
        });
    }

    // Handle results from launched activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ITEM_REQUEST_CODE) {
                Toast.makeText(this, "New item added successfully!", Toast.LENGTH_SHORT).show();
                // If your dashboard displays a list of items, you might want to refresh it here
            } else if (requestCode == MANAGE_ITEMS_REQUEST_CODE) {
                // This indicates that ManageRecyclableItemsActivity completed,
                // likely after an item was updated.
                Toast.makeText(this, "Item management completed.", Toast.LENGTH_SHORT).show();
                // If your dashboard displays a list of items, you might want to refresh it here
            }
        } else if (resultCode == RESULT_CANCELED) {
            // Optional: Handle if the user cancelled the operation in the launched activity
            if (requestCode == ADD_ITEM_REQUEST_CODE) {
                Toast.makeText(this, "Add item cancelled.", Toast.LENGTH_SHORT).show();
            } else if (requestCode == MANAGE_ITEMS_REQUEST_CODE) {
                Toast.makeText(this, "Item management cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
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