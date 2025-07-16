package com.example.recyclerequestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private RadioGroup roleRadioGroup;
    private RadioButton radioUser, radioAdmin;
    private Button btnLogin;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Link XML components to Java
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);
        btnLogin = findViewById(R.id.btnLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        // 2. Login Button Click
        btnLogin.setOnClickListener(v -> loginClicked());
    }

    // 3. Called when Login button is clicked
    public void loginClicked() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Input Validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check role
        if (selectedId == R.id.radioUser) {
            // Simulate login success → Redirect to UserDashboard
            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logged in as User", Toast.LENGTH_SHORT).show();

        } else if (selectedId == R.id.radioAdmin) {
            // Simulate login success → Redirect to AdminDashboard
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
        }
    }
}
