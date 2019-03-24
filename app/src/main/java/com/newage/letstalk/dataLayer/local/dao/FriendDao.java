package com.newage.letstalk.dataLayer.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.newage.letstalk.dataLayer.local.tables.Friend;

import java.util.List;

@Dao
public interface FriendDao {

    @Query("SELECT * FROM friends")
    List<Friend> getFriends();

    @Query("SELECT * from friends")
    LiveData<List<Friend>> getFriendsLive();

    @Query("SELECT * FROM friends WHERE id = :friendId")
    Friend getFriendById(String friendId);

    @Query("SELECT * FROM friends WHERE id = :friendId")
    LiveData<Friend> getFriendByIdLive(String friendId);

    // @Query("SELECT * FROM friends WHERE isOnline = 1 ORDER BY match DESC LIMIT :limit")
   // Friend getFriendsWith(String friendId, int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriend(Friend friend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriends(List<Friend> friends);

    @Update
    int updateFriend(Friend friend);

    @Update
    int updateFriends(Friend... friends);

    @Query("DELETE FROM friends WHERE id = :friendId")
    int deleteFriendById(String friendId);

    @Query("DELETE FROM friends")
    void deleteAllFriends();

}