package com.example.factoryscanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.factoryscanner.databinding.ActivityMainBinding;
import com.example.factoryscanner.ui.purchase.PurchaseItemFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView employeeNameTextView;
    TextView companyNameTextView;
    ImageView companyLogo;
    private String my_user;
    private String my_password;
    public Toolbar toolbar;
    public NavigationView navigationView;
    public DrawerLayout drawer;
    public AppBarConfiguration mAppBarConfiguration;
    public NavController navController;
    private ActivityMainBinding binding;
    public static SharedPreferences sharedPreferences;

    GlobalClass globalVariable = GlobalClass.getInstance();

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String ip = sharedPreferences.getString("pref_ip","");
        my_user = sharedPreferences.getString("pref_user","");
        my_password = sharedPreferences.getString("pref_password","");
        String employeeName = sharedPreferences.getString("employee_name","");

        Context context = this;
        globalVariable.get_preferences(context);

        setSupportActionBar(binding.appBarMain.toolbar);

        textView = findViewById(R.id.text_home);
        try {
            CheckLogin checkLogin = new CheckLogin(MainActivity.this);
            checkLogin.execute();
            binding.appBarMain.loginButton.hide();
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        employeeNameTextView = (TextView) headerView.findViewById(R.id.employee_name);
        companyNameTextView = (TextView) headerView.findViewById(R.id.companyName);
        companyLogo = (ImageView) headerView.findViewById(R.id.companyLogo);
        drawer = binding.drawerLayout;
        toolbar = findViewById(R.id.toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_purchase, R.id.nav_list, R.id.nav_inventory, R.id.nav_location, R.id.nav_issuing)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }


    public void showBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if(id == R.id.action_logout) {
            if (sharedPreferences.getBoolean("pref_logout",true)){
                sharedPreferences.edit().remove("pref_ip").apply();
                sharedPreferences.edit().remove("pref_user").apply();
                sharedPreferences.edit().remove("pref_port").apply();
                sharedPreferences.edit().remove("pref_db").apply();
                sharedPreferences.edit().remove("pref_password").apply();
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,mAppBarConfiguration);
    }

    public void saveNewPreference(String preferenceName, String preferenceValue) {
        sharedPreferences.edit().putString(preferenceName,preferenceValue).apply();
    }

    public String getPreference(String preferenceName) {
        return sharedPreferences.getString(preferenceName,"");
    }


    private class CheckLogin extends BackgroundTask {

        String z = "";
        Boolean isSuccess = false;
        String companyName = "";
        byte [] imageBytes = null;




        public CheckLogin(Activity activity) {
            super(activity);
        }

        @Override
        public void onPostExecute(String r) {
            try
            {
                if (isSuccess) {
                    textView.setText(r);
                    employeeNameTextView.setText(r);
                    companyNameTextView.setText(companyName);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    companyLogo.setImageBitmap(bitmap);
                    DrawerLayout drawer = binding.drawerLayout;
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    toggle.setDrawerIndicatorEnabled(true);
                    globalVariable.set_preference(getApplicationContext());
                    sharedPreferences.edit().putString("employee_name",r).apply();
                    Snackbar.make(textView, "Bejelentkezés...", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else
                {
                    Toast.makeText(MainActivity.this, r, Toast.LENGTH_LONG).show();
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                }
            } catch (Exception ex)
            {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public String doInBackground(String... params) {
            if (my_user.trim().equals("") || my_password.trim().equals(""))
                z = getString(R.string.please_enter_username_password);
            else try {
                globalVariable.get_preferences(getApplicationContext());
                Connection con = globalVariable.CONN(); //Connection Object
                //con = connectionclass(un, pass, db, ip);        // Connect to database
                if (con == null) {
                    z = getString(R.string.connection_error);
                } else {

                    String query = globalVariable.getSql_command("00");

                    PreparedStatement stmt = (PreparedStatement) con.prepareStatement(query);
                    stmt.setString(1, my_user);
                    stmt.setString(2, my_password);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        z = rs.getString("Full_Name").trim();

                        globalVariable.setUser(rs.getString("Employee_Reference").trim());
                        globalVariable.setPin(rs.getString("PIN_Number").trim());
                        globalVariable.setEmployee_name(rs.getString("Full_Name").trim());
                        companyName = rs.getString("Company_Name").trim();
                        globalVariable.setCompany_Name(companyName);
                        imageBytes = rs.getBytes("Company_Logo");
                        globalVariable.set_preference(getApplicationContext());
                        isSuccess = true;
                        con.close();
                    } else {
                        z = getString(R.string.invalid_credentials);
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = getString(R.string.invalid_credentials) + " \n" + ex.getMessage();
            }
            return z;
        }
    }
}