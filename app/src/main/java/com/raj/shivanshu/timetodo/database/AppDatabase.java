package com.raj.shivanshu.timetodo.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Created by Shivanshu Raj on 20-08-2022.
 */
@Database(entities = {TaskEntry.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static final String TAG = "AppDatabase";
    public static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: Creating new database...");
                sInstance=Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }

        }
        Log.d(TAG, "getInstance: returning instance of the already instantiated database");
        return sInstance;

    }

    public abstract TaskDao taskDao();
}
