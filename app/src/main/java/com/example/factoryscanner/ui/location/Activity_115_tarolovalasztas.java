package com.example.factoryscanner.ui.location;

import static android.graphics.Color.parseColor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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

public class Activity_115_tarolovalasztas extends AppCompatActivity {
    private ListView ListView_115;
    GlobalClass globalVariable;
    TextView textView;
    private ArrayList<Class_115_item_list> ItemArrayList_115;
    private MyAppAdapter myAppAdapter;
    private ArrayAdapter taroloAdapter;
    private boolean success = false;
    EditText filterTarolohely;
    ImageView imagesSearch;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_115_tarolovalasztas);
        try {
            globalVariable = (GlobalClass) getApplicationContext();
            imagesSearch =(ImageView) findViewById(R.id.imageView_search_115);

            filterTarolohely = (EditText) findViewById(R.id.editText_tarolohely_115);
            ItemArrayList_115 = new ArrayList<Class_115_item_list>();
            SyncData orderData = new SyncData();
            orderData.execute("");
            filterTarolohely.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ItemArrayList_115 = new ArrayList<Class_115_item_list>();
                    imagesSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SyncData orderData = new SyncData();
                            orderData.execute("");
                        }
                    });
                    filterTarolohely.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (!filterTarolohely.getText().toString().trim().equals("")){
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
            Toast.makeText(Activity_115_tarolovalasztas.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private class SyncData extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(Activity_115_tarolovalasztas.this, getString(R.string.Data_Refress),
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
                    String query = globalVariable.getSql_command("115");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,(filterTarolohely.getEditableText().toString()));
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_115.add(new Class_115_item_list(rs.getString("Location"), rs.getString("Notes"), rs.getString("Location_Type"), rs.getString("Warehouse")));
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
                Toast.makeText(Activity_115_tarolovalasztas.this, msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_115, Activity_115_tarolovalasztas.this);
                    ListView_115.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_115.setAdapter(myAppAdapter);
                } catch (Exception ex) {
                    Toast.makeText(Activity_115_tarolovalasztas.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public class MyAppAdapter extends BaseAdapter  {

        public class ViewHolder {
            TextView Location;
            TextView Notes;
            TextView Location_Type;
            TextView Warehouse;
        }
        public List<Class_115_item_list> parkingList;
        public Context context;
        ArrayList<Class_115_item_list> arraylist;

        private MyAppAdapter(List<Class_115_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_115_item_list>();
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
                rowView = inflater.inflate(R.layout.activity_115_tarolovalasztas_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.Location = (TextView) rowView.findViewById(R.id.btn_location);
                viewHolder.Notes = (TextView) rowView.findViewById(R.id.tv_notes);
                viewHolder.Warehouse = (TextView) rowView.findViewById(R.id.tv_warehouse);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.Location.setText(parkingList.get(position).getLocation());
            viewHolder.Notes.setText(parkingList.get(position).getNotes());
            viewHolder.Warehouse.setText(parkingList.get(position).getWarehouse());

            if (!parkingList.get(position).getLocation_Type().trim().equals(""))
            {
                viewHolder.Location.setBackgroundColor(Color.RED);
            }
            else {
                viewHolder.Location.setBackgroundColor(parseColor("#E0E0E0"));
            }


            viewHolder.Location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    globalVariable.setGlobal_settings("Location",parkingList.get(position).getLocation());
                    Intent eszkozvalasztas = new Intent(Activity_115_tarolovalasztas.this, Activity_1151_taroloreszlet.class);

                    Activity_115_tarolovalasztas.this.startActivity(eszkozvalasztas);
                }
            });

            return rowView;
        }

    }
}
