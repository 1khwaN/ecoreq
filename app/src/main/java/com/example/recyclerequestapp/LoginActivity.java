package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // ✅ Auto-login if user is already logged in
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
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
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }

    public void loginClicked(View view) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (validateLogin(username, password)) {
            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {
        UserService userService = ApiUtils.getUserService();
        Call<User> call = userService.login(username, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null && user.getToken() != null) {
                        // Save user session
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        displayToast("Login successful");

                        // Redirect based on role
                        String role = user.getRole();
                        Intent intent;

                        if ("admin".equalsIgnoreCase(role)) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        displayToast("Login failed: invalid user or token.");
                    }
                } else {
                    displayToast("Login failed: check your credentials.");
                }
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
