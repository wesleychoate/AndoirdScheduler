package com.wac.android.finalscheduler.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to the Scheduler App.\nPlease enter mentors then terms.\nEnjoy!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}