package com.newage.letstalk.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
