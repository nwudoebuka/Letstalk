package com.newage.letstalk.dataLayer.local.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "friends")
public final class ChatList implements Serializable {

    @PrimaryKey @NonNull @ColumnInfo(name = "id")
    @Expose @SerializedName("id")
    private final String mId;

    @Nullable @ColumnInfo(name = "name")
    @Expose @SerializedName("name")
    private final String mName;

    @Nullable @ColumnInfo(name = "phone")
    @Expose @SerializedName("phone")
    private final String mPhone;

    @Nullable @ColumnInfo(name = "dp")
    @Expose @SerializedName("dp")
    private final String mDp;

    //private Date lastChatDate;
    //private boolean isOnline;

    public ChatList(@NonNull String mId, @Nullable String mName, @Nullable String mPhone, @Nullable String mDp) {
        this.mId = mId;
        this.mName = mName;
        this.mPhone = mPhone;
        this.mDp = mDp;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getPhone() {
        return mPhone;
    }

    @Nullable
    public String getDp() {
        return mDp;
    }





    /* other utility methods */

//    public boolean isEmpty() {
//        return false;
//        //return Strings.isNullOrEmpty(mTitle) && Strings.isNullOrEmpty(mDescription);
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ChatList user = (ChatList) o;
//        return Objects.equal(mId, user.mId);
//        //&& Objects.equal(mTitle, task.mTitle) && Objects.equal(mDescription, task.mDescription);
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hashCode(mId, mTitle, mDescription);
//    }

//    @Override
//    public String toString() {
//        return "ChatList with id " + mId;
//    }

}