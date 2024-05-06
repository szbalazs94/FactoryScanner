package com.example.factoryscanner.ui.issuing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
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

public class Activity_11312_adagkivalasztas extends AppCompatActivity {
    TextView Works_Order_Number,Description;
    GlobalClass globalVariable;
    private ListView ListView_11312;
    private ArrayList<Class_11312_item_list> ItemArrayList_11312;
    private MyAppAdapter myAppAdapter;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_11312_adagkivalasztas);
            globalVariable = (GlobalClass) getApplicationContext();
            Works_Order_Number = (TextView) findViewById(R.id.tv_works_order_number_11312);
            Description = (TextView) findViewById(R.id.tv_description_11312);
            Works_Order_Number.setText(globalVariable.getGlobal_Settings("Works_Order_Input"));
            Description.setText(globalVariable.getGlobal_Settings("Description"));
            ItemArrayList_11312 = new ArrayList<Class_11312_item_list>();
            ListView_11312.setAdapter(myAppAdapter);

            SyncData orderData = new SyncData();
            orderData.execute("");
        }  catch (Exception ex)
        {
            Toast.makeText(Activity_11312_adagkivalasztas.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_11312_adagkivalasztas.this, getString(R.string.Data_Refress),
                    getString(R.string.Just_moment),
                    true);
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
                    // Change below query according to your own database.
                    String query = globalVariable.getSql_command("11312");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setInt(1, globalVariable.getGlobal_int("Component_Unique"));
                    ResultSet rs = stmt.executeQuery();

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                ItemArrayList_11312.add(new Class_11312_item_list(
                                        rs.getString("Component_Unique"),
                                        rs.getString("Batch_Number"),
                                        rs.getString("Location"),
                                        rs.getString("Supplier"),
                                        rs.getString("Date_Recd"),
                                        rs.getString("Quantity_in_Location"),
                                        rs.getString("Allocated_Qty"),
                                        rs.getString("Stock_Measure"),
                                        rs.getFloat("Issue_Quantity"),
                                        rs.getString("Material_Type"),
                                        rs.getString("Batch_Unique")));
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
            } catch (Exception e)
            {
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
                Toast.makeText(Activity_11312_adagkivalasztas.this, msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_11312, Activity_11312_adagkivalasztas.this);
                    ListView_11312.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_11312.setAdapter(myAppAdapter);
                } catch (Exception ex)
                {
                    Toast.makeText(Activity_11312_adagkivalasztas.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }



    }
    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder {
            TextView Batch_Number;
            TextView Location;
            TextView Supplier;
            TextView Date_Recd;
            TextView Quantity_in_Location;
            TextView Allocated_Quantity;
            TextView Stock_Measure;
            EditText Issue_Quantity;
            Button Kiadas;
        }

        public List<Class_11312_item_list> parkingList;

        public Context context;
        ArrayList<Class_11312_item_list> arraylist;

        private MyAppAdapter(List<Class_11312_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_11312_item_list>();
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
                rowView = inflater.inflate(R.layout.activity_11312_adagkivalasztas_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.Batch_Number = (TextView) rowView.findViewById(R.id.tv_batch_number_11312);
                viewHolder.Location = (TextView) rowView.findViewById(R.id.tv_location_11312);
                viewHolder.Supplier = (TextView) rowView.findViewById(R.id.tv_supplier_11312);
                viewHolder.Date_Recd = (TextView) rowView.findViewById(R.id.tv_date_recd_11312);
                viewHolder.Quantity_in_Location = (TextView) rowView.findViewById(R.id.tv_quantity_in_location_11312);
                viewHolder.Allocated_Quantity = (TextView) rowView.findViewById(R.id.tv_allocated_quantity_11312);
                viewHolder.Stock_Measure= (TextView) rowView.findViewById(R.id.tv_stock_measure_11312);
                viewHolder.Issue_Quantity = (EditText) rowView.findViewById(R.id.tv_edit_text_test);
                viewHolder.Kiadas = (Button) rowView.findViewById(R.id.btn_kiadas_11312);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.Batch_Number.setText(parkingList.get(position).getBatch_Number());
            viewHolder.Location.setText(parkingList.get(position).getLocation());
            viewHolder.Supplier.setText(parkingList.get(position).getSupplier());
            viewHolder.Date_Recd.setText(parkingList.get(position).getDate_Recd());
            viewHolder.Quantity_in_Location.setText(parkingList.get(position).getQuantity_in_Location());
            viewHolder.Allocated_Quantity.setText(parkingList.get(position).getAllocated_Qty());
            viewHolder.Stock_Measure.setText(parkingList.get(position).getStock_Measure());
            viewHolder.Issue_Quantity.setText(parkingList.get(position).getIssue_Quantity().toString());

            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.Kiadas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Double.parseDouble(parkingList.get(position).getIssue_Quantity().toString())!=0.0);
                    {
                        final Editable Issue_Quantity_Text = finalViewHolder.Issue_Quantity.getText();
                        globalVariable.setGlobal_settings("Material_Type",parkingList.get(position).getMaterial_Type());
                        globalVariable.setGlobal_int("Batch_Unique",Integer.parseInt(parkingList.get(position).getBatch_Unique()));
                        globalVariable.setGlobal_settings("Location",parkingList.get(position).getLocation());
                        globalVariable.setGlobal_settings("Issue_Quantity",parkingList.get(position).getIssue_Quantity().toString());
                        SyncData2 orderData2 = new SyncData2();
                        orderData2.execute("");

                    }



                }
            });

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
            progress = ProgressDialog.show(Activity_11312_adagkivalasztas.this, getString(R.string.Data_Refress),
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

                    String query = globalVariable.getSql_command("11313");
                    String Employee_Ref = globalVariable.getUser();

                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setString("p_Material_Type",globalVariable.getGlobal_Settings("Material_Type"));
                    cs.setDouble("p_worksOrderNo",Double.parseDouble(globalVariable.getGlobal_Settings("Works_Order_Input")));
                    cs.setInt("p_Component_Unique",globalVariable.getGlobal_int("Component_Unique"));
                    cs.setInt("p_Batch_Unique",globalVariable.getGlobal_int("Batch_Unique"));
                    cs.setString("p_Location",globalVariable.getGlobal_Settings("Location"));
                    cs.setDouble("p_Issue_Quantity",Double.parseDouble(globalVariable.getGlobal_Settings("Issue_Quantity")));
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
                Toast.makeText(Activity_11312_adagkivalasztas.this, msg, Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                try {
                    Intent openGoin = new Intent(Activity_11312_adagkivalasztas.this, Activity_11311_kiadmlreszlet.class);
                    Activity_11312_adagkivalasztas.this.startActivity(openGoin);
                    finish();
                    Toast.makeText(Activity_11312_adagkivalasztas.this, "Kiadás megtörtént!", Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(Activity_11312_adagkivalasztas.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
