package com.newage.letstalk.dataLayer.local.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "friends")
public final class Friend implements Serializable {

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

    public Friend(@NonNull String mId, @Nullable String mName, @Nullable String mPhone, @Nullable String mDp) {
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
//        Friend user = (Friend) o;
//        return Objects.equal(mId, user.mId);
//        //&& Objects.equal(mTitle, task.mTitle) && Objects.equal(mDescription, task.mDescription);
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hashCode(mId, mTitle, mDescription);
//    }

//    @Override
//    public String toString() {
//        return "Friend with id " + mId;
//    }

}