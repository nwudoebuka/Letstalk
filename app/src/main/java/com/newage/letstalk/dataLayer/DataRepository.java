package com.newage.letstalk.dataLayer;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.newage.letstalk.api.ApiInterface;
import com.newage.letstalk.api.RetrofitService;
import com.newage.letstalk.dataLayer.local.AppDatabase;
import com.newage.letstalk.dataLayer.local.tables.Friend;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepository {
    private AppDatabase database;

    public DataRepository(Context context) {
        database = AppDatabase.getInstance(context);
    }


    public LiveData<Friend> getFriend(String id) {
        return database.getFriendDAO().getFriendByIdLive(id);
    }

    public LiveData<List<Friend>> getFriends(String phoneNumber) {
        LiveData<List<Friend>> list = database.getFriendDAO().getFriendsLive();

        if(list.getValue() == null){
            refreshFriendList(phoneNumber);
        }

        return list;
    }

    @SuppressLint("StaticFieldLeak")
    public void insertFriend(final Friend friend) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().insertFriend(friend);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertFriends(final List<Friend> friends) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().insertFriends(friends);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void updateFriend(final Friend friend) {
        //note.setModifiedAt(AppUtils.getCurrentDateTime());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().updateFriend(friend);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteFriend(final String id) {
        final LiveData<Friend> friend = getFriend(id);

        if (friend != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    database.getFriendDAO().deleteFriendById(id);
                    return null;
                }
            }.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteFriend() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().deleteAllFriends();
                return null;
            }
        }.execute();
    }

    public void refreshFriendList(String phoneNumber){
        ApiInterface api = RetrofitService.initializer();
        Call<List<Friend>> call = api.getFriendList(phoneNumber);
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(@NonNull Call<List<Friend>> call, @NonNull Response<List<Friend>> response) {
                if (response.isSuccessful()) {
                      List<Friend> friends = response.body();
                      insertFriends(friends);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Friend>> call,@NonNull Throwable t) {
                if(call.isCanceled()){
                    Log.e("DataRespository", "Call was canceled");
                }else {
                    Log.e("DataRespository", t.getMessage());
                }
            }
        });
    }
}