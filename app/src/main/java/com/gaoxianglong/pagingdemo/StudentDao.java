package com.gaoxianglong.pagingdemo;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StudentDao {
    @Insert
    void insertStudents(Student... students);

    @Query("DELETE FROM STUDENT_TABLE")
    void deleteAllStudent();

    @Query("SELECT * FROM STUDENT_TABLE ORDER BY ID")
    DataSource.Factory<Integer, Student> getAllStudents();
}
