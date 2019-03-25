package com.newage.letstalk.dataLayer.local.dao;

import com.newage.letstalk.dataLayer.local.tables.Messages;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MessagesDao {

    @Query("SELECT * FROM messages WHERE key_remote_jid = :friendId")
    List<Messages> getItems(String friendId);

    @Query("SELECT * FROM messages WHERE key_remote_jid = :friendId")
    LiveData<List<Messages>> getItemsLive(String friendId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Messages message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<Messages> messages);

    @Update
    int updateItem(Messages messages);

    @Update
    int updateItems(Messages... messages);

    @Query("DELETE FROM messages WHERE key_remote_jid = :friendId")
    int deleteItemsByRemoteId(String friendId);

    @Query("DELETE FROM messages")
    void deleteAllItems();
}