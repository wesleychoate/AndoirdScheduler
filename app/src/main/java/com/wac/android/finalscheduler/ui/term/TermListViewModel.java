package com.wac.android.finalscheduler.ui.term;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TermListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TermListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is term fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}