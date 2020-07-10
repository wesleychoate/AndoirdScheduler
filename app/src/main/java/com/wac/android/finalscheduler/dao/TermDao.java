package com.wac.android.finalscheduler.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.wac.android.finalscheduler.entities.Term;

import java.util.List;

@Dao
public interface TermDao extends DaoBaseVersion<Term> {
    @Insert
    long insertTerm(Term term);

    @Query("SELECT * FROM terms")
    LiveData<List<Term>> getAllTerms();

    @Query("SELECT * FROM terms WHERE id=:id")
    Term getTerm(Integer id);

    @Query("DELETE FROM terms")
    void deleteAllTerms();
}
