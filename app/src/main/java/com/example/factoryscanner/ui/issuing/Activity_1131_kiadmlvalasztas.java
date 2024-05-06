package com.example.factoryscanner.ui.issuing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Activity_1131_kiadmlvalasztas extends AppCompatActivity {
    GlobalClass globalVariable;
    private EditText Works_Order_Input;
    Spinner dynamicSpinner,dropDownMaterialForm;
    String MaterialForm;
    ArrayList<String> data;
    String[] temp_data;
    private boolean success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1131_kiadmlvalasztas);
        Works_Order_Input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!Works_Order_Input.getText().toString().trim().equals("")){
                    if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        globalVariable.setGlobal_settings("Works_Order_Input",Works_Order_Input.getEditableText().toString());
                        Intent openKiadMunkalapra = new Intent(Activity_1131_kiadmlvalasztas.this, Activity_11311_kiadmlreszlet.class);
                        Activity_1131_kiadmlvalasztas.this.startActivity(openKiadMunkalapra);
                        return true;
                    }
                }
                return false;
            }
            });

            String[] items2 = new String[] { "", "RM", "BI", "MA" };
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, items2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropDownMaterialForm.setAdapter(adapter2);
            dropDownMaterialForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.v("item", (String) parent.getItemAtPosition(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
            SyncData orderData = new SyncData();
            orderData.execute("");

            String[] items = new String[] { "", "RM - Alapanyag", "BI - Kereskedelmi tétel", "MA - Gyártott tétel" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dynamicSpinner.setAdapter(adapter);
            dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.v("item", (String) parent.getItemAtPosition(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(Activity_1131_kiadmlvalasztas.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private class SyncData extends AsyncTask<String, String, String> {
        String msg = getString(R.string.connection_error);
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_1131_kiadmlvalasztas.this, getString(R.string.Data_Refress),
                    getString(R.string.Just_moment), true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Connection conn = globalVariable.CONN(); //Connection Object
                if (conn == null) {
                    success = false;
                } else {
                    String query = globalVariable.getSql_command("1131");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    data = new ArrayList<String>();
                    int x =0;

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                String id = rs.getString("Material_Form");

                                data.add(id);
                                x += x + 1;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                msg = ex.getMessage();
                            }
                        }

                        success = true;
                    } else {
                        msg = "No Data found!";
                        success = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }
        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {
            progress.dismiss();


            if (!success)
            {
                Toast.makeText(Activity_1131_kiadmlvalasztas.this, msg, Toast.LENGTH_LONG).show();
            }
            else try {
                temp_data = new String[data.size()];
                temp_data = data.toArray(temp_data);
                String[] items2 = temp_data;
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Activity_1131_kiadmlvalasztas.this,android.R.layout.simple_spinner_item, items2);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropDownMaterialForm.setAdapter(adapter2);


            } catch (Exception ex) {
                Toast.makeText(Activity_1131_kiadmlvalasztas.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }
}

