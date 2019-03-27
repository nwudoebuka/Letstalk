package com.newage.letstalk.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.newage.letstalk.R;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.activity.Dashboard;
import com.newage.letstalk.dataLayer.local.AppDatabase;
import com.newage.letstalk.dataLayer.local.tables.ChatList;
import com.newage.letstalk.dataLayer.local.tables.Messages;
import com.newage.letstalk.model.response.ChatModel;
import com.newage.letstalk.utils.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private AppDatabase database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        database = AppDatabase.getInstance(getApplicationContext());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.i("TAG", "running");

                String myJid = new SessionManager(getApplicationContext()).getPhoneNumber();

                if(!TextUtils.isEmpty(myJid)
                        && Utility.isNetworkAvailable(getApplicationContext())){
                    Log.i("TAG", "running found user");

                    List<ChatList> friends = database.getFriendDAO().getFriends();
                    for (ChatList friend : friends) {
                        if(!TextUtils.equals(friend.getPhone(), "bot")){
                            serve(myJid, friend.getPhone());
                        }
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 15000, 15000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void serve(String myJid, String contactJid) {
        String url = "https://globeexservices.com/letstalk/messages.php/?frnd=" + contactJid + "&user=" + myJid;
        new GetChatsClass().execute(url);
    }

    @SuppressLint("StaticFieldLeak")
    class GetChatsClass extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String value = httpRequest(url, "GET", null);

            if (!TextUtils.isEmpty(value) && !TextUtils.equals(value, "0 results")) {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<ArrayList<ChatModel>>() {}.getType();

                List<ChatModel> chatModels = gson.fromJson(value, type);

                for (ChatModel chat : chatModels) {
                    String data = chat.getData();
                    String sender = chat.getSender();
                    String receiver = chat.getReciever();
                    String audio = chat.getAudio();
                    String video = chat.getVideo();
                    String dp = chat.getDp();

                    if (!TextUtils.isEmpty(data)) {
                        Messages message = new Messages();
                        message.setRemoteId(sender);
                        message.setMyId(receiver);
                        message.setFromMe(false);
                        message.setStatus(0);
                        message.setPushNeeded(false);
                        message.setDate(new Date());

                        if (TextUtils.equals("audiomtgcora", data)) {
                            message.setAudio(audio.replaceAll("\\\\", ""));
                            addNotification(sender, "sent you an audio file");
                        } else if (TextUtils.equals("nothingmtgcora", data)) {
                            message.setImage(dp.replaceAll("\\\\", ""));
                            addNotification(sender, "sent you an image");
                        } else {
                            message.setData(data);
                            addNotification(sender, data);
                        }

                        database.getMessageDAO().insertItem(message); //save on local db
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);
        }

    }

    private String httpRequest(String requestURL, String requestMethod, HashMap<String, String> data) {
        HttpURLConnection httpURLConnectionObject = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStreamReader inputStreamReader = null;
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(requestURL);
            httpURLConnectionObject = (HttpURLConnection) url.openConnection();
            httpURLConnectionObject.setReadTimeout(19000);
            httpURLConnectionObject.setConnectTimeout(19000);
            httpURLConnectionObject.setRequestMethod(requestMethod.toUpperCase());
            httpURLConnectionObject.setDoInput(true);
            httpURLConnectionObject.setDoOutput(true);

            //For Post request
            if (httpURLConnectionObject.getRequestMethod().equalsIgnoreCase("POST")) {
                OutputStream outPutStream = httpURLConnectionObject.getOutputStream();
                outputStreamWriter = new OutputStreamWriter(outPutStream, StandardCharsets.UTF_8);
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(dataWriter(data));
                bufferedWriter.flush();
            }

            //For GET request and reading value of result for POST request
            InputStream inputStream = httpURLConnectionObject.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);

            if (httpURLConnectionObject.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }

                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }

                if (httpURLConnectionObject != null) {
                    httpURLConnectionObject.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private String dataWriter(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
        StringBuilder stringBuilderObject = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
            if (isFirst) isFirst = false;
            else stringBuilderObject.append("&");

            stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
            stringBuilderObject.append("=");
            stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
        }

        return stringBuilderObject.toString();
    }

    public void addNotification(String title, String content) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setSound(soundUri);

        Intent notificationIntent = new Intent(this, Dashboard.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}