package com.newage.letstalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.newage.letstalk.adapter.ChatAdapter;
import com.newage.letstalk.interfaces.ChatMessage;
import com.newage.letstalk.model.FriendChatMessage;
import com.newage.letstalk.model.MyChatMessage;
import com.newage.letstalk.xmpp.XmppConnection;
import com.newage.letstalk.xmpp.XmppConnectionService;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class Chat3 extends AppCompatActivity {
    private static final String TAG = Sendimage.class.getSimpleName();
    RecyclerView recyclerView;
    EmojiconEditText mMsgEditText;

    String user, img, contactJid, nameofuser;
    SessionManager session;
    String Message_Holder;
    ChatAdapter adapter;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            user = intent.getStringExtra("user");
            img = intent.getStringExtra("img");
            contactJid = intent.getStringExtra("phone");
        }

        Toolbar toolbar = findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //setTitle(contactJid);

        recyclerView = findViewById(R.id.msgListView);
        mMsgEditText = findViewById(R.id.messageEditText);

        ImageButton emoji = findViewById(R.id.emoji_btn);
        ImageButton attachment = findViewById(R.id.pick_attachment);
        ImageButton audio = findViewById(R.id.pick_audio);

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

        findViewById(R.id.sendMessageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextMessage(v);
            }
        });
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


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
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

                    default:
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter(XmppConnectionService.NEW_MESSAGE);
        registerReceiver(mBroadcastReceiver, filter);
    }

}