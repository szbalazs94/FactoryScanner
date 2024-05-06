package com.example.factoryscanner.ui.issuing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IssuingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public IssuingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Anyag kiadás");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
