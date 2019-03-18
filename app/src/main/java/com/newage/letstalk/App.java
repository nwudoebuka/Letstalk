package com.newage.letstalk;

import android.app.Application;
import android.content.Intent;

import com.newage.letstalk.services.MyService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // startService(new Intent(this, MyService.class));
    }
}
