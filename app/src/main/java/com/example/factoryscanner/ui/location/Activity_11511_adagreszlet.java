package com.example.factoryscanner.ui.location;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.R;

public class Activity_11511_adagreszlet extends AppCompatActivity {

    GlobalClass globalVariable;
    TextView Batch_Number;
    TextView Location;
    TextView Supplier_Name;
    TextView Date_Received;
    TextView Quantity_In_Location;
    TextView Stock_UOM;
    TextView Allocated_Quantity;
    TextView Part_Number;

    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_11511_adagreszlet);
        try {
            globalVariable = (GlobalClass) getApplicationContext();
            Part_Number = (TextView) findViewById(R.id.tv_part_number_11511);
            Batch_Number = (TextView) findViewById(R.id.tv_batch_number_11511);
            Location = (TextView) findViewById(R.id.tv_location_11511);
            Supplier_Name = (TextView) findViewById(R.id.tv_supplier_name_11511);
            Date_Received = (TextView) findViewById(R.id.tv_date_received_11511);
            Quantity_In_Location = (TextView) findViewById(R.id.tv_quantity_in_location11511);
            Stock_UOM = (TextView) findViewById(R.id.tv_stock_UOM_11511);
            Allocated_Quantity = (TextView) findViewById(R.id.tv_allocated_quantity_11511);
            Part_Number.setText(globalVariable.getGlobal_Settings("Part_Number"));
            Batch_Number.setText(globalVariable.getGlobal_Settings("Batch_Number"));
            Location.setText(globalVariable.getGlobal_Settings("Location"));
            Supplier_Name.setText(globalVariable.getGlobal_Settings("Supplier_Name"));
            Date_Received.setText(globalVariable.getGlobal_Settings("Date_Received"));
            Quantity_In_Location.setText(globalVariable.getGlobal_Settings("Quantity_in_Location"));
            Stock_UOM.setText(globalVariable.getGlobal_Settings("Stock_UOM"));
            Allocated_Quantity.setText(globalVariable.getGlobal_Settings("Allocated_Quantity"));

        }
        catch (Exception ex)
        {
            Toast.makeText(Activity_11511_adagreszlet.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
