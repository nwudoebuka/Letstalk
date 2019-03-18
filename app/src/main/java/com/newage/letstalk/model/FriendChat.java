package com.newage.letstalk.model;

import com.newage.letstalk.interfaces.ChatType;

public class FriendChat implements ChatType {





    @Override
    public int getChatViewType() {
        return ChatType.TYPE_FRIEND;
    }
}
