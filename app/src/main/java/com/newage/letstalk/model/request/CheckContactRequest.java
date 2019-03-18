package com.newage.letstalk.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckContactRequest {
    @Expose @SerializedName("phonec") String friendPhone;
    @Expose @SerializedName("nameec") String friendName;
    @Expose @SerializedName("username") String userPhone;

    public String getFriendPhone() {
        return friendPhone;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getUserPhone() {
        return userPhone;
    }
}
