package com.newage.letstalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.newage.letstalk.activity.Dashboard;
import com.newage.letstalk.activity.Splashscreen;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidHivePref";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String phone){
        pref.edit().putBoolean(IS_LOGIN, true).apply();
        pref.edit().putString(KEY_NAME, name).apply();
        pref.edit().putString(KEY_PHONE, phone).apply();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    @Deprecated
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Splashscreen.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }else{

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Dashboard.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

        }
    }


    /**
     * Get stored session data
     * */
    @Deprecated
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        pref.edit().clear().apply();
    }

    /**
     * Quick check for login
     * **/
    // Get login state
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    //Set login state
    public void setLoggedIn(boolean value){
        pref.edit().putBoolean(IS_LOGIN, value).apply();
    }

    public String getPhoneNumber(){
        return pref.getString(KEY_PHONE, "");
    }

}