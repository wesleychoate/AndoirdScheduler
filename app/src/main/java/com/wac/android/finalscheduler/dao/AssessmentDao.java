package com.wac.android.finalscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.wac.android.finalscheduler.entities.Assessment;

import java.util.List;

@Dao
public interface AssessmentDao extends DaoBaseVersion<Assessment> {
    @Query("SELECT * FROM assessments where id=:id")
    Assessment getAssessment(Integer id);

    @Query("SELECT * FROM assessments")
    LiveData<List<Assessment>> getAllAssessments();

    @Query("SELECT * FROM assessments WHERE courseId=:courseId")
    LiveData<List<Assessment>> getAssessmentsForCourse(final int courseId);
}
