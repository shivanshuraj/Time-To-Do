package com.raj.shivanshu.timetodo.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Shivanshu Raj on 20-08-2022.
 */
@Entity(tableName = "todo")
public class TaskEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private Date date;
    private int priority;
    private String description;
    private boolean isSelected;
    private boolean isCompleted;

    @Ignore
    public TaskEntry(String title, Date date, int priority, boolean isCompleted) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.isCompleted = TaskEntry.this.isCompleted;
    }

    @Ignore
    public TaskEntry(String title, Date date, int priority, String description, boolean isCompleted) {
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.description = description;
        this.isCompleted = TaskEntry.this.isCompleted;
    }

    public TaskEntry(int id, String title, Date date, int priority, String description, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.priority = priority;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

