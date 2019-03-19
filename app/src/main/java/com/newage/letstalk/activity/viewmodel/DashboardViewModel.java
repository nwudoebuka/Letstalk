package com.newage.letstalk.activity.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newage.letstalk.SessionManager;
import com.newage.letstalk.dataLayer.DataRepository;
import com.newage.letstalk.dataLayer.local.tables.Friend;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private DataRepository repository;
    private SessionManager session;
    final private MutableLiveData<String> friendRequest = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        session = new SessionManager(application.getApplicationContext());
        repository = new DataRepository(application.getApplicationContext());
        start(session.getPhoneNumber());
    }

    public void start(String phoneNumber){
        friendRequest.setValue(phoneNumber);
    }

    public LiveData<List<Friend>> getFriendsList() {
        return Transformations.switchMap(friendRequest, new Function<String, LiveData<List<Friend>>>() {
            @Override
            public LiveData<List<Friend>> apply(String input) {

                LiveData<List<Friend>> resLiveData = repository.getFriends(input);

                final MediatorLiveData<List<Friend>> mediator = new MediatorLiveData<>();
                mediator.addSource(resLiveData, new Observer<List<Friend>>() {
                    @Override
                    public void onChanged(@Nullable List<Friend> friends) {
                        mediator.setValue(friends);
                    }
                });

                return mediator;
            }
        });
    }

    public void addFriend(Friend friend){
        repository.insertFriend(friend);
    }

    public void refreshFriendList(){
        repository.refreshFriendList(session.getPhoneNumber());
    }
}