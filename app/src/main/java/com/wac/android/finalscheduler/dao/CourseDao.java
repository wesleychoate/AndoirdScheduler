package com.wac.android.finalscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.wac.android.finalscheduler.entities.Course;

import java.util.List;

@Dao
public interface CourseDao extends DaoBaseVersion<Course> {
    @Query("SELECT * FROM courses WHERE id=:id")
    Course getCourse(Integer id);

    @Query("SELECT * FROM courses")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM courses WHERE termId=:termId")
    List<Course> getCoursesForTerm(final int termId);

    @Query("DELETE FROM courses")
    void deleteAllCourses();

    @Insert
    long insertCourse(Course course);

    @Transaction
    @Query("SELECT COUNT(*) FROM courses WHERE mentorId=:mentorId")
    public int getCourseCountForMentor(Integer mentorId);
}
