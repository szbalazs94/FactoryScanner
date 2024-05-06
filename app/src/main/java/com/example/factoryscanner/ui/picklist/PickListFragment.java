package com.example.factoryscanner.ui.picklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.CustomScannerActivity;
import com.example.factoryscanner.databinding.FragmentPicklistBinding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PickListFragment extends Fragment {

    private FragmentPicklistBinding binding;
    TextView textView;
    EditText PickList;
    GlobalClass globalVariable;
    Button ButtonConfirmAll;
    ImageButton imageButton;
    private ListView ListView_112;
    private ArrayList<Class_112_item_list> ItemArrayList_112;
    private MyAppAdapter myAppAdapter;
    private MainActivity mainActivity;
    private ProgressBar progressBar;
    private boolean success = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PickListViewModel pickListViewModel =
                new ViewModelProvider(this).get(PickListViewModel.class);

        binding = FragmentPicklistBinding.inflate(inflater, container, false);
        View inflated = inflater.inflate(R.layout.activity_112_kiszedestetelek, container, false);

        try {
            PickList = inflated.findViewById(R.id.edittxt_pick_list);
            ButtonConfirmAll = inflated.findViewById(R.id.button_confirm_all_112);
            mainActivity = ((MainActivity) getActivity());
            progressBar = inflated.findViewById(R.id.progress_bar_112);
            imageButton = inflated.findViewById(R.id.scan_112);
            progressBar.setVisibility(View.INVISIBLE);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PickListFragment.this.getContext(), CustomScannerActivity.class);
                    scanBarcodeActivityResultLauncher.launch(intent);

                }
            });
            PickList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))) {
                        InputMethodManager inputManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(inflated.getWindowToken(), 0);
                        SyncData orderData = new SyncData(mainActivity);
                        orderData.execute();
                        ButtonConfirmAll.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        return true;

                    }

                    return false;
                }
            });


            globalVariable = GlobalClass.getInstance();
            ListView_112 = inflated.findViewById(R.id.list_112);
            globalVariable = GlobalClass.getInstance();
            ItemArrayList_112 = new ArrayList<Class_112_item_list>();
            ListView_112.setAdapter(myAppAdapter);
            ButtonConfirmAll.setVisibility(View.INVISIBLE);


        } catch (Exception ex) {
            Toast.makeText(PickListFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        final TextView textView = binding.textSlideshow;
        pickListViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return inflated;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar();
    }

    private void setUpToolbar() {
        mainActivity.setSupportActionBar(mainActivity.toolbar);
        NavController navController = NavHostFragment.findNavController(this);
        AppBarConfiguration mAppBarConfiguration = mainActivity.mAppBarConfiguration;
        NavigationUI.setupWithNavController(mainActivity.toolbar, navController, mAppBarConfiguration);


    }

    private final ActivityResultLauncher<Intent> scanBarcodeActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        PickList.setText(MainActivity.sharedPreferences.getString("Barcode_Result",""));
                        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                        PickList.dispatchKeyEvent(event);
                    }
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class SyncData extends BackgroundTask {
        String msg = "";
        ProgressBar progress;

        public SyncData(Activity activity) {
            super(activity);
        }

        @Override
        public String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                globalVariable.get_preferences(mainActivity.getApplicationContext());
                Connection conn = globalVariable.CONN(); //Connection Object
                if (conn == null) {
                    success = false;
                    msg = getString(R.string.connection_error);
                } else {
                    // Change below query according to your own database.
                    String query = globalVariable.getSql_command("112");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    String s = PickList.getText().toString();
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
        public void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {
            progressBar.setVisibility(View.INVISIBLE);
            if (!success) {
                Toast.makeText(PickListFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_112, PickListFragment.this.getContext());
                    ListView_112.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_112.setAdapter(myAppAdapter);
                    ButtonConfirmAll.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            SyncData2 orderData2 = new SyncData2(mainActivity);
                            orderData2.execute();

                        }
                    });
                } catch (Exception ex) {
                    Toast.makeText(PickListFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
            PickListFragment.MyAppAdapter.ViewHolder viewHolder = null;
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
                viewHolder = (MyAppAdapter.ViewHolder) convertView.getTag();
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
                    MainActivity.sharedPreferences.edit().putString("Pick_List",PickList.getText().toString()).apply();
                    MainActivity.sharedPreferences.edit().putString("Part_Number",parkingList.get(position).getPart_Number()).apply();
                    MainActivity.sharedPreferences.edit().putString("Issue_Number",parkingList.get(position).getIssue_Number()).apply();

                    Navigation.findNavController(v).navigate(R.id.action_nav_list_to_pickListConfirmFragment);

                }
            });

            return rowView;
        }
    }

    private class SyncData2 extends BackgroundTask
    {
        String msg = "";

        public SyncData2(Activity activity) {
            super(activity);
        }

        @Override
        public String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try
            {
                globalVariable.get_preferences(mainActivity.getApplicationContext());
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
        public void onPostExecute(String msg)
        {
            progressBar.setVisibility(View.GONE);
            if (!success)
            {
                Toast.makeText(PickListFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    //TODO
                    // Intent open11 = new Intent(Activity_112_kiszedestetelek.this, Activity_11_funkciovalasztas.class);
                    // Activity_112_kiszedestetelek.this.startActivity(open11);


                } catch (Exception ex)
                {
                    Toast.makeText(PickListFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}