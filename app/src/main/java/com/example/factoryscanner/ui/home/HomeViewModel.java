package com.example.factoryscanner.ui.home;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.example.factoryscanner.R;

public class HomeViewModel extends ViewModel{

    private final MutableLiveData<String> mText;
    private static SharedPreferences sharedPreferences;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Antal");
    }

    public LiveData<String> getText() {
        return mText;
    }
}