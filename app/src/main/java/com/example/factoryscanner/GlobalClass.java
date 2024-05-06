package com.example.factoryscanner;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class GlobalClass extends Application {

    HashMap<String, String> global_settings = new HashMap<String, String>();
    HashMap<String, Integer> global_int = new HashMap<String, Integer>();
    public static GlobalClass getInstance() {
        return new GlobalClass();
    }

    private String ip = "";// ip = "192.168.0.118:50048"; //192.168.0.118:1433;
    private String port = "";
    private String databaseName = ""; // = "GIGA2003";

    private String user = ""; // = "GOTTI";
    private String pin = ""; // = "1234";
    private String employee_name = ""; //= "Imre";
    private String company_Name = "";
    private boolean licenc_ok = false;

    private String id;
    private String temp_id;

    public void setGlobal_settings(String Key, String Value)
    {
        global_settings.put(Key,Value);
    }

    public String getGlobal_Settings (String Key){
        if(global_settings.get(Key)!=null){return global_settings.get(Key);}
        return "";
    }
    public void setGlobal_int(String Key, Integer Value)
    {
        global_int.put(Key,Value);
    }

    public Integer getGlobal_int (String Key){
        if(global_int.get(Key)!=null){return global_int.get(Key);}
        return 0;
    }

    public String getIp (){
        if(ip!=null){return ip;}
        return "";
    }
    public String getPort (){
        if(port!=null){return port;}
        return "";
    }
    public String getDatabaseName(){
        if(databaseName !=null){return databaseName;}
        return "";
    }

    public String getUser (){
        if(user!=null){return user;}
        return "";
    }
    public String getPin(){
        if(pin!=null){return pin;}
        return "";
    }

    public String getEmployee_name(){
        if(employee_name!=null){return employee_name;}
        return "";
    }
    public String getCompany_Name(){
        if(company_Name!=null){return company_Name;}
        return "";
    }
    public boolean getLicence_OK(){
        return licenc_ok;
    }

    public String getTemp_id () {
        if (temp_id != null){return temp_id;}
        return "";
    }
    public String getId(){
        if(id!=null){return id;}
        return "";
    }
    public String getSql_command(String param_id){
        id = param_id;
        String sql_command = "";

        try {
            Connection con = CONN(); //Connection Object
            if (con == null) {
                sql_command = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "SELECT [sql_command] FROM [dbo].[WNET_ANDROID_SQL] WHERE [id] = '" + id.trim() + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {
                    sql_command = rs.getString("sql_command").trim();
                    con.close();
                } else {
                    con.close();
                }
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return sql_command;
    }

    public void setIp(String new_ip) {
        ip = new_ip;
    }
    public void setPort(String new_port) {
        port = new_port;
    }
    public void setDatabaseName(String new_databaseName) {
        databaseName = new_databaseName;
    }

    public void setUser(String new_user) {
        user = new_user;
    }
    public void setPin(String new_pin) {
        pin = new_pin;
    }
    public void setEmployee_name(String new_employee_name) {
        employee_name = new_employee_name;
    }
    public void setCompany_Name (String new_company_Name) {
        company_Name = new_company_Name;
    }
    public void setLicence_OK (boolean new_licenc_ok) {
        licenc_ok = new_licenc_ok;
    }

    public void setId(String new_id) {
        id = new_id;
    }
    public void setTemp_id(String new_temp_id) {
        temp_id=new_temp_id;
    }

    public static final String MY_PREFS_NAME = "FactoryMasterPreferences";

    public void remove_preference()
    {
        SharedPreferences settings = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE) ;
        settings.edit().clear().apply();
    }

    public void set_preference(Context context)
    {
        try
        {
            PreferenceManager.setDefaultValues(context,R.xml.root_preferences,false);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings.edit().remove("ip").apply();
            settings.edit().remove("port").apply();
            settings.edit().remove("databaseName").apply();
            settings.edit().remove("user").apply();
            settings.edit().remove("pin").apply();
            settings.edit().remove("employee_name").apply();
            settings.edit().remove("company_Name").apply();
            settings.edit().remove("licenc_ok").apply();

            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString( "ip", ip);
            editor.putString( "port", port);
            editor.putString( "databaseName", databaseName);
            editor.putString( "user", user);
            editor.putString( "pin", pin);
            editor.putString( "employee_name", employee_name);
            editor.putString( "company_Name", company_Name);
            if (licenc_ok)
            {
                editor.putString( "licenc_ok","true");
            }
            else
            {
                editor.putString( "licenc_ok","false");
            }

            editor.apply();
        } catch (Exception e)
        {
            Log.e("ERROR", e.getMessage());
        }
    }

    public void get_preferences(Context context)
    {
        try
        {
            PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            ip = prefs.getString("pref_ip", ""); //81.183.210.78
            port = prefs.getString("pref_port", ""); // 50048
            databaseName = prefs.getString("pref_db", ""); // GIGA2003
            user = prefs.getString("pref_user", ""); // ADMIN
            pin = prefs.getString("pref_password", ""); // 1234
            employee_name = prefs.getString("employee_name", "ADMIN"); // employee_name
            company_Name = prefs.getString("company_Name", "WinnerNet"); // employee_name
            licenc_ok= prefs.getString("licenc_ok", "true").equals("true");
        } catch (Exception e)
        {
            Log.e("ERROR", e.getMessage());
        }
    }

    public Connection CONN()
    {
        Connection conn = null;
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String ConnURL = null;
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                //Database Password
                String password = "Gs0y1tuAs6yrt";
                //Database user
                String un = "sa";
                ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + databaseName + ";user=" + un + ";password=" + password + ";";
                conn = DriverManager.getConnection(ConnURL);
            } catch (SQLException se) {
                Log.e("ERRO", se.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e("ERRO", e.getMessage());
            } catch (Exception e) {
                Log.e("ERRO", e.getMessage());
            }

        } catch (Exception e)
        {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
