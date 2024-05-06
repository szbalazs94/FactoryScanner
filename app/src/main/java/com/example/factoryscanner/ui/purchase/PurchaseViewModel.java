package com.example.factoryscanner.ui.purchase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PurchaseViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PurchaseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Bevételezés");
    }

    public LiveData<String> getText() {
        return mText;
    }
}