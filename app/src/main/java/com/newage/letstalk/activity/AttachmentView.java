package com.newage.letstalk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

//import com.esafirm.imagepicker.model.Image;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.model.Image;
import com.newage.letstalk.R;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class AttachmentView extends AppCompatActivity {
    EmojiconEditText mMsgEditText;
    List<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_view);

        LinearLayout linearLayout = findViewById(R.id.container);

        LinearLayout input = findViewById(R.id.form);
        ImageButton emoji = findViewById(R.id.emoji_btn);
        ImageButton send = findViewById(R.id.sendMessageButton);
        mMsgEditText = findViewById(R.id.messageEditText);

        images = getIntent().getParcelableArrayListExtra("images");
        for (Image image : images) {
            ImageView imageView = new ImageView(this);
            Glide.with(imageView).load(image.getPath()).into(imageView);

//            GlideApp.with(this).load(bitmap)
//                    .placeholder(R.drawable.ic_image_placeholder)
//                    .error(R.drawable.ic_broken_image)
//                    .into(imageView);

            linearLayout.addView(imageView);
        }


        EmojIconActions emojIcon = new EmojIconActions(this, findViewById(android.R.id.content), mMsgEditText, emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_keyboard, R.drawable.ic_insert_emoticon);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("TAG", "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("TAG", "Keyboard closed");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult();
            }
        });
    }


    @Override
    public void onBackPressed() {
        returnNoResult();
    }

    private void returnResult() {
        Intent intent = new Intent();
        intent.putExtra("message", mMsgEditText.getText().toString());
        intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) images);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void returnNoResult() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

}