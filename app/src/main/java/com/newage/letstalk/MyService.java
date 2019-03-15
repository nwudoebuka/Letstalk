package com.newage.letstalk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service {

//    Timer timer = new Timer();
//    TimerTask updateProfile = new CustomTimerTask(MyService.this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here


        //Declare the timer
        Timer t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
//                                      addNotification();
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                5000);


        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
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
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}



