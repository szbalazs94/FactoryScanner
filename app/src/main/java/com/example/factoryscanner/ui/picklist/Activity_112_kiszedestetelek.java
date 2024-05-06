package com.example.factoryscanner.ui.picklist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
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

public class Activity_112_kiszedestetelek extends AppCompatActivity {

    TextView textView;
    EditText PickList;
    GlobalClass globalVariable;
    Button ButtonConfirmAll;
    private ListView ListView_112;
    private ArrayList<Class_112_item_list> ItemArrayList_112;
    private MyAppAdapter myAppAdapter;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_112_kiszedestetelek);

        try {
            PickList = (EditText) findViewById(R.id.edittxt_pick_list);
            ButtonConfirmAll = (Button) findViewById(R.id.button_confirm_all_112);
            PickList.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (!PickList.getText().toString().trim().equals("")){
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            SyncData orderData = new SyncData();
                            orderData.execute("");

                        }
                    }
                    return false;
                }
            });
            ButtonConfirmAll.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SyncData2 orderData2 = new SyncData2();
                    orderData2.execute("");

                }
            });
            globalVariable = (GlobalClass) getApplicationContext();
            textView.setText(globalVariable.getEmployee_name());

            if (globalVariable.getGlobal_Settings("Pick_List")!="")
            {
                PickList.setText(globalVariable.getGlobal_Settings("Pick_List"));
                SyncData orderData = new SyncData();
                orderData.execute("");

            }
            ListView_112 = (ListView) findViewById(R.id.list_112);
            globalVariable = (GlobalClass) getApplicationContext();
            ItemArrayList_112 = new ArrayList<Class_112_item_list>();

            ListView_112.setAdapter(myAppAdapter);

        } catch (Exception ex) {
            Toast.makeText(Activity_112_kiszedestetelek.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_112_kiszedestetelek.this, getString(R.string.Data_Refress),
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
                    String query = globalVariable.getSql_command("112");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,PickList.getText().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_112.add(new Class_112_item_list(
                                        rs.getString("Del_Plan_No"),
                                        rs.getString("Sales_Order_Number"),
                                        rs.getString("Sales_Order_Item_No"),
                                        rs.getString("Customer_Account_Code"),
                                        rs.getString("Part_Number"),
                                        rs.getString("Stock_Description"),
                                        rs.getString("Issue_Number"),
                                        rs.getString("Qty_Required"),
                                        rs.getString("Qty_Allocated"),
                                        rs.getString("Ship_Req_Date"),
                                        rs.getString("Location")));
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
                Toast.makeText(Activity_112_kiszedestetelek.this, msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_112, Activity_112_kiszedestetelek.this);
                    ListView_112.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_112.setAdapter(myAppAdapter);
                    ButtonConfirmAll.setVisibility(View.VISIBLE) ;
                } catch (Exception ex) {
                    Toast.makeText(Activity_112_kiszedestetelek.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder {
            TextView Part_Number;
            TextView Stock_Description;
            TextView Sales_Order_Number, Sales_Order_Item_No, Customer_Account_Code, Issue_Number, Qty_Required, Qty_Allocated, Ship_Req_Date, Location;


        }

        public List<Class_112_item_list> parkingList;

        public Context context;
        ArrayList<Class_112_item_list> arraylist;

        private MyAppAdapter(List<Class_112_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_112_item_list>();
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
            rowView = inflater.inflate(R.layout.activity_112_kiszedestetelek_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.Part_Number = (TextView) rowView.findViewById(R.id.btn_part_number_112);
            viewHolder.Stock_Description = (TextView) rowView.findViewById(R.id.tv_stock_description_112);
            viewHolder.Sales_Order_Number = (TextView) rowView.findViewById(R.id.tv_sales_order_number_112);
            viewHolder.Sales_Order_Item_No = (TextView) rowView.findViewById(R.id.tv_sales_order_item_no_112);
            viewHolder.Customer_Account_Code = (TextView) rowView.findViewById(R.id.tv_customer_account_code_112);
            viewHolder.Issue_Number = (TextView) rowView.findViewById(R.id.tv_issue_number_112);
            viewHolder.Qty_Required = (TextView) rowView.findViewById(R.id.tv_quantity_required_112);
            viewHolder.Qty_Allocated = (TextView) rowView.findViewById(R.id.tv_quantity_allocated_112);
            viewHolder.Ship_Req_Date = (TextView) rowView.findViewById(R.id.tv_ship_req_date_112);
            viewHolder.Location = (TextView) rowView.findViewById(R.id.tv_location_112);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // here setting up names and images
        viewHolder.Part_Number.setText(parkingList.get(position).getPart_Number());
        viewHolder.Stock_Description.setText(parkingList.get(position).getStock_Description());
        viewHolder.Sales_Order_Number.setText(parkingList.get(position).getSales_Order_Number());
        viewHolder.Sales_Order_Item_No.setText(parkingList.get(position).getSales_Order_Item_No());
        viewHolder.Customer_Account_Code.setText(parkingList.get(position).getCustomer_Account_Code());
        viewHolder.Issue_Number.setText(parkingList.get(position).getIssue_Number());
        viewHolder.Qty_Required.setText(parkingList.get(position).getQty_Required());
        viewHolder.Qty_Allocated.setText(parkingList.get(position).getQty_Allocated());
        viewHolder.Ship_Req_Date.setText(parkingList.get(position).getShip_Req_Date());
        viewHolder.Location.setText(parkingList.get(position).getLocation());

        viewHolder.Part_Number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.setGlobal_settings("Pick_List",PickList.getText().toString());
                globalVariable.setGlobal_settings("Part_Number",parkingList.get(position).getPart_Number());
                globalVariable.setGlobal_settings("Issue_Number",parkingList.get(position).getIssue_Number());
                Intent openJovahagyas = new Intent(Activity_112_kiszedestetelek.this, Activity_1121_kiszedesjovahagy.class);

                Activity_112_kiszedestetelek.this.startActivity(openJovahagyas);

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
            progress = ProgressDialog.show(Activity_112_kiszedestetelek.this, getString(R.string.Data_Refress),
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

                    String query = globalVariable.getSql_command("1122");

                    String Employee_Ref = globalVariable.getUser();

                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setInt("p_picklistNo",Integer.parseInt(PickList.getText().toString()));
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
                Toast.makeText(Activity_112_kiszedestetelek.this, msg, Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                try {


                } catch (Exception ex)
                {
                    Toast.makeText(Activity_112_kiszedestetelek.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

