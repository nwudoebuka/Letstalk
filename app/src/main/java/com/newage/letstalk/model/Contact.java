package com.newage.letstalk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Contact {
    @Nullable
    private final String name;

    @NonNull
    private final String phoneNumber;

    public Contact(@Nullable String name, @NonNull String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
