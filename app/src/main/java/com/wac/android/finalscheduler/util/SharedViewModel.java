package com.wac.android.finalscheduler.util;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.wac.android.finalscheduler.ScheduleRepository;
import com.wac.android.finalscheduler.entities.Assessment;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.entities.Term;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SharedViewModel extends AndroidViewModel {
    private static SharedViewModel sharedViewModel;

    private ScheduleRepository sData;
    private LiveData<List<Mentor>> mentors;
    private LiveData<List<Term>> terms;
    private LiveData<List<Course>> courses;
    private LiveData<List<Assessment>> assessments;

    private SharedViewModel(@NonNull Application app) {
        super(app);
        sData = new ScheduleRepository(app);
        mentors = sData.getAllMentors();
        terms = sData.getAllTerms();
        courses = sData.getAllCourses();
        assessments = sData.getAllAssessments();
    }

    public static synchronized SharedViewModel getInstance(@NonNull Application app) {
        if (sharedViewModel == null) {
            sharedViewModel = new SharedViewModel(app);
            return sharedViewModel;
        }
        return sharedViewModel;
    }

    public LiveData<List<Mentor>> getMentors() {
        return this.mentors;
    }

    public void createMentor(Mentor mentor) {
        sData.createMentor(mentor);
    }

    public void updateMentor(Mentor mentor) {
        sData.updateMentor(mentor);
    }

    public void deleteMentor(Mentor mentor) {
        sData.deleteMentor(mentor);
    }

    public LiveData<List<Term>> getTerms() {
        return this.terms;
    }

    public LiveData<List<Course>> getCourses() {
        return this.courses;
    }

    public void deleteCourse(Course course) { sData.deleteCourse(course); }

    public long createCourse(Course course) {
        return sData.createCourse(course);
    }

    public void updateCourse(Course course) { sData.updateCourse(course); }

    public long createTerm(Term term) {
        return sData.createTerm(term);
    }

    public void updateTerm(Term term) { sData.updateTerm(term);}

    public void deleteTerm(Term term) { sData.deleteTerm(term);}

    public void createAssessment(Assessment assessment) { sData.createAssessment(assessment); }

    public LiveData<List<Assessment>> getAssessments() { return this.assessments; }

    public void updateAssessment(Assessment assessment) { sData.updateAssessment(assessment); }

    public void deleteAssessment(Assessment assessment) { sData.deleteAssessment(assessment); }
}
