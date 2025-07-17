package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclerequestapp.model.User;
import com.example.recyclerequestapp.remote.ApiUtils;
import com.example.recyclerequestapp.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private RadioGroup roleRadioGroup;
    private Button btnLogin;
    private TextView textViewRegister;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link components
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        btnLogin = findViewById(R.id.btnLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(v -> loginClicked());
    }

    private void loginClicked() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<User> call = userService.login(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    if (selectedId == R.id.radioUser) {
                        startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                        Toast.makeText(LoginActivity.this, "Logged in as User", Toast.LENGTH_SHORT).show();
                    } else if (selectedId == R.id.radioAdmin) {
                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        Toast.makeText(LoginActivity.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                    }

                    // Optionally: Save token using SharedPreferences here
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
