package com.newage.letstalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.newage.letstalk.SessionManager;

/**
 * Created by Newage_android on 5/1/2018.
 */
public class Launch extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        //session.checkLogin();

        Intent i;

        if(session.isLoggedIn()){
            i = new Intent(this, Dashboard.class);
        }else{
            i = new Intent(this, Splashscreen.class);
        }

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        startActivity(i);
        finish();
    }
}