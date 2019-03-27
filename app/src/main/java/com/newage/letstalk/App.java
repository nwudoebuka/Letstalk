package com.newage.letstalk;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //startService(new Intent(this, MyService.class));
    }
}
