package com.newage.letstalk.model;

import com.newage.letstalk.interfaces.ChatType;

public class MyChat implements ChatType {
    String message;

    public MyChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int getChatViewType() {
        return ChatType.TYPE_ME;
    }
}
