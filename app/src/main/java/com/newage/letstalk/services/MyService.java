package com.newage.letstalk.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.newage.letstalk.R;
import com.newage.letstalk.activity.Register;


public class MyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Timer t = new Timer();
//        t.scheduleAtFixedRate(
//                new TimerTask() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 0, 5000);
//        timer.scheduleAtFixedRate(updateProfile, 0, 10000);

        return super.onStartCommand(intent, flags, startId);
    }

    public void addNotification() {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("you have a New message")
                        .setContentText("this is the notification")
                        //adding sound to notification
                        .setSound(soundUri);

        Intent notificationIntent = new Intent(this, Register.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}