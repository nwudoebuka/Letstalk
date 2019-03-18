package com.newage.letstalk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Friend implements Serializable {
    @Expose @SerializedName("id") String id;
    @Expose @SerializedName("name") String name;
    @Expose @SerializedName("phone") String phone;
    @Expose @SerializedName("dp") String imageUrl;

    public Friend(String name, String phone, String imageUrl) {
        this.name = name;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}