package com.newage.letstalk.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.newage.letstalk.services.MyService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MyService.class);
        context.startService(startServiceIntent);
    }
}
