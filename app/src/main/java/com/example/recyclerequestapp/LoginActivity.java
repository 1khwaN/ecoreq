package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.recyclerequestapp.SharedPrefManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Handle window insets for immersive layout (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        // Check if user is already logged in
        SharedPrefManager spm = SharedPrefManager.getInstance(this);
        if (spm.isLoggedIn()) {
            User user = spm.getUser();
            String role = user.getRole();

            Intent intent;
            if ("admin".equalsIgnoreCase(role)) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(this, UserDashboardActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Optional to prevent back navigation
        }
    }

    public void loginClicked(View view) {
        Log.d("LoginActivity", "Login button clicked");

        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (validateLogin(username, password)) {
            Log.d("LoginActivity", "Username and password validated");
            doLogin(username, password);
        }
    }


    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();

        Call<User> call = userService.login(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    if (user.getToken() != null) {
                        SharedPrefManager spm = SharedPrefManager.getInstance(LoginActivity.this);
                        spm.userLogin(user); // Correct method name

                        displayToast("Login successful");

                        Intent intent;
                        if ("admin".equalsIgnoreCase(user.getRole())) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        displayToast("Login failed: Token missing.");
                    }
                } else {
                    displayToast("Login failed: Invalid credentials.");
                }
                Log.d("LoginActivity", "Attempting login for: " + username);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Error connecting to server.");
                Log.e("LoginError", t.getMessage(), t);
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if (username.isEmpty()) {
            displayToast("Username is required.");
            return false;
        }
        if (password.isEmpty()) {
            displayToast("Password is required.");
            return false;
        }
        return true;
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}