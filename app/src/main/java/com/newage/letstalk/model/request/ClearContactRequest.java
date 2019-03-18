package com.newage.letstalk.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClearContactRequest {
    @Expose @SerializedName("user") String phone;

    public ClearContactRequest(String phone) {
        this.phone = phone;
    }
}
