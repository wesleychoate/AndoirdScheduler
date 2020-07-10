package com.wac.android.finalscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.wac.android.finalscheduler.entities.Mentor;

import java.util.List;

@Dao
public interface MentorDao extends DaoBaseVersion<Mentor> {
    @Query("SELECT * FROM mentors WHERE id=:id")
    Mentor getMentor(Integer id);

    @Query("SELECT * FROM mentors")
    LiveData<List<Mentor>> getAllMentors();

    @Query("DELETE FROM mentors")
    void deleteAllMentors();
}
