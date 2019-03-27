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

    @Query("SELECT * FROM messages WHERE key_remote_jid = :friendId AND key_id = :me")
    List<Messages> getItems(String friendId, String me);

    @Query("SELECT * FROM messages WHERE key_remote_jid = :friendId AND key_id = :me")
    LiveData<List<Messages>> getItemsLive(String friendId, String me);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Messages message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<Messages> messages);

    @Update
    int updateItem(Messages messages);

    @Update
    int updateItems(Messages... messages);

    @Query("UPDATE messages SET status = :status WHERE id = :id")
    int updateSatus(long id, int status);

    @Query("UPDATE messages SET needs_push = :push WHERE id = :id")
    int updateNeedPush(long id, boolean push);

    @Query("DELETE FROM messages WHERE key_remote_jid = :friendId AND key_id = :me")
    int deleteItemsByRemoteId(String friendId, String me);

    @Query("DELETE FROM messages")
    void deleteAllItems();
}