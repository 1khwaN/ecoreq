package com.example.recyclerequestapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.recyclerequestapp.model.User;

public class SharedPrefManager {
    private static final String PREF_NAME = "recyclesharedpref";
    private static final String KEY_ID = "keyid",
            KEY_USERNAME = "keyusername",
            KEY_EMAIL = "keyemail",
            KEY_TOKEN = "keytoken",
            KEY_ROLE = "keyrole";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences prefs = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(KEY_ID, user.getId());
        e.putString(KEY_USERNAME, user.getUsername());
        e.putString(KEY_EMAIL, user.getEmail());
        e.putString(KEY_TOKEN, user.getToken());
        e.putString(KEY_ROLE, user.getRole());
        e.apply();
    }

    public User getUser() {
        SharedPreferences p = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        User u = new User();
        u.setId(p.getInt(KEY_ID, -1));
        u.setUsername(p.getString(KEY_USERNAME, null));
        u.setEmail(p.getString(KEY_EMAIL, null));
        u.setToken(p.getString(KEY_TOKEN, null));
        u.setRole(p.getString(KEY_ROLE, null));
        return u;
    }

    public void logout() {
        SharedPreferences.Editor e = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        e.clear().apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

}
