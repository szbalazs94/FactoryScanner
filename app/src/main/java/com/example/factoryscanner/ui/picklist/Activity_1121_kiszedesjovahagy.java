package com.example.factoryscanner.ui.picklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

public class Activity_1121_kiszedesjovahagy extends AppCompatActivity {

    TextView Pick_List_1121;
    TextView Part_Number_1121;
    Button Submit, Cancel;

    GlobalClass globalVariable;
    private ListView ListView_1121;
    private ArrayList<Class_1121_item_list> ItemArrayList_1121;
    private MyAppAdapter myAppAdapter;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1121_kiszedesjovahagy);

        try {
            Pick_List_1121 = (TextView) findViewById(R.id.tv_pick_list_1121);
            Part_Number_1121 = (TextView) findViewById(R.id.tv_part_number_1121);
           // Submit = (Button) findViewById(R.id.button_submit_1121);
            Cancel = (Button) findViewById(R.id.button_cancel_1121);
            Cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent openMain = new Intent(Activity_1121_kiszedesjovahagy.this, MainActivity.class);
                    Activity_1121_kiszedesjovahagy.this.startActivity(openMain);
                    finish();

                }
            });


            globalVariable = (GlobalClass) getApplicationContext();
            ItemArrayList_1121 = new ArrayList<Class_1121_item_list>();
            Part_Number_1121.setText(globalVariable.getGlobal_Settings("Part_Number"));
            Pick_List_1121.setText(globalVariable.getGlobal_Settings("Pick_List"));
            ListView_1121.setAdapter(myAppAdapter);
            SyncData orderData = new SyncData();
            orderData.execute("");
        } catch (Exception ex) {
            Toast.makeText(Activity_1121_kiszedesjovahagy.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_1121_kiszedesjovahagy.this, getString(R.string.Data_Refress),
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
                    String query = globalVariable.getSql_command("1121");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,Pick_List_1121.getText().toString());
                    stmt.setString(2,Part_Number_1121.getText().toString());
                    stmt.setString(3,globalVariable.getGlobal_Settings("Issue_Number"));
                    stmt.setString(4,Pick_List_1121.getText().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_1121.add(new Class_1121_item_list(
                                        rs.getString("Batch_Unique"),
                                        rs.getString("Location"),
                                        rs.getString("Batch_Number"),
                                        rs.getString("Date_Received"),
                                        rs.getString("Supplier"),
                                        rs.getString("Quantity_In_Location"),
                                        rs.getString("Stock_UOM"),
                                        rs.getString("Quantity_To_Pick"),
                                        rs.getString("Allocated_Quantity")));
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
                Toast.makeText(Activity_1121_kiszedesjovahagy.this, msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_1121, Activity_1121_kiszedesjovahagy.this);
                    ListView_1121.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_1121.setAdapter(myAppAdapter);
                } catch (Exception ex) {
                    Toast.makeText(Activity_1121_kiszedesjovahagy.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder {

            TextView Supplier_Name, Quantity_In_Location, Stock_UOM, Batch_Number, Location, Date_Received, Allocated_Quantity;
            EditText Quantity_To_Pick;
            Button Submit;


        }

        public List<Class_1121_item_list> parkingList;

        public Context context;
        ArrayList<Class_1121_item_list> arraylist;

        private MyAppAdapter(List<Class_1121_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_1121_item_list>();
            arraylist.addAll(parkingList);
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
        {

            View rowView = convertView;
            ViewHolder viewHolder = null;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.activity_1121_kiszedesjovahagy_list, parent, false);
                viewHolder = new ViewHolder();
             //   viewHolder.Supplier_Name = (TextView) rowView.findViewById(R.id.tv_supplier_name_1121);
                viewHolder.Quantity_In_Location = (TextView) rowView.findViewById(R.id.tv_quantity_in_location_1121);
             //   viewHolder.Stock_UOM = (TextView) rowView.findViewById(R.id.tv_stock_UOM_1121);
             //   viewHolder.Batch_Number = (TextView) rowView.findViewById(R.id.tv_batch_number_1121);
             //   viewHolder.Location = (TextView) rowView.findViewById(R.id.tv_location_1121);
                viewHolder.Date_Received = (TextView) rowView.findViewById(R.id.tv_date_1121);
              //  viewHolder.Allocated_Quantity = (TextView) rowView.findViewById(R.id.textView79);
                viewHolder.Quantity_To_Pick = (EditText) rowView.findViewById(R.id.tv_quantity_to_pick_1121);
                viewHolder.Submit = (Button) rowView.findViewById(R.id.button_submit_1121);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
           // viewHolder.Supplier_Name.setText(parkingList.get(position).getSupplier_Name());
            viewHolder.Quantity_In_Location.setText("Készlet: " + parkingList.get(position).getQuantity_In_Location().toString() + " Kiszedett: " );
           // viewHolder.Stock_UOM.setText(parkingList.get(position).getStock_UOM());
          //  viewHolder.Batch_Number.setText(parkingList.get(position).getBatch_Number());
           // viewHolder.Location.setText(parkingList.get(position).getLocation());
            viewHolder.Date_Received.setText("Érkezett: " + parkingList.get(position).getDate_Received() + parkingList.get(position).getSupplier_Name());
          //  viewHolder.Allocated_Quantity.setText("Kiszedett: (" + parkingList.get(position).getAllocated_Quantity() + ")");
            viewHolder.Quantity_To_Pick.setText(parkingList.get(position).getQuantity_To_Pick());
            viewHolder.Submit.setText(parkingList.get(position).getLocation() + "   (" + parkingList.get(position).getBatch_Number() + ")");

            viewHolder.Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(parkingList.get(position).getQuantity_In_Location())!=0)
                    {
                        globalVariable.setGlobal_settings("Location",parkingList.get(position).getLocation());
                        globalVariable.setGlobal_settings("Batch_Unique",parkingList.get(position).getBatch_Unique());
                        globalVariable.setGlobal_settings("Quantity_To_Pick",parkingList.get(position).getQuantity_To_Pick());
                        SyncData2 orderData2 = new SyncData2();
                        orderData2.execute("");
                    }
                }
            });


            if (!parkingList.get(position).getQuantity_To_Pick().trim().equals("0"))
            {
                viewHolder.Submit.setBackgroundColor(Color.GREEN);
            }

            return rowView;
        }
    }
    private class SyncData2 extends AsyncTask<String, String, String>
    {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_1121_kiszedesjovahagy.this, getString(R.string.Data_Refress),
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

                    String query = globalVariable.getSql_command("11211");

                    String Employee_Ref = globalVariable.getUser();

                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setInt("p_PicklistNo",Integer.parseInt(Pick_List_1121.getText().toString()));
                    cs.setString("p_Location",globalVariable.getGlobal_Settings("Location"));
                    cs.setString("p_BatchUniqueStr",globalVariable.getGlobal_Settings("Batch_Unique"));
                    cs.setDouble("p_QtyIssued",Double.parseDouble(globalVariable.getGlobal_Settings("Quantity_To_Pick")));
                    cs.setString("p_serialNumber","");
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
                Toast.makeText(Activity_1121_kiszedesjovahagy.this, msg, Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                try {
                    Intent Open112 = new Intent(Activity_1121_kiszedesjovahagy.this, Activity_112_kiszedestetelek.class);
                    Activity_1121_kiszedesjovahagy.this.startActivity(Open112);
                    finish();


                } catch (Exception ex)
                {
                    Toast.makeText(Activity_1121_kiszedesjovahagy.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
