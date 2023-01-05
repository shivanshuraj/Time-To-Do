package com.raj.shivanshu.timetodo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.raj.shivanshu.timetodo.database.AppDatabase;
import com.raj.shivanshu.timetodo.database.TaskEntry;

/**
 * Created by Shivanshu Raj on 24-08-2022.
 */
public class MainViewModel extends AndroidViewModel {
    LiveData<TaskEntry> tasks;
    AppDatabase db;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

}
