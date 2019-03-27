package com.newage.letstalk.dataLayer;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import android.util.Log;

import com.newage.letstalk.api.ApiInterface;
import com.newage.letstalk.api.RetrofitService;
import com.newage.letstalk.dataLayer.local.AppDatabase;
import com.newage.letstalk.dataLayer.local.tables.ChatList;
import com.newage.letstalk.dataLayer.local.tables.Messages;
import com.newage.letstalk.interfaces.ChatMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepository {
    private AppDatabase database;

    public DataRepository(Context context) {
        database = AppDatabase.getInstance(context);
    }


    public LiveData<ChatList> getFriend(String id) {
        return database.getFriendDAO().getFriendByIdLive(id);
    }

    public LiveData<List<ChatList>> getFriends(String phoneNumber) {
        LiveData<List<ChatList>> list = database.getFriendDAO().getFriendsLive();

        if (list.getValue() == null) {
            refreshFriendList(phoneNumber);
        }

        return list;
    }

    public LiveData<List<Messages>> getMessages(String remoteId, String me) {
        LiveData<List<Messages>> list = database.getMessageDAO().getItemsLive(remoteId, me);
        //if(list.getValue() == null){ refreshFriendList(phoneNumber); }
        return list;
    }

    @SuppressLint("StaticFieldLeak")
    public void insertFriend(final ChatList friend) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().insertFriend(friend);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertMessage(final Messages message) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getMessageDAO().insertItem(message);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertFriends(final List<ChatList> friends) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().insertFriends(friends);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void updateFriend(final ChatList chatList) {
        //note.setModifiedAt(AppUtils.getCurrentDateTime());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.getFriendDAO().updateFriend(chatList);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteFriend(final String id) {
        final LiveData<ChatList> friend = getFriend(id);

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

    public void refreshFriendList(String phoneNumber) {
        ApiInterface api = RetrofitService.initializer();
        Call<List<ChatList>> call = api.getFriendList(phoneNumber);
        call.enqueue(new Callback<List<ChatList>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatList>> call, @NonNull Response<List<ChatList>> response) {
                if (response.isSuccessful()) {
                    Log.i("DataRespository", "Successful");
                    List<ChatList> chatLists = response.body();
                    insertFriends(chatLists);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatList>> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    Log.e("DataRespository", "Call was canceled");
                } else {
                    Log.e("DataRespository", t.getMessage());
                }
            }
        });
    }
}