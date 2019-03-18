package com.newage.letstalk.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @Expose @SerializedName("name") String name;
    @Expose @SerializedName("phone") String phone;
    @Expose @SerializedName("otp") String otp;

    public RegisterRequest(String name, String phone, String otp) {
        this.name = name;
        this.phone = phone;
        this.otp = otp;
    }
}
