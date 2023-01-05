package com.raj.shivanshu.timetodo.database;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Created by Shivanshu Raj on 20-08-2022.
 */
public class DateConverter {
    @TypeConverter
    public Date toDate(long timeInMillis){
        return new Date(timeInMillis);
    }

    @TypeConverter
    public Long toTimeInMillis(Date date){
        return date.getTime();
    }
}
