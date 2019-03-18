package com.newage.letstalk.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @Expose @SerializedName("phone") String phone;
    @Expose @SerializedName("otp") String otp;

    public LoginRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }
}
