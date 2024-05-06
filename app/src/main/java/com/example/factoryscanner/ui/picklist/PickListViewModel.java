package com.example.factoryscanner.ui.picklist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PickListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PickListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Kiszedési lista");
    }

    public LiveData<String> getText() {
        return mText;
    }
}