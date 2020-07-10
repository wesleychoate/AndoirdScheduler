package com.wac.android.finalscheduler.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface DaoBaseVersion<T> {

    @Insert
    void insert(T... t);

    @Insert
    void insert(T t);

    @Update
    void update(T... t);

    @Delete
    void delete(T... t);
}
