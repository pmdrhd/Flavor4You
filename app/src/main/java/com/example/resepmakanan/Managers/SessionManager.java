package com.example.resepmakanan.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private final String PREF_NAME = "user_session";
    private final String KEY_ID = "id";
    private final String KEY_USERNAME = "username";
    private final String KEY_EMAIL = "email";
    private final String KEY_LOGIN = "isLoggedIn";

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUser(int id, String username, String email) {
        editor.putBoolean(KEY_LOGIN, true);
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGIN, false);
    }

    public int getId() { return prefs.getInt(KEY_ID, -1); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
