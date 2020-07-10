package com.wac.android.finalscheduler.ui.mentor;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.wac.android.finalscheduler.ScheduleRepository;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.entities.Term;
import com.wac.android.finalscheduler.util.NotificationHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MentorViewModel extends AndroidViewModel {
    private WorkManager mWorkManager;

    private ScheduleRepository sData;

    private LiveData<List<Mentor>> mentors;

    public LiveData<List<Mentor>> getMentors() {
        //OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationHelper.class)
        //        .setInitialDelay(5000L, TimeUnit.MILLISECONDS)
                //.setInputData(inputData)
                //.addTag(workTag)
        //        .build();

        //mWorkManager.enqueue(notificationWork);
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

    public MentorViewModel(@NonNull Application app) {
        super(app);
        sData = new ScheduleRepository(app);
        mentors = sData.getAllMentors();

        mWorkManager = WorkManager.getInstance(app);
    }
}