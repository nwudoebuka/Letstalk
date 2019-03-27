package com.newage.letstalk;

import android.content.Context;

import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private static final String PREF_NAME = "AndroidHivePref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_USER_STATUS = "user_status";

    public SessionManager(Context context){
        pref = context.getSharedPreferences(PREF_NAME, 0);
    }

    public void createLoginSession(String name, String phone){
        pref.edit().putBoolean(IS_LOGIN, true).apply();
        pref.edit().putString(KEY_NAME, name).apply();
        pref.edit().putString(KEY_PHONE, phone).apply();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setLoggedIn(boolean value){
        pref.edit().putBoolean(IS_LOGIN, value).apply();
    }

    public String getPhoneNumber(){ return pref.getString(KEY_PHONE, ""); }

    public String getUserName(){
        return pref.getString(KEY_NAME, "");
    }

    public String getUserStatus(){
        return pref.getString(KEY_USER_STATUS, "Available");
    }

    public void setUserSatus(String value){ pref.edit().putString(KEY_USER_STATUS, value).apply(); }

    public void logoutUser(){
        pref.edit().clear().apply();
    }
}