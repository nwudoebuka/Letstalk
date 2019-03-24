package com.newage.letstalk;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newage.letstalk.activity.AttachmentView;
import com.newage.letstalk.adapter.ChatAdapter;
import com.newage.letstalk.dataLayer.local.tables.Friend;
import com.newage.letstalk.interfaces.ChatMessage;
import com.newage.letstalk.model.FriendChatMessage;
import com.newage.letstalk.model.MyChatMessage;
//import com.newage.letstalk.utils.ImagePicker;
import com.newage.letstalk.xmpp.XmppConnection;
import com.newage.letstalk.xmpp.XmppConnectionService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    String contactJid;
    String nameofuser;
    SessionManager session;
    String Message_Holder;
    ChatAdapter adapter;
    Friend friend;

    private BroadcastReceiver mBroadcastReceiver;
    private static final int MULTIPLE_PERMISSIONS = 12;
    private int ACTION = 0;
    private MediaRecorder mediaRecorder;
    private String audioSavePathInDevice;
    private boolean isRecording = false;

    private static final int IMAGE_REQUEST_CODE = 12340;
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    //private ArrayList<Image> images = new ArrayList<>();

    ImageButton emoji;
    ImageButton attachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        //ImagePicker.setMinQuality(600, 600);

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

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            friend = (Friend) intent.getSerializableExtra("friend");
            contactJid = friend.getPhone();
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
                    //ImagePicker.pickImage(Chat3.this, "Select your image", IMAGE_REQUEST_CODE, false);
                    //ImagePicker.cameraOnly().start(Chat3.this);
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


        session = new SessionManager(getBaseContext());
        nameofuser = session.getPhoneNumber();

        adapter = new ChatAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        //linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        /* if chating with bot*/
        if (TextUtils.equals(friend.getPhone(), "bot")) {
            input.setVisibility(View.GONE);

            FriendChatMessage chatMessage = new FriendChatMessage(friend.getPhone());
            chatMessage.setMessageText("Welcome to letstalk");
            adapter.swapItem(chatMessage);
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    public void sendTextMessage(View v) {
        String message = mMsgEditText.getEditableText().toString();

        if (!TextUtils.isEmpty(message)) {
            mMsgEditText.setText("");

            MyChatMessage chat = new MyChatMessage();
            chat.setMessageText(message);
            adapter.addItem(chat);
            recyclerView.smoothScrollToPosition(adapter.getItemCount());

            // perform actual message sending
            if (XmppConnectionService.getState().equals(XmppConnection.ConnectionState.CONNECTED)) {
                Log.d(TAG, "The client is connected to the server, Sending Message");

                //Send the message to the server
                Intent intent = new Intent(XmppConnectionService.SEND_MESSAGE);
                intent.putExtra(XmppConnectionService.BUNDLE_MESSAGE_BODY, message);
                intent.putExtra(XmppConnectionService.BUNDLE_TO, contactJid);
                sendBroadcast(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
            }
            //message sending ends here
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

    private void sendAudio() {
        if (!TextUtils.isEmpty(audioSavePathInDevice)) {
            MyChatMessage chat = new MyChatMessage();
            chat.setMessageAudio(audioSavePathInDevice);
            adapter.addItem(chat);
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    public void sendImageMessage(String caption, String imageUri, Bitmap bitmap) {
        MyChatMessage chat = new MyChatMessage();
        chat.setMessageText(caption);
        chat.setBitmap(bitmap);
        chat.setMessageImage(imageUri);
        adapter.addItem(chat);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
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
        unregisterReceiver(mBroadcastReceiver);
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

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                switch (action) {
                    case XmppConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(XmppConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(XmppConnectionService.BUNDLE_MESSAGE_BODY);

//                        if (from.equals(contactJid)) {
                        FriendChatMessage chatMessage = new FriendChatMessage(from);
                        chatMessage.setMessageText(body);
                        adapter.addItem(chatMessage);
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
//                        } else {
//                            Log.d(TAG, "Got a message from jid :" + from);
//                        }
                        break;
                    case XmppConnectionService.UI_AUTHENTICATED:
                        Log.d(TAG, "Got a broadcast to show the main app window");
                        //Show the main app window
                        //showProgress(false);
                        //Intent i2 = new Intent(mContext,ContactListActivity.class);
                        //startActivity(i2);
                        //finish();
                        break;
                    default:
                        break;
                }
            }
        };

        //IntentFilter filter = new IntentFilter(XmppConnectionService.UI_AUTHENTICATED);
        IntentFilter filter = new IntentFilter(XmppConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);
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
                    //ImagePicker.pickImage(this, "Select your image", IMAGE_REQUEST_CODE, false);
                    //ImagePicker.cameraOnly().start(this);

                    pickImage();

                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
       //     images = (ArrayList<Image>) ImagePicker.getImages(data);

            //Intent intent = new Intent(getBaseContext(), AttachmentView.class);
            //intent.putExtra("hello", "hello");
            //intent.putExtra("imageArray", byteArray);
            //intent.putExtra("image", images.get(0));
            //startActivityForResult(intent, 123);

       //     AttachmentView.start(this, images);
       //     return;
       // }


//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == IMAGE_REQUEST_CODE) {
////                Bitmap bitmap = ImagePicker.getImageFromResult(getBaseContext(), requestCode, resultCode, data);
////                if (bitmap != null) {
////
////                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////                    byte[] byteArray = stream.toByteArray();
////
////                    Intent intent = new Intent(getBaseContext(), AttachmentView.class);
////                    //intent.putExtra("hello", "hello");
////                    intent.putExtra("imageArray", byteArray);
////                    startActivityForResult(intent, 123);
////                }
//
//
//            } else if (requestCode == 123) {
//                Bitmap bitmap = data.getParcelableExtra("bitmap");
//                String caption = data.getStringExtra("message");
//                String imageUri = data.getStringExtra("imageUri");
//                sendImageMessage(caption, imageUri, bitmap);
//            }
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void pickImage() {
        final boolean returnAfterCapture = true;
        final boolean isSingleMode = true;
        final boolean useCustomImageLoader = true;
        final boolean folderMode = true;
        final boolean includeVideo = true;
        final boolean isExclude = true;

//        ImagePicker imagePicker = ImagePicker.create(this)
//                .language("in") // Set image picker language
////                .theme(R.style.ImagePickerTheme)
//                .returnMode(returnAfterCapture ? ReturnMode.ALL : ReturnMode.NONE) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
//                .folderMode(folderMode) // set folder mode (false by default)
//                .includeVideo(includeVideo) // include video (false by default)
//                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
//                .toolbarFolderTitle("Folder") // folder selection title
//                .toolbarImageTitle("Tap to select") // image selection title
//                .toolbarDoneButtonText("DONE"); // done button text
//
//        if (useCustomImageLoader) {
////            imagePicker.imageLoader(new GrayscaleImageLoader());
//        }
//
//        if (isSingleMode) {
////            imagePicker.single();
//        } else {
////            imagePicker.multi(); // multi mode (default mode)
//        }
//
//        if (isExclude) {
////            imagePicker.exclude(images); // don't show anything on this selected images
//        } else {
////            imagePicker.origin(images); // original selected images, used in multi mode
//        }
//
//        imagePicker.limit(10) // max images can be selected (99 by default)
//                .showCamera(true) // show camera or not (true by default)
//                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
//                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()); // can be full path
//
//        imagePicker.start();
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