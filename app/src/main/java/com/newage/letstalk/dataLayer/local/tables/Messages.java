package com.newage.letstalk.dataLayer.local.tables;

import com.newage.letstalk.interfaces.ChatMessage;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public final class Messages implements ChatMessage, Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @Nullable @ColumnInfo(name = "key_remote_jid")
    private String remoteId;

    @Nullable @ColumnInfo(name = "key_id")
    private String myId;

    @ColumnInfo(name = "key_from_me")
    private boolean isFromMe;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "needs_push")
    private boolean isPushNeeded;

    @Nullable @ColumnInfo(name = "timestamp")
    private Date date;

    @Nullable @ColumnInfo(name = "data")
    private String data;

    @Nullable @ColumnInfo(name = "audio")
    private String audio;

    @Nullable @ColumnInfo(name = "image")
    private String image;







    @Nullable
    public String getAudio() {
        return audio;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public long getId() {
        return id;
    }


    @Nullable
    public String getRemoteId() {
        return remoteId;
    }

    @Nullable
    public String getMyId() {
        return myId;
    }

    public boolean isFromMe() {
        return isFromMe;
    }

    public int getStatus() {
        return status;
    }

    public boolean isPushNeeded() {
        return isPushNeeded;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    @Nullable
    public String getData() {
        return data;
    }


    public void setImage(@Nullable String image) {
        this.image = image;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public void setAudio(@Nullable String audio) {
        this.audio = audio;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRemoteId(@Nullable String remoteId) {
        this.remoteId = remoteId;
    }

    public void setMyId(@Nullable String myId) {
        this.myId = myId;
    }

    public void setFromMe(boolean fromMe) {
        isFromMe = fromMe;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPushNeeded(boolean pushNeeded) {
        isPushNeeded = pushNeeded;
    }

    public void setData(@Nullable String data) {
        this.data = data;
    }

    @Ignore
    @Override
    public int getChatViewType() {
        if(isFromMe) return ChatMessage.TYPE_ME;
        else return ChatMessage.TYPE_FRIEND;
    }
}