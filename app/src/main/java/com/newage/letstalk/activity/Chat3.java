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
import com.newage.letstalk.Sendimage;
import com.newage.letstalk.SessionManager;
import com.newage.letstalk.activity.viewmodel.MessageViewModel;
import com.newage.letstalk.adapter.ChatAdapter;
import com.newage.letstalk.dataLayer.local.tables.ChatList;
import com.newage.letstalk.dataLayer.local.tables.Messages;
import com.newage.letstalk.xmpp.XmppConnectionService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class Chat3 extends AppCompatActivity {
    private static final String TAG = Sendimage.class.getSimpleName();
    RecyclerView recyclerView;
    EmojiconEditText mMsgEditText;
    ImageButton send;
    TextView info;
    ImageButton audio;

    String contactJid, myJid;
    String nameofuser;
    SessionManager session;
    ChatAdapter adapter;
    ChatList chatList;

  //  private BroadcastReceiver mBroadcastReceiver;
    private static final int MULTIPLE_PERMISSIONS = 12;
    private int ACTION = 0;
    private MediaRecorder mediaRecorder;
    private String audioSavePathInDevice;
    private boolean isRecording = false;

    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    private ArrayList<Image> images = new ArrayList<>();

    ImageButton emoji;
    ImageButton attachment;

    private MessageViewModel viewModel;

    HttpParse httpParse = new HttpParse();
    String HttpURL;

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
        nameofuser = session.getPhoneNumber();

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
        }

        viewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        viewModel.getMessages(contactJid);
        viewModel.getMessagesList().observe(this, new Observer<List<Messages>>() {
            @Override
            public void onChanged(List<Messages> messages) {
                adapter.swapItems(messages);
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });


        String HTTP_JSON_URL = "https://globeexservices.com/letstalk/Messages.php/?frnd="+contactJid+"&user="+myJid+"";
        HttpURL = "https://globeexservices.com/letstalk/sendmessage.php/?frnd="+contactJid+"&user="+myJid+"";
    }

    public void sendTextMessage(View v) {
        String message = mMsgEditText.getEditableText().toString();

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

    public void sendTextRemote(Messages messages){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Messages", messages.getData());
        hashMap.put("sender", messages.getMyId());
        hashMap.put("reciever", messages.getRemoteId());

        SendMessageClass sendMessageClass = new SendMessageClass();
        sendMessageClass.execute(hashMap);
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

    public void sendAudioRemote(Messages messages){
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("Messages", messages.getData());
//        hashMap.put("sender", messages.getMyId());
//        hashMap.put("reciever", messages.getRemoteId());
//
//        SendMessageClass sendMessageClass = new SendMessageClass();
//        sendMessageClass.execute(hashMap);
    }

    public void sendImageMessage(String caption, List<Image> images) {
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

    public void sendImageRemote(Messages messages){
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("Messages", messages.getData());
//        hashMap.put("sender", messages.getMyId());
//        hashMap.put("reciever", messages.getRemoteId());
//
//        SendMessageClass sendMessageClass = new SendMessageClass();
//        sendMessageClass.execute(hashMap);
    }

    @SuppressLint("StaticFieldLeak")
    class SendMessageClass extends AsyncTask<HashMap, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(HashMap... params) {
            HashMap<String, String> hashMap = (HashMap<String, String>) params[0];
            return httpParse.postRequest(hashMap, HttpURL);
        }
        @Override
        protected void onPostExecute(String httpResponseMsg) {
            super.onPostExecute(httpResponseMsg);
            // JSON_HTTP_CALL2();
        }
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