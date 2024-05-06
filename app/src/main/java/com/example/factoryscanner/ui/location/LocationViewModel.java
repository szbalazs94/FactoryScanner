package com.example.factoryscanner.ui.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LocationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tárolóhely");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
