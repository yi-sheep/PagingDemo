package com.gaoxianglong.pagingdemo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;

@Database(entities = {Student.class}, version = 1, exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {
    private static StudentDatabase instance;
    static synchronized StudentDatabase getInstance(Context context){
        // 判断数据库是否已经存在
        if (instance == null) {
            instance = Room.databaseBuilder(context,StudentDatabase.class,"Students_database")
                    .build();
        }
        return instance;
    }

    abstract StudentDao getStudentDao();
}
