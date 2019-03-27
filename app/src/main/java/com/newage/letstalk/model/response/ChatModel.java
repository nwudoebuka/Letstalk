package com.newage.letstalk.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ChatModel implements Serializable {
    @Expose
    @SerializedName("messages")
    private String data;

    @Expose
    @SerializedName("sender")
    private String sender;

    @Expose
    @SerializedName("reciever")
    private String reciever;

    @Expose
    @SerializedName("audio")
    private String audio;

    @Expose
    @SerializedName("video")
    private String video;

    @Expose
    @SerializedName("dp")
    private String dp;

    public String getData() {
        return data;
    }

    public String getSender() {
        return sender;
    }

    public String getReciever() {
        return reciever;
    }

    public String getAudio() {
        return audio;
    }

    public String getVideo() {
        return video;
    }

    public String getDp() {
        return dp;
    }
}