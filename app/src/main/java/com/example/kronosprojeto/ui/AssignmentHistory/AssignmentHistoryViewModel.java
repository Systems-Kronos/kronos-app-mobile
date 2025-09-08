package com.example.kronosprojeto.ui.AssignmentHistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AssignmentHistoryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AssignmentHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
