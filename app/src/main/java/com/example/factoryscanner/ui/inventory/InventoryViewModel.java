package com.example.factoryscanner.ui.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventoryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public InventoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Leltár");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
