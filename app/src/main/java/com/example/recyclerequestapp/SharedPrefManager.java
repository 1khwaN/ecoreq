package com.example.recyclerequestapp; // Or com.example.recyclerequestapp.sharedpref if it's in a subpackage

import android.content.Context;
import android.content.SharedPreferences;

import com.example.recyclerequestapp.model.User; // Ensure correct import

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "MyPrefs";
    // Keys for SharedPreferences
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_ROLE = "key_role";
    // Add keys for other fields you might want to persist, e.g.:
    // private static final String KEY_LEASE = "key_lease";
    // private static final String KEY_IS_ACTIVE = "key_is_active";
    // private static final String KEY_SECRET = "key_secret";


    private static SharedPrefManager instance;
    private Context context;

    SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    // Method to save user data after successful login
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, user.getId()); // Use getId() from your User model
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        // Add other fields if you want to save them
        // editor.putString(KEY_LEASE, user.getLease());
        // editor.putInt(KEY_IS_ACTIVE, user.getIs_active());
        // editor.putString(KEY_SECRET, user.getSecret());

        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // A user is considered logged in if a token and role exist
        return sharedPreferences.getString(KEY_TOKEN, null) != null &&
                sharedPreferences.getString(KEY_ROLE, null) != null &&
                sharedPreferences.getInt(KEY_USER_ID, -1) != -1;
    }

    // Get the logged in user details
    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        int id = sharedPreferences.getInt(KEY_USER_ID, -1);
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        String role = sharedPreferences.getString(KEY_ROLE, null);
        // Retrieve other fields if saved
        // String lease = sharedPreferences.getString(KEY_LEASE, null);
        // int is_active = sharedPreferences.getInt(KEY_IS_ACTIVE, 0);
        // String secret = sharedPreferences.getString(KEY_SECRET, null);


        // Construct User object only if essential data is available
        if (id != -1 && email != null && username != null && token != null && role != null) {
            User user = new User(); // Assuming User has a no-arg constructor
            user.setId(id);
            user.setEmail(email);
            user.setUsername(username);
            user.setToken(token);
            user.setRole(role);
            // Set other fields if retrieved
            // user.setLease(lease);
            // user.setIs_active(is_active);
            // user.setSecret(secret);
            return user;
        }
        return null; // Return null if user data is incomplete or not found
    }

    // Logout user
    public void logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clears all data
        editor.apply();
    }
}