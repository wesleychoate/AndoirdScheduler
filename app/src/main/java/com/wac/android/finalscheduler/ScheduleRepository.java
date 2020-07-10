package com.wac.android.finalscheduler;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.wac.android.finalscheduler.dao.AssessmentDao;
import com.wac.android.finalscheduler.dao.CourseDao;
import com.wac.android.finalscheduler.dao.MentorDao;
import com.wac.android.finalscheduler.dao.TermDao;
import com.wac.android.finalscheduler.entities.Assessment;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.entities.Term;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ScheduleRepository {
    public TermDao termDao;
    public CourseDao courseDao;
    public MentorDao mentorDao;
    public AssessmentDao assessmentDao;

    private LiveData<List<Term>> terms;
    private LiveData<List<Mentor>> mentors;
    private LiveData<List<Course>> courses;
    private LiveData<List<Assessment>> assessments;

    public ScheduleRepository(Application app) {
        ScheduleDatabase sdb = ScheduleDatabase.getInstance(app);
        termDao = sdb.getTermDao();
        mentorDao = sdb.getMentorDao();
        courseDao = sdb.getCourseDao();
        assessmentDao = sdb.getAssessmentDao();
        terms = termDao.getAllTerms();
        mentors = mentorDao.getAllMentors();
        courses = courseDao.getAllCourses();
        assessments = assessmentDao.getAllAssessments();
    }

    public LiveData<List<Term>> getAllTerms() { return this.terms; }
    public LiveData<List<Mentor>> getAllMentors() { return this.mentors; }
    public LiveData<List<Course>> getAllCourses() { return this.courses; }
    public LiveData<List<Assessment>> getAllAssessments() { return this.assessments; }

    public long createTerm(Term term) {
        Future<Long> newTermId = ScheduleDatabase.writeExec.submit(() -> termDao.insertTerm(term));
        try {
            return newTermId.get();
        } catch (ExecutionException e) {
            Log.e("Term", "Creating new term failed!", e);
            return -1;
        } catch (InterruptedException e) {
            Log.e("Term", "Creating new term failed!", e);
            return -1;
        }
    }

    public void updateTerm(Term term) {
        ScheduleDatabase.writeExec.execute(() -> {
            termDao.update(term);
        });
    }

    public void deleteTerm(Term term) {
        ScheduleDatabase.writeExec.execute(() -> {
            termDao.delete(term);
        });
    }

    public void createMentor(Mentor mentor) {
        ScheduleDatabase.writeExec.execute(() -> {
            mentorDao.insert(mentor);
        });
    }

    public void updateMentor(Mentor mentor) {
        ScheduleDatabase.writeExec.execute(() -> {
            mentorDao.update(mentor);
        });
    }

    public void deleteMentor(Mentor mentor) {
        ScheduleDatabase.writeExec.execute(() -> {
            mentorDao.delete(mentor);
        });
    }

    public void deleteCourse(Course course) {
        ScheduleDatabase.writeExec.execute(() -> {
            courseDao.delete(course);
        });
    }

    public long createCourse(Course course) {
        Future<Long> newCourseId = ScheduleDatabase.writeExec.submit(() -> courseDao.insertCourse(course));
        try {
            return newCourseId.get();
        } catch (ExecutionException e) {
            Log.e("Course", "Creating new course failed!", e);
            return -1;
        } catch (InterruptedException e) {
            Log.e("Course", "Creating new course failed!", e);
            return -1;
        }
    }

    public void updateCourse(Course course) {
        ScheduleDatabase.writeExec.execute(() -> {
            courseDao.update(course);
        });
    }

    public void createAssessment(Assessment assessment) {
        ScheduleDatabase.writeExec.execute(() -> {
            assessmentDao.insert(assessment);
        });
    }

    public void updateAssessment(Assessment assessment) {
        ScheduleDatabase.writeExec.execute(() -> {
            assessmentDao.update(assessment);
        });
    }

    public void deleteAssessment(Assessment assessment) {
        ScheduleDatabase.writeExec.execute(() -> {
            assessmentDao.delete(assessment);
        });
    }
}
