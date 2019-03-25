package com.newage.letstalk.interfaces;

public interface ChatMessage {
    int TYPE_FRIEND = 0;
    int TYPE_ME = 1;

    int getChatViewType();
    //String getChatViewTypeName();
    //long getTimeInMilliSeconds();
}