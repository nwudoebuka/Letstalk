package com.newage.letstalk;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Newage_android on 5/2/2018.
 */



public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, MyService.class));

    }
}
