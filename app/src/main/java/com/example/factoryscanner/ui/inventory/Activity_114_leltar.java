package com.example.factoryscanner.ui.inventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Activity_114_leltar extends AppCompatActivity {
    EditText Batch_Unique_114;
    Button OK;
    TextView Batch_Number;
    TextView Location;
    TextView Supplier_Name;
    TextView Date_Received;
    EditText Quantity_In_Location;
    TextView Stock_UOM;
    TextView Allocated_Quantity;
    TextView Part_Number;
    TextView Part_Description;
    Integer AdjustementQuantity;

    GlobalClass globalVariable;

    private boolean success = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_114_leltar);
        try {
            globalVariable = (GlobalClass) getApplicationContext();
            Batch_Unique_114 = (EditText) findViewById(R.id.edittxt_batch_unique_1114);
            Batch_Unique_114.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (!Batch_Unique_114.getText().toString().trim().equals("")){
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            SyncData orderData = new SyncData();
                            orderData.execute("");
                            return true;
                        }
                    }
                    return false;
                }
            });
            OK = (Button) findViewById(R.id.button_OK_114);
            Part_Number = (TextView) findViewById(R.id.tv_part_number_114);
            Batch_Number = (TextView) findViewById(R.id.tv_batch_number_114);
            Location = (TextView) findViewById(R.id.tv_location_114);
            Supplier_Name = (TextView) findViewById(R.id.tv_supplier_name_114);
            Date_Received = (TextView) findViewById(R.id.tv_date_received_114);
            Quantity_In_Location = (EditText) findViewById(R.id.edittxt_quantity_in_location_114);
            Quantity_In_Location.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (!Quantity_In_Location.getText().toString().trim().equals("")){
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            AdjustementQuantity = (Integer.parseInt(Quantity_In_Location.getText().toString())-(Integer.parseInt(globalVariable.getGlobal_Settings("Quantity_in_Location"))));
                            globalVariable.setGlobal_settings("Batch_Unique",Batch_Unique_114.getText().toString());
                            SyncData2 orderData2 = new SyncData2();
                            orderData2.execute("");

                            return true;
                        }
                    }
                    return false;
                }
            });
            Part_Description = (TextView) findViewById(R.id.tv_part_description_114);
            Stock_UOM = (TextView) findViewById(R.id.tv_stock_UOM_114);
            final Button Button_GOIN = (Button) findViewById(R.id.btn_GOIN_11111);
            OK.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!Quantity_In_Location.getText().toString().trim().equals("")){
                        AdjustementQuantity = (Integer.parseInt(Quantity_In_Location.getText().toString())-(Integer.parseInt(globalVariable.getGlobal_Settings("Quantity_in_Location"))));
                        globalVariable.setGlobal_settings("Batch_Unique",Batch_Unique_114.getText().toString());
                        SyncData2 orderData2 = new SyncData2();
                        orderData2.execute("");
                    }



                }
            });

        } catch (Exception ex) {
            Toast.makeText(Activity_114_leltar.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_114_leltar.this, getString(R.string.Data_Refress),
                    getString(R.string.Just_moment), true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Connection conn = globalVariable.CONN(); //Connection Object
                if (conn == null) {
                    success = false;
                    msg = getString(R.string.connection_error);
                } else {
                    // Change below query according to your own database.
                    String query = globalVariable.getSql_command("114");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,Batch_Unique_114.getText().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                globalVariable.setGlobal_settings("Batch_Unique",rs.getString("Batch_Unique"));
                                globalVariable.setGlobal_settings("Location",rs.getString("Location"));
                                globalVariable.setGlobal_settings("Batch_Number",rs.getString("Batch_Number"));
                                globalVariable.setGlobal_settings("Part_Number",rs.getString("Part_Number"));
                                globalVariable.setGlobal_settings("Part_Description",rs.getString("Part_Description"));
                                globalVariable.setGlobal_settings("Quantity_in_Location",rs.getString("Quantity_in_Location"));
                                globalVariable.setGlobal_settings("Stock_UOM",rs.getString("Stock_UOM"));
                                globalVariable.setGlobal_settings("Allocated_Quantity",rs.getString("Allocated_Quantity"));
                                globalVariable.setGlobal_settings("Date_Received",rs.getString("Date_Received"));
                                globalVariable.setGlobal_settings("Supplier_Name",rs.getString("Supplier_Name"));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                msg = ex.getMessage();
                            }
                        }

                        success = true;
                    } else {
                        msg = getString(R.string.No_data_found);
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
            if (!success) {
                Toast.makeText(Activity_114_leltar.this, msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    Location.setText(globalVariable.getGlobal_Settings("Location"));
                    Batch_Number.setText(globalVariable.getGlobal_Settings("Batch_Number"));
                    Part_Number.setText(globalVariable.getGlobal_Settings("Part_Number"));
                    Part_Description.setText(globalVariable.getGlobal_Settings("Part_Description"));
                    Quantity_In_Location.setText(globalVariable.getGlobal_Settings("Quantity_in_Location"));
                    Stock_UOM.setText(globalVariable.getGlobal_Settings("Stock_UOM"));
                    Date_Received.setText(globalVariable.getGlobal_Settings("Date_Received"));
                    Supplier_Name.setText(globalVariable.getGlobal_Settings("Supplier_Name"));
                } catch (Exception ex) {
                    Toast.makeText(Activity_114_leltar.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class SyncData2 extends AsyncTask<String, String, String>
    {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_114_leltar.this, getString(R.string.Data_Refress),
                    getString(R.string.Just_moment),true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try
            {
                Connection conn = globalVariable.CONN(); //Connection Object
                if (conn == null)
                {
                    success = false;
                    msg = getString(R.string.connection_error);
                }
                else {

                    String query = globalVariable.getSql_command("1141");

                    String Employee_Ref = globalVariable.getUser();


                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setString("p_batchNumber",globalVariable.getGlobal_Settings("Batch_Unique"));
                    cs.setString("p_Location",Location.getText().toString());
                    cs.setInt("p_Adjustment_Quantity",AdjustementQuantity);
                    cs.setString("p_owner",Employee_Ref);
                    cs.setString("p_source",Employee_Ref);

                    cs.execute();

                    success = true;

                }
            } catch (Exception e)
            {
                msg = e.getMessage();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg)
        {
            progress.dismiss();
            if (!success)
            {
                Toast.makeText(Activity_114_leltar.this, msg, Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                try {
                    Intent openGoin = new Intent(Activity_114_leltar.this, MainActivity.class);
                    Activity_114_leltar.this.startActivity(openGoin);
                    Toast.makeText(Activity_114_leltar.this, "Leltár módosítva:" + AdjustementQuantity, Toast.LENGTH_SHORT).show();
                    finish();


                } catch (Exception ex)
                {
                    Toast.makeText(Activity_114_leltar.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
