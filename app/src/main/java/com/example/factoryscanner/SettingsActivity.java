package com.example.factoryscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreferenceDialogFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton scanSettings;
    private View inflated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final EditTextPreference preference = findPreference("pref_password");
            final EditTextPreference preferenceIp = findPreference("pref_ip");
            final EditTextPreference preferencePort = findPreference("pref_port");
            final EditTextPreference preferenceDb = findPreference("pref_db");
            final EditTextPreference preferenceUser = findPreference("pref_user");


            Preference button = getPreferenceManager().findPreference("scanLogin");
            button.setWidgetLayoutResource(R.layout.as_widget);
            if (button != null) {
                button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        Intent intent = new Intent(SettingsFragment.this.getContext(), CustomScannerActivity.class);
                        scanLoginResultLauncher.launch(intent);
                        return true;
                    }
                });
            }

            if (preferenceIp != null) {
                preferenceIp.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (TextUtils.isEmpty(text)){
                            return "Nincs beállítva!";
                        }
                        return text;
                    }
                });
            }

            if (preferencePort != null) {
                preferencePort.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (TextUtils.isEmpty(text)){
                            return "Nincs beállítva!";
                        }
                        return text;
                    }
                });
            }

            if (preferenceUser != null) {
                preferenceUser.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (TextUtils.isEmpty(text)){
                            return "Nincs beállítva!";
                        }
                        return text;
                    }
                });
            }

            if (preferenceDb != null) {
                preferenceDb.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                    @Override
                    public CharSequence provideSummary(EditTextPreference preference) {
                        String text = preference.getText();
                        if (TextUtils.isEmpty(text)){
                            return "Nincs beállítva!";
                        }
                        return text;
                    }
                });
            }

            if (preference != null) {
                preference.setSummaryProvider(new Preference.SummaryProvider() {
                    @Override
                    public CharSequence provideSummary(Preference preference) {

                        String getPassword = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_password", "not set");
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("employee_name","Dobos Antal").apply();
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                        //we assume getPassword is not null
                        assert getPassword != null;

                        //return "not set" else return password with asterisks
                        if (getPassword.equals("not set")) {
                            return getPassword;
                        } else {
                            return (setAsterisks(getPassword.length()));
                        }
                    }
                });

                //set input type as password and set summary with asterisks the new password
                preference.setOnBindEditTextListener(
                        new EditTextPreference.OnBindEditTextListener() {
                            @Override
                            public void onBindEditText(@NonNull final EditText editText) {
                                final int inputType = editText.getInputType();
                                ViewGroup container =(ViewGroup) editText.getParent();
                                container.removeView(editText);
                                container.addView(editText);
                                AppCompatCheckBox chkBox = new AppCompatCheckBox(container.getContext());
                                chkBox.setText("Show password");
                                chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        final int selStart = editText.getSelectionStart();
                                        final int selEnd = editText.getSelectionEnd();

                                        if (isChecked) {
                                            editText.setInputType(inputType | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                        } else {
                                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        }

                                        editText.setSelection(selStart, selEnd);
                                    }
                                });
                                container.addView(chkBox);
                                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                preference.setSummaryProvider(new Preference.SummaryProvider() {
                                    @Override
                                    public CharSequence provideSummary(Preference preference) {
                                        return setAsterisks(editText.getText().toString().length());
                                    }
                                });
                            }
                        });

            }
        }

        private final ActivityResultLauncher<Intent> scanLoginResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String scannedLoginDetails = MainActivity.sharedPreferences.getString("Barcode_Result","");
                            String [] credentialList = scannedLoginDetails.split(",");
                            EditTextPreference preferenceIp = findPreference("pref_ip");
                            EditTextPreference preferencePort = findPreference("pref_port");
                            EditTextPreference preferenceDataBaseName = findPreference("pref_db");
                            EditTextPreference preferenceUserName = findPreference("pref_user");
                            EditTextPreference preferencePassword = findPreference("pref_password");
                            preferenceIp.setText(credentialList[0]);
                            preferencePort.setText(credentialList[1]);
                            preferenceDataBaseName.setText(credentialList[2]);
                            preferenceUserName.setText(credentialList[3]);
                            preferencePassword.setText(credentialList[4]);

                        }
                    }
                });

        //return the password in asterisks
        private String setAsterisks(int length) {
            StringBuilder sb = new StringBuilder();
            for (int s = 0; s < length; s++) {
                sb.append("*");
            }
            return sb.toString();
        }
    }

    public static class PasswordToggle extends EditTextPreferenceDialogFragmentCompat {

        protected void onAddEditTextToDialogView(View dialogView, final EditText editText) {
            View oldEditText = dialogView.findViewById(android.R.id.edit);
            if (oldEditText != null) {
                ViewGroup container = (ViewGroup) (oldEditText.getParent());
                if (container != null) {
                    container.removeView(oldEditText);
                    container.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    final int inputType = editText.getInputType();
                    AppCompatCheckBox chkBox = new AppCompatCheckBox(container.getContext());
                    chkBox.setText("Show password");
                    chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            final int selStart = editText.getSelectionStart();
                            final int selEnd = editText.getSelectionEnd();

                            if (isChecked) {
                                editText.setInputType(inputType | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            } else {
                                editText.setInputType(inputType);
                            }

                            editText.setSelection(selStart, selEnd);
                        }
                    });
                    container.addView(chkBox, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        }
    }
}