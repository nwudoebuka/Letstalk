package com.newage.letstalk.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import com.newage.letstalk.HttpParse;
import com.newage.letstalk.R;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.activity.viewmodel.MessageViewModel;
import com.newage.letstalk.adapter.ChatAdapter;
import com.newage.letstalk.dataLayer.local.tables.ChatList;
import com.newage.letstalk.dataLayer.local.tables.Messages;
import com.newage.letstalk.xmpp.XmppConnectionService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class Chat3 extends AppCompatActivity {
    private static final String TAG = Chat3.class.getSimpleName();
    private RecyclerView recyclerView;
    private EmojiconEditText mMsgEditText;
    private ImageButton send;
    private TextView info;
    private ImageButton audio;
    private String contactJid, myJid;
    private String HTTP_JSON_URL;
    private SessionManager session;
    private ChatAdapter adapter;
    private ChatList chatList;
    //private BroadcastReceiver mBroadcastReceiver;
    private static final int MULTIPLE_PERMISSIONS = 12;
    private int ACTION = 0;
    private MediaRecorder mediaRecorder;
    private String audioSavePathInDevice;
    private boolean isRecording = false;
    private String ServerUploadPath;
    private ArrayList<Image> images = new ArrayList<>();
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private ImageButton emoji;
    private ImageButton attachment;
    private MessageViewModel viewModel;
    private HttpParse httpParse = new HttpParse();
    private String HttpURL;
    private String upLoadServerUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Toolbar toolbar = findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        createAudioStore();
        createImageStore();

        recyclerView = findViewById(R.id.msgListView);
        mMsgEditText = findViewById(R.id.messageEditText);
        send = findViewById(R.id.sendMessageButton);
        info = findViewById(R.id.info);

        emoji = findViewById(R.id.emoji_btn);
        attachment = findViewById(R.id.pick_attachment);
        audio = findViewById(R.id.pick_audio);
        LinearLayout input = findViewById(R.id.form);

        session = new SessionManager(getBaseContext());

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            chatList = (ChatList) intent.getSerializableExtra("chatList");
            contactJid = chatList.getPhone();
            myJid = session.getPhoneNumber();
        }

        //setTitle(contactJid);
        //startService();

        mMsgEditText.addTextChangedListener(onTextChangedListener());

        EmojIconActions emojIcon = new EmojIconActions(this, findViewById(android.R.id.content), mMsgEditText, emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_insert_emoticon);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ACTION = 1;
                if (checkPermissions()) {
                    if (isRecording) {
                        enableAllViews();
                        sendAudio();
                    } else {
                        recordAudio();
                    }
                }
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ACTION = 2;
                if (checkPermissions()) {
                    pickImage();
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (TextUtils.isEmpty(mMsgEditText.getText().toString())){
                //    Toast.makeText(getBaseContext(), "Hold to record, release to send", Toast.LENGTH_SHORT).show();
                //}else {
                sendTextMessage(v);
                // }
            }
        });
        send.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sendTextMessage(v);
                return true;
            }
        });

        adapter = new ChatAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        /* if chating with bot*/
        if (TextUtils.equals(contactJid, "bot")) {
            input.setVisibility(View.GONE);
            Messages chat = new Messages();
            chat.setRemoteId(contactJid);
            chat.setMyId(myJid);
            chat.setFromMe(false);
            chat.setStatus(0);
            chat.setPushNeeded(false);
            chat.setDate(new Date());
            chat.setData("Welcome to Letstalk");
            adapter.swapItem(chat);
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }else {
            viewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
            viewModel.getMessages(contactJid);
            viewModel.getMessagesList().observe(this, new Observer<List<Messages>>() {
                @Override
                public void onChanged(List<Messages> messages) {
                    adapter.swapItems(messages);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                }
            });

            HTTP_JSON_URL = "https://globeexservices.com/letstalk/messages.php/?frnd=" + contactJid + "&user=" + myJid + "";
            HttpURL = "https://globeexservices.com/letstalk/sendmessage.php/?frnd=" + contactJid + "&user=" + myJid + "";
            upLoadServerUri = "https://globeexservices.com/letstalk/audiomessages/audio.php/?user=" + myJid + "&frnd=" + contactJid + "";
            ServerUploadPath = "https://globeexservices.com/letstalk/sendimage.php";
        }
    }

    public void sendTextMessage(View v) {
        String message = mMsgEditText.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            mMsgEditText.setText("");

            Messages chat = new Messages();
            chat.setRemoteId(contactJid);
            chat.setMyId(myJid);
            chat.setFromMe(true);
            chat.setStatus(0);
            chat.setPushNeeded(true);
            chat.setDate(new Date());
            chat.setData(message);

            viewModel.saveMessage(chat); //save on local db
            sendTextRemote(chat); //send to server

            // perform actual message sending
//            if (XmppConnectionService.getState().equals(XmppConnection.ConnectionState.CONNECTED)) {
//                Log.d(TAG, "The client is connected to the server, Sending Message");
//                //Send the message to the server
//                Intent intent = new Intent(XmppConnectionService.SEND_MESSAGE);
//                intent.putExtra(XmppConnectionService.BUNDLE_MESSAGE_BODY, message);
//                intent.putExtra(XmppConnectionService.BUNDLE_TO, contactJid);
//                sendBroadcast(intent);
//            } else {
//               // Toast.makeText(getApplicationContext(), "Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
//            }
        }
    }

    private void sendTextRemote(Messages messages) {
        SendMessageClass sendMessageClass = new SendMessageClass();
        sendMessageClass.execute(messages);
    }

    private void sendAudio() {
        if (!TextUtils.isEmpty(audioSavePathInDevice)) {
            Messages chat = new Messages();
            chat.setRemoteId(contactJid);
            chat.setMyId(myJid);
            chat.setFromMe(true);
            chat.setStatus(0);
            chat.setPushNeeded(true);
            chat.setDate(new Date());
            chat.setAudio(audioSavePathInDevice);

            viewModel.saveMessage(chat); //save on local db
            sendAudioRemote(chat); //send to server
        }
    }

    public void sendAudioRemote(Messages messages) {
        SendAudioClass sendAudioClass = new SendAudioClass();
        sendAudioClass.execute(messages);
    }

    public void sendImageMessage(String caption, List<Image> images) {
        //String image = bitmapToBase64(BitmapFactory.decodeFile(images.get(0).getPath()));

        Messages chat = new Messages();
        chat.setRemoteId(contactJid);
        chat.setMyId(myJid);
        chat.setFromMe(true);
        chat.setStatus(0);
        chat.setPushNeeded(true);
        chat.setDate(new Date());
        chat.setData(caption);
        chat.setImage(images.get(0).getPath());

        viewModel.saveMessage(chat); //save on local db
        sendImageRemote(chat); //send to server
    }

    public void sendImageRemote(Messages messages) {
        SendImageClass sendImageClass = new SendImageClass();
        sendImageClass.execute(messages);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @SuppressLint("StaticFieldLeak")
    class SendMessageClass extends AsyncTask<Messages, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Messages... params) {
            Messages messages = params[0];

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("messages", messages.getData());
            hashMap.put("sender", messages.getMyId());
            hashMap.put("reciever", messages.getRemoteId());

            return httpParse.postRequest(HttpURL, hashMap);
        }

        @Override
        protected void onPostExecute(String httpResponseMsg) {
            super.onPostExecute(httpResponseMsg);
            // JSON_HTTP_CALL2();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SendImageClass extends AsyncTask<Messages, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String string1) {
            super.onPostExecute(string1);
        }

        @Override
        protected String doInBackground(Messages... params) {
            Messages messages = params[0];

            Bitmap bitmap = BitmapFactory.decodeFile(messages.getImage());
            final String ConvertImage = bitmapToBase64(bitmap);

            HashMap<String, String> HashMapParams = new HashMap<String, String>();
            HashMapParams.put("image_name", messages.getMyId());
            HashMapParams.put("imgstatus", messages.getRemoteId());
            HashMapParams.put("image_path", ConvertImage);

            return imageHttpRequest(ServerUploadPath, HashMapParams);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SendAudioClass extends AsyncTask<Messages, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected Boolean doInBackground(Messages... params) {
            Messages messages = params[0];

            try {
                String sourceFileUri = messages.getAudio();
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {
                    try {

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(upLoadServerUri);
                        // Open a HTTP connection to the URL
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("bill", sourceFileUri);

                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\"" + sourceFileUri + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        if (serverResponseCode == 200) {
                            // recursiveDelete(mDirectory1);
                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } // End else block
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //JSON_HTTP_CALL(1);
        }

    }


    private String imageHttpRequest(String requestURL, HashMap<String, String> PData) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(requestURL);
            HttpURLConnection httpURLConnectionObject = (HttpURLConnection) url.openConnection();
            httpURLConnectionObject.setReadTimeout(19000);
            httpURLConnectionObject.setConnectTimeout(19000);
            httpURLConnectionObject.setRequestMethod("POST");
            httpURLConnectionObject.setDoInput(true);
            httpURLConnectionObject.setDoOutput(true);

            OutputStream OutPutStream = httpURLConnectionObject.getOutputStream();
            BufferedWriter bufferedWriterObject = new BufferedWriter(new OutputStreamWriter(OutPutStream, "UTF-8"));
            bufferedWriterObject.write(bufferedWriterDataFN(PData));
            bufferedWriterObject.flush();
            bufferedWriterObject.close();
            OutPutStream.close();

            int RC = httpURLConnectionObject.getResponseCode();

            if (RC == HttpsURLConnection.HTTP_OK) {
                BufferedReader bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                String RC2;
                while ((RC2 = bufferedReaderObject.readLine()) != null) {
                    stringBuilder.append(RC2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
        StringBuilder stringBuilderObject;
        stringBuilderObject = new StringBuilder();
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


    private void recordAudio() {
        if (createAudioStore()) {
            String filelocalName = new SimpleDateFormat("ddMMyyhhmmssSS", Locale.getDefault()).format(new Date()) + ".3gp";
            audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LetsTalk/Media/Audio/" + filelocalName;

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(audioSavePathInDevice);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                disableAllViews();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Something is wrong", Toast.LENGTH_LONG).show();
                enableAllViews();
            }
        }
    }

    private void disableAllViews() {
        info.setVisibility(View.VISIBLE);
        info.setText("Recording in progress...");
        audio.setBackgroundResource(R.drawable.ic_mic_off);
        isRecording = true;

        emoji.setEnabled(false);
        attachment.setEnabled(false);
        send.setEnabled(false);
        mMsgEditText.setEnabled(false);
    }

    private void enableAllViews() {
        if (mediaRecorder != null && isRecording) {
            mediaRecorder.stop();
        }
        info.setVisibility(View.GONE);
        audio.setBackgroundResource(R.drawable.ic_mic_on);
        isRecording = false;

        emoji.setEnabled(true);
        attachment.setEnabled(true);
        send.setEnabled(true);
        mMsgEditText.setEnabled(true);
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mMsgEditText.removeTextChangedListener(this);
                try {
                    if (TextUtils.isEmpty(s.toString())) {
                        //send.setBackgroundResource(R.drawable.ic_mic_on);
                    } else {
                        //send.setBackgroundResource(R.drawable.send_button);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMsgEditText.addTextChangedListener(this);
            }
        };
    }

    private boolean createAudioStore() {
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), "/LetsTalk/Media/Audio");
        if (!fullCacheDir.exists()) {
            return fullCacheDir.mkdirs();
        }

        return true;
    }

    private boolean createImageStore() {
        File fullCacheDir = new File(Environment.getExternalStorageDirectory().toString(), "/LetsTalk/Media/Image");
        if (!fullCacheDir.exists()) {
            return fullCacheDir.mkdirs();
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  unregisterReceiver(mBroadcastReceiver);
        if (mediaRecorder != null) {
            enableAllViews();
            mediaRecorder.release();
        }
        adapter.releaseResources();
    }

    @Override
    public void onBackPressed() {
        adapter.releaseResources();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                assert action != null;
//                switch (action) {
//                    case XmppConnectionService.NEW_MESSAGE:
//                        String from = intent.getStringExtra(XmppConnectionService.BUNDLE_FROM_JID);
//                        String body = intent.getStringExtra(XmppConnectionService.BUNDLE_MESSAGE_BODY);
//
////                        if (from.equals(contactJid)) {
//
////                        } else {
////                            Log.d(TAG, "Got a message from jid :" + from);
////                        }
//                        break;
//                    case XmppConnectionService.UI_AUTHENTICATED:
//                        Log.d(TAG, "Got a broadcast to show the main app window");
//                        //Show the main app window
//                        //showProgress(false);
//                        //Intent i2 = new Intent(mContext,ContactListActivity.class);
//                        //startActivity(i2);
//                        //finish();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//
//        //IntentFilter filter = new IntentFilter(XmppConnectionService.UI_AUTHENTICATED);
//        IntentFilter filter = new IntentFilter(XmppConnectionService.NEW_MESSAGE);
//        registerReceiver(mBroadcastReceiver, filter);
//
    }

    public boolean checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getBaseContext()), p) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (ACTION == 1) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getBaseContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getBaseContext()),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    recordAudio();
                }
            } else if (ACTION == 2) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getBaseContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getBaseContext()),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //ImagePicker.cameraOnly().start(this);
                    pickImage();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            Intent intent = new Intent(this, AttachmentView.class);
            intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) images);
            startActivityForResult(intent, 123);
            return;
        }

        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null) {
            List<Image> images = data.getParcelableArrayListExtra("images");
            String caption = data.getStringExtra("message");
            sendImageMessage(caption, images);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pickImage() {
        final boolean folderMode = true;
        final boolean includeVideo = false;
        final boolean showCamera = true;

        ImagePicker imagePicker = ImagePicker.create(this)
                .language("in") // Set image picker language
                .returnMode(ReturnMode.ALL) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                .folderMode(folderMode) // set folder mode (false by default)
                .includeVideo(includeVideo) // include video (false by default)
                .toolbarArrowColor(Color.WHITE) // set toolbar arrow up color
                .toolbarFolderTitle("Send to " + contactJid) // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarDoneButtonText("DONE"); // done button text

        imagePicker.single();
        //imagePicker.multi(); // multi mode (default mode)
        imagePicker.exclude(images); // don't show anything on this selected images
        imagePicker.limit(2) // max images can be selected (99 by default)
                .showCamera(showCamera) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                .start();
    }


    private void startService() {
        //Start the service
        Intent i1 = new Intent(this, XmppConnectionService.class);
        startService(i1);
    }

    //Check if service is running.
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.popup_menu_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

//        if (id == R.id.setautoresponse) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(Chat2.this);
//            builder.setTitle("Auto-response");
//            builder.setMessage("set a message as auto-responder");
//            final EditText input = new EditText(Chat2.this);
//            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
//            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
//            builder.setView(input);
//            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    m_Text = input.getText().toString();
//                    UserRegisterFunctionauto(nameofuser, m_Text);
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.setCancelable(false);
//            builder.show();
//            return true;
//        }else  if (id == R.id.unsetautoresponse) {
//           // UserRegisterFunctiondisauto(nameofuser);
//        }
    }

}