package com.newage.letstalk.dataLayer.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.newage.letstalk.dataLayer.local.tables.ChatList;

import java.util.List;

@Dao
public interface ChatListDao {

    @Query("SELECT * FROM friends")
    List<ChatList> getFriends();

    @Query("SELECT * from friends")
    LiveData<List<ChatList>> getFriendsLive();

    @Query("SELECT * FROM friends WHERE id = :friendId")
    ChatList getFriendById(String friendId);

    @Query("SELECT * FROM friends WHERE id = :friendId")
    LiveData<ChatList> getFriendByIdLive(String friendId);

    // @Query("SELECT * FROM friends WHERE isOnline = 1 ORDER BY match DESC LIMIT :limit")
   // ChatList getFriendsWith(String friendId, int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriend(ChatList chatList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriends(List<ChatList> chatLists);

    @Update
    int updateFriend(ChatList chatList);

    @Update
    int updateFriends(ChatList... chatLists);

    @Query("DELETE FROM friends WHERE id = :friendId")
    int deleteFriendById(String friendId);

    @Query("DELETE FROM friends")
    void deleteAllFriends();

}