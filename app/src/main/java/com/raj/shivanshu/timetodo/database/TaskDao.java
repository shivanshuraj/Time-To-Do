package com.raj.shivanshu.timetodo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by Shivanshu Raj on 20-08-2022.
 */

@Dao
public interface TaskDao {
    @Query("SELECT * FROM todo ORDER BY priority")
    LiveData<List<TaskEntry>> loadAllTasks();

    @Insert
    void insertTask(TaskEntry todo);

    @Delete
    void deleteTask(TaskEntry taskEntry);

    @Query("DELETE FROM todo WHERE id=:id")
    void deleteTaskById(int id);

    @Query("SELECT * FROM todo WHERE id=:id")
    TaskEntry loadTaskById(int id);

    @Update(onConflict =OnConflictStrategy.REPLACE)
    void updateTask(TaskEntry taskEntry);

}

