package com.newage.letstalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
            session.checkLogin();


    }
//    public void check() {
//
//        finish();
//        startActivity(new Intent(Launch.this, Splashscreen.class));
//    }


}
