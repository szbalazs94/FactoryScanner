package com.example.factoryscanner.ui.issuing;

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
import android.widget.Button;
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

public class Activity_11311_kiadmlreszlet extends AppCompatActivity {
    TextView Works_Order_Number;
    GlobalClass globalVariable;
    EditText filterPartNumber;
    ImageView imageSearch;
    private ListView ListView_11311;
    private ArrayList<Class_11311_item_list> ItemArrayList_11311;
    private MyAppAdapter myAppAdapter;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_11311_kiadmlreszlet);
        globalVariable = (GlobalClass) getApplicationContext();
        Works_Order_Number = (TextView) findViewById(R.id.tv_works_order_number_11311);
        Works_Order_Number.setText(globalVariable.getGlobal_Settings("Works_Order_Input"));
        ListView_11311 = (ListView) findViewById(R.id.list_11311);
        filterPartNumber = (EditText) findViewById(R.id.editText_11311);
        imageSearch = (ImageView) findViewById(R.id.imageView_search_11311);
        ItemArrayList_11311 = new ArrayList<Class_11311_item_list>();
        ListView_11311.setAdapter(myAppAdapter);
        SyncData orderData = new SyncData();
            orderData.execute("");
        }  catch (Exception ex)
        {
            Toast.makeText(Activity_11311_kiadmlreszlet.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        filterPartNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ItemArrayList_11311 = new ArrayList<Class_11311_item_list>();
                imageSearch.setOnClickListener(new View.OnClickListener() {
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
    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_11311_kiadmlreszlet.this, getString(R.string.Data_Refress),
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
                    String query = globalVariable.getSql_command("11311");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1, globalVariable.getGlobal_Settings("Works_Order_Input").trim());
                    stmt.setString(2, filterPartNumber.getEditableText().toString());
                    stmt.setString(3, globalVariable.getGlobal_Settings("Material_Type"));
                    stmt.setString(4, globalVariable.getGlobal_Settings("Material_Form"));
                    ResultSet rs = stmt.executeQuery();

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                ItemArrayList_11311.add(new Class_11311_item_list(
                                        rs.getString("LOP_Code"),
                                        rs.getString("Component_Unique"),
                                        rs.getString("Material_Type"),
                                        rs.getString("Part_Number"),
                                        rs.getString("Material_Form"),
                                        rs.getString("Description"),
                                        rs.getString("Quantity_Reqd"),
                                        rs.getString("Issued_Qty"),
                                        rs.getFloat("Still_Quantity_Reqd_UOM"),
                                        rs.getString("Quantity_In_Location"),
                                        rs.getString("QC")));
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
                Toast.makeText(Activity_11311_kiadmlreszlet.this, msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_11311, Activity_11311_kiadmlreszlet.this);
                    ListView_11311.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_11311.setAdapter(myAppAdapter);
                } catch (Exception ex)
                {
                    Toast.makeText(Activity_11311_kiadmlreszlet.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }



    }
    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder {
            TextView LOP_Code;
            TextView Material_Type;
            TextView Part_Number;
            TextView Material_Form;
            TextView Description;
            TextView Issued_Qty;
            TextView Still_Required_Qty;
            Button AnyagKiadas;
        }

        public List<Class_11311_item_list> parkingList;

        public Context context;
        ArrayList<Class_11311_item_list> arraylist;

        private MyAppAdapter(List<Class_11311_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_11311_item_list>();
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
                rowView = inflater.inflate(R.layout.activity_11311_kiadasmlreszlet_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.Material_Type = (TextView) rowView.findViewById(R.id.tv_material_type_11311);
                viewHolder.Part_Number = (TextView) rowView.findViewById(R.id.tv_part_number_11311);
                viewHolder.Description = (TextView) rowView.findViewById(R.id.tv_description_11311);
                viewHolder.Issued_Qty = (TextView) rowView.findViewById(R.id.tv_issued_qty_11311);
                viewHolder.Still_Required_Qty= (TextView) rowView.findViewById(R.id.tv_still_required_qty_11311);
                viewHolder.AnyagKiadas = (Button) rowView.findViewById(R.id.button_anyag_kiadas_11311);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.LOP_Code.setText(parkingList.get(position).getLOP_Code());
            viewHolder.Material_Type.setText(parkingList.get(position).getMaterial_Type());
            viewHolder.Part_Number.setText(parkingList.get(position).getPart_Number());
            viewHolder.Material_Form.setText(parkingList.get(position).getMaterial_Form());
            viewHolder.Description.setText(parkingList.get(position).getDescription());
            viewHolder.Issued_Qty.setText(parkingList.get(position).getIssued_Qty());
            viewHolder.Still_Required_Qty.setText(parkingList.get(position).getStill_Quantity_Reqd_UOM().toString());

            viewHolder.AnyagKiadas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    globalVariable.setGlobal_settings("Description",parkingList.get(position).getDescription());
                    globalVariable.setGlobal_int("Component_Unique",Integer.parseInt(parkingList.get(position).getComponent_Unique()));

                    Intent openAnyagKi = new Intent(Activity_11311_kiadmlreszlet.this, Activity_11312_adagkivalasztas.class);

                    Activity_11311_kiadmlreszlet.this.startActivity(openAnyagKi);
                    //Activity_0231_eszkozvalasztas.start(Activity_023_eszkoz.this,parkingList.get(position).getKategoria());
                }
            });

            return rowView;
        }


    }
}
