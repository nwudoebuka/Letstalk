package com.newage.letstalk.activity.viewmodel;

import android.app.Application;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.newage.letstalk.SessionManager;
import com.newage.letstalk.dataLayer.DataRepository;
import com.newage.letstalk.dataLayer.local.tables.ChatList;

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

    public LiveData<List<ChatList>> getFriendsList() {
        return Transformations.switchMap(friendRequest, new Function<String, LiveData<List<ChatList>>>() {
            @Override
            public LiveData<List<ChatList>> apply(String input) {

                LiveData<List<ChatList>> resLiveData = repository.getFriends(input);

                final MediatorLiveData<List<ChatList>> mediator = new MediatorLiveData<>();
                mediator.addSource(resLiveData, new Observer<List<ChatList>>() {
                    @Override
                    public void onChanged(@Nullable List<ChatList> chatLists) {
                        mediator.setValue(chatLists);
                    }
                });

                return mediator;
            }
        });
    }

    public void addFriend(ChatList chatList){
        repository.insertFriend(chatList);
    }

    public void refreshFriendList(){
        repository.refreshFriendList(session.getPhoneNumber());
    }

    public void logout(){
        repository.deleteFriend();
    }
}