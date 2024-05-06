package com.example.factoryscanner.ui.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;

public class Activity_1151_taroloreszlet extends AppCompatActivity {


    TextView Location;

    private ListView ListView_1151;
    GlobalClass globalVariable;
    TextView textView;
    EditText filterPartNumber;
    ImageView imageSearch1151;
    private ArrayList<Class_1151_item_list> ItemArrayList_1151;
    private MyAppAdapter myAppAdapter;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1151_taroloreszlet);
        try {
            globalVariable = (GlobalClass) getApplicationContext();
            Location = (TextView) findViewById(R.id.tv_location_1151);
            filterPartNumber = (EditText) findViewById(R.id.editText_1151);
            imageSearch1151 = (ImageView) findViewById(R.id.imageView_search_1151);
            ItemArrayList_1151 = new ArrayList<Class_1151_item_list>();
            Location.setText(globalVariable.getGlobal_Settings("Location"));
            SyncData orderData = new SyncData();
            orderData.execute("");
            filterPartNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ItemArrayList_1151 = new ArrayList<Class_1151_item_list>();
                    imageSearch1151.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SyncData orderData = new SyncData();
                            orderData.execute("");
                        }
                    });
                    filterPartNumber.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (!filterPartNumber.getText().toString().trim().equals("")){
                                if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                    SyncData orderData = new SyncData();
                                    orderData.execute("");
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(Activity_1151_taroloreszlet.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_1151_taroloreszlet.this, getString(R.string.Data_Refress),
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
                    String query = globalVariable.getSql_command("1151");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,globalVariable.getGlobal_Settings("Location"));
                    stmt.setString(2,filterPartNumber.getEditableText().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_1151.add(new Class_1151_item_list(
                                        rs.getString("Batch_Unique"),
                                        rs.getString("Location"),
                                        rs.getString("Batch_Number"),
                                        rs.getString("Part_Number"),
                                        rs.getString("Part_Description"),
                                        rs.getString("Quantity_In_Location"),
                                        rs.getString("Stock_UOM"),
                                        rs.getString("Allocated_Quantity"),
                                        rs.getString("Supplier_Name"),
                                        rs.getString("Date_Received"), rs.getString("BarCodeString")));
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
                Toast.makeText(Activity_1151_taroloreszlet.this, msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_1151, Activity_1151_taroloreszlet.this);
                    ListView_1151.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_1151.setAdapter(myAppAdapter);
                } catch (Exception ex) {
                    Toast.makeText(Activity_1151_taroloreszlet.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder {
            TextView Batch_Number;
            TextView Part_Number;
            TextView Part_Description;
            TextView Quantity_In_Location;
            TextView Allocated_Quantity;
        }

        public List<Class_1151_item_list> parkingList;

        public Context context;
        ArrayList<Class_1151_item_list> arraylist;

        private MyAppAdapter(List<Class_1151_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_1151_item_list>();
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
                rowView = inflater.inflate(R.layout.activity_1151_taroloreszlet_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.Batch_Number = (TextView) rowView.findViewById(R.id.tv_batch_number_1151);
                viewHolder.Part_Number = (TextView) rowView.findViewById(R.id.tv_part_number_1151);
                viewHolder.Part_Description = (TextView) rowView.findViewById(R.id.tv_quantity_in_location_1121);
                viewHolder.Quantity_In_Location = (TextView) rowView.findViewById(R.id.tv_quantity_in_location_1151);
                viewHolder.Allocated_Quantity = (TextView) rowView.findViewById(R.id.tv_allocated_quantity_1151);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.Batch_Number.setText(parkingList.get(position).getBatch_number());
            viewHolder.Part_Number.setText(parkingList.get(position).getPart_no());
            viewHolder.Part_Description.setText(parkingList.get(position).getPart_description());
            viewHolder.Quantity_In_Location.setText(parkingList.get(position).getQuantity_in_location());
            viewHolder.Allocated_Quantity.setText(parkingList.get(position).getAllocated_quantity());



            viewHolder.Batch_Number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    globalVariable.setGlobal_settings("Part_Number",parkingList.get(position).getPart_no());
                    globalVariable.setGlobal_settings("Batch_Unique",parkingList.get(position).getBatch_unique());
                    globalVariable.setGlobal_settings("Location",parkingList.get(position).getLocation());
                    globalVariable.setGlobal_settings("Batch_Number",parkingList.get(position).getBatch_number());
                    globalVariable.setGlobal_settings("Part_Description",parkingList.get(position).getPart_description());
                    globalVariable.setGlobal_settings("Quantity_in_Location",parkingList.get(position).getQuantity_in_location());
                    globalVariable.setGlobal_settings("Stock_UOM",parkingList.get(position).getStock_UOM());
                    globalVariable.setGlobal_settings("Allocated_Quantity",parkingList.get(position).getAllocated_quantity());
                    globalVariable.setGlobal_settings("Supplier_Name",parkingList.get(position).getSupplier_name());
                    globalVariable.setGlobal_settings("Date_Received",parkingList.get(position).getDate_received());


                    Intent eszkozvalasztas = new Intent(Activity_1151_taroloreszlet.this, Activity_11511_adagreszlet.class);

                    Activity_1151_taroloreszlet.this.startActivity(eszkozvalasztas);

                }
            });

            return rowView;
        }

    }
}
