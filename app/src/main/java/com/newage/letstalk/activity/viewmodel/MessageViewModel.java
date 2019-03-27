package com.newage.letstalk.activity.viewmodel;

import android.app.Application;

import com.newage.letstalk.SessionManager;
import com.newage.letstalk.dataLayer.DataRepository;
import com.newage.letstalk.dataLayer.local.tables.Messages;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

public class MessageViewModel extends AndroidViewModel {
    private DataRepository repository;
    private SessionManager session;
    final private MutableLiveData<String> messageRequest = new MutableLiveData<>();

    public MessageViewModel(@NonNull Application application) {
        super(application);
        session = new SessionManager(application.getApplicationContext());
        repository = new DataRepository(application.getApplicationContext());
    }

    public LiveData<List<Messages>> getMessagesList() {
        return Transformations.switchMap(messageRequest, new Function<String, LiveData<List<Messages>>>() {
            @Override
            public LiveData<List<Messages>> apply(String input) {

                LiveData<List<Messages>> resLiveData = repository.getMessages(input, session.getPhoneNumber());
                final MediatorLiveData<List<Messages>> mediator = new MediatorLiveData<>();
                mediator.addSource(resLiveData, new Observer<List<Messages>>() {
                    @Override
                    public void onChanged(@Nullable List<Messages> chatLists) {
                        mediator.setValue(chatLists);
                    }
                });

                return mediator;
            }
        });
    }

    public void getMessages(String remoteId){
        messageRequest.setValue(remoteId);
    }

    public void saveMessage(Messages message){
        repository.insertMessage(message);
    }
}
