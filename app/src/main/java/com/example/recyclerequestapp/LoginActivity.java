package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.model.LoginResponse;
import com.example.recyclerequestapp.model.User; // Ensure this import is correct
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔒 Auto-login check
        SharedPrefManager spm = SharedPrefManager.getInstance(this); // Get instance early
        if (spm.isLoggedIn()) {
            User loggedInUser = spm.getUser();
            if (loggedInUser != null) {
                // Redirect directly without showing login screen if already logged in
                redirectBasedOnRole(loggedInUser.getRole());
                return; // Important: exit onCreate after redirection
            } else {
                // Fallback for corrupted shared prefs, clear and force login
                Toast.makeText(this, "Session data corrupted, please log in again.", Toast.LENGTH_LONG).show();
                spm.logout();
            }
        }

        etUsername = findViewById(R.id.edtUsername);
        etPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            userService.login(usernameOrEmail, password).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        // Create a User object from the LoginResponse data
                        User user = new User(); // Use the no-arg constructor
                        user.setId(loginResponse.getUserId()); // Assuming getUserId() from LoginResponse maps to User's id
                        user.setUsername(loginResponse.getUsername() != null ? loginResponse.getUsername() : usernameOrEmail); // Get from response or input
                        user.setEmail(loginResponse.getEmail() != null ? loginResponse.getEmail() : usernameOrEmail); // Get from response or input
                        user.setToken(loginResponse.getToken());
                        user.setRole(loginResponse.getRole());
                        // Set other fields from loginResponse if available and needed in User model
                        // user.setLease(loginResponse.getLease());
                        // user.setIs_active(loginResponse.getIs_active());
                        // user.setSecret(loginResponse.getSecret());

                        // Save the complete User object to SharedPreferences
                        spm.userLogin(user);

                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Log.d("TOKEN_DEBUG", "Login success. Received token: " + loginResponse.getToken());

                        Log.d("TOKEN_DEBUG", "SharedPrefManager stored token: " + spm.getUser().getToken());
                        // Redirect based on the role retrieved from the login response
                        redirectBasedOnRole(user.getRole());

                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: Invalid credentials or server error.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // New method for role-based redirection
    private void redirectBasedOnRole(String role) {
        Intent intent;
        if (role == null) {
            Toast.makeText(LoginActivity.this, "Role not found. Redirecting to user dashboard.", Toast.LENGTH_LONG).show();
            intent = new Intent(LoginActivity.this, UserDashboardActivity.class); // Default if role is missing
        } else if ("admin".equalsIgnoreCase(role)) { // Check if role is "admin" (case-insensitive)
            // Assuming ViewAllRequestsActivity is your admin's landing page
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
        } else if ("user".equalsIgnoreCase(role) || "customer".equalsIgnoreCase(role)) { // Check for user/customer role
            intent = new Intent(LoginActivity.this, UserDashboardActivity.class); // User's landing page
            Toast.makeText(LoginActivity.this, "Welcome User!", Toast.LENGTH_SHORT).show();
        } else {
            // Default or error case: unrecognized role string
            Toast.makeText(LoginActivity.this, "Unrecognized role. Redirecting to user dashboard.", Toast.LENGTH_LONG).show();
            intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}