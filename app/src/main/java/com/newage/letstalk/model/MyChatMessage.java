package com.newage.letstalk.model;

import android.graphics.Bitmap;

import com.newage.letstalk.interfaces.ChatMessage;
import com.newage.letstalk.utils.Utility;

public class MyChatMessage implements ChatMessage {
    private final String date, time;
    private final String sender;
    private String messageText;
    private String messageImage;
    private String messageAudio;
    private String messageVideo;
    private Bitmap bitmap;

    public MyChatMessage() {
        this.date = Utility.getCurrentDate();
        this.time = Utility.getCurrentDate();
        this.sender = "Me";
    }

    public MyChatMessage(String sender) {
        this.date = Utility.getCurrentDate();
        this.time = Utility.getCurrentDate();
        this.sender = sender;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    public void setMessageAudio(String messageAudio) {
        this.messageAudio = messageAudio;
    }

    public void setMessageVideo(String messageVideo) {
        this.messageVideo = messageVideo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageImage() {
        return messageImage;
    }

    public String getMessageAudio() {
        return messageAudio;
    }

    public String getMessageVideo() {
        return messageVideo;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getChatViewType() {
        return ChatMessage.TYPE_ME;
    }

}