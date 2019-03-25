package com.newage.letstalk.dataLayer.local;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.newage.letstalk.dataLayer.local.dao.ChatListDao;
import com.newage.letstalk.dataLayer.local.dao.MessagesDao;
import com.newage.letstalk.dataLayer.local.tables.ChatList;
import com.newage.letstalk.dataLayer.local.tables.Messages;

@Database(entities = {ChatList.class, Messages.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static String DB_NAME = "letstalkDb.db";

    public abstract ChatListDao getFriendDAO();
    public abstract MessagesDao getMessageDAO();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                            //.allowMainThreadQueries() //Allows room to do operation on main thread Note: Not advisable
                            //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            //.fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public void cleanUp() {
        INSTANCE = null;
    }

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            // Create the new table
//            database.execSQL("CREATE TABLE notification_new (id INTEGER NOT NULL, isUnRead INTEGER NOT NULL, title TEXT, message TEXT, date INTEGER, source INTEGER NOT NULL, PRIMARY KEY(id))");
//            // Copy the data
//            database.execSQL("INSERT INTO notification_new (id, isUnRead, title, message, date) SELECT id, isUnRead, title, message, date FROM notification");
//            // Remove the old table
//            database.execSQL("DROP TABLE notification");
//            // Change the table name to the correct one
//            database.execSQL("ALTER TABLE notification_new RENAME TO notification");
//
//            database.execSQL("CREATE TABLE fingers (id INTEGER NOT NULL, token TEXT, publicKey TEXT, PRIMARY KEY(id))");
//        }
//    };
//
//    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE user ADD COLUMN password TEXT");
//        }
//    };

}