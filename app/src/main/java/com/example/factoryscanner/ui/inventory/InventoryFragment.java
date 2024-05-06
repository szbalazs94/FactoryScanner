package com.example.factoryscanner.ui.inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.factoryscanner.databinding.FragmentInventoryBinding;
import com.example.factoryscanner.ui.picklist.PickListViewModel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;
    private MainActivity mainActivity;
    private GlobalClass globalVariable;
    private boolean success = false;
    private ProgressBar progressBar;
    EditText Batch_Unique_114;
    Button OK;
    ImageButton scanBarcode;
    TextView Batch_Number;
    TextView Location;
    TextView Supplier_Name;
    TextView Date_Received;
    EditText Quantity_In_Location;
    TextView Stock_UOM;
    TextView Allocated_Quantity;
    TextView Part_Number;
    TextView Part_Description;
    private Integer Label;
    private CheckBox cb_label;
    float adjustmentQuantity;
    private View inflated;
    private Class_114_item_list ItemArrayList_114;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PickListViewModel pickListViewModel =
                new ViewModelProvider(this).get(PickListViewModel.class);

        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        inflated = inflater.inflate(R.layout.activity_114_leltar, container, false);

        try {
            globalVariable = GlobalClass.getInstance();
            mainActivity = ((MainActivity) getActivity());
            progressBar = inflated.findViewById(R.id.progress_bar_114);
            Batch_Unique_114 = inflated.findViewById(R.id.edittxt_batch_unique_1114);
            scanBarcode = inflated.findViewById(R.id.scan_114);
            progressBar.setVisibility(View.INVISIBLE);
            ItemArrayList_114 = new Class_114_item_list();
            Batch_Unique_114.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    int keyEventCode = 0;
                    if (event != null) {
                        keyEventCode = event.getAction();
                    } else {
                        keyEventCode = 66;
                    }
                    if ( (actionId == EditorInfo.IME_ACTION_DONE) || keyEventCode == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_NEXT) {
                        InputMethodManager inputManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(inflated.getWindowToken(), 0);
                        SyncData orderData = new SyncData(mainActivity);
                        orderData.execute();
                        progressBar.setVisibility(View.VISIBLE);

                        return true;

                    }

                    return false;
                }
            });
            cb_label = inflated.findViewById(R.id.cb_label_114);
            cb_label.setChecked(MainActivity.sharedPreferences.getBoolean("label_print_inventory",false));
            OK = inflated.findViewById(R.id.button_OK_114);
            Part_Number = inflated.findViewById(R.id.tv_part_number_114);
            Batch_Number = inflated.findViewById(R.id.tv_batch_number_114);
            Location = inflated.findViewById(R.id.tv_location_114);
            Supplier_Name = inflated.findViewById(R.id.tv_supplier_name_114);
            Date_Received = inflated.findViewById(R.id.tv_date_received_114);
            Quantity_In_Location = inflated.findViewById(R.id.edittxt_quantity_in_location_114);
            Quantity_In_Location.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (!Quantity_In_Location.getText().toString().trim().equals("")){
                        if ((keyCode == EditorInfo.IME_ACTION_DONE) || keyCode == EditorInfo.IME_ACTION_NEXT) {
                            adjustmentQuantity = (Float.parseFloat(Quantity_In_Location.getText().toString())-(Float.parseFloat(globalVariable.getGlobal_Settings("Quantity_in_Location"))));
                            globalVariable.setGlobal_settings("Batch_Unique_114",Batch_Unique_114.getText().toString());
                            SyncData2 orderData2 = new SyncData2(mainActivity);
                            orderData2.execute();

                            return true;
                        }
                    }
                    return false;
                }
            });

            scanBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InventoryFragment.this.getContext(), CustomScannerActivity.class);
                    scanBarcodeActivityResultLauncher.launch(intent);

                }
            });
            Batch_Unique_114.setText(MainActivity.sharedPreferences.getString("Batch_Unique_114",""));
            Part_Description = inflated.findViewById(R.id.tv_part_description_114);
            Stock_UOM = inflated.findViewById(R.id.tv_stock_UOM_114);
            final Button Button_GOIN = inflated.findViewById(R.id.btn_GOIN_11111);
            OK.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!Quantity_In_Location.getText().toString().trim().equals("")){
                        Double s = Double.valueOf((Quantity_In_Location.getText().toString()));
                        Double p = Double.valueOf(ItemArrayList_114.Quantity_In_Location);
                        adjustmentQuantity = (Float.parseFloat(Quantity_In_Location.getText().toString())-Float.parseFloat(ItemArrayList_114.Quantity_In_Location));
                        MainActivity.sharedPreferences.edit().remove("Batch_Unique_114").apply();
                        MainActivity.sharedPreferences.edit().putString("Batch_Unique_114",Batch_Unique_114.getText().toString()).apply();
                        SyncData2 orderData2 = new SyncData2(mainActivity);
                        orderData2.execute();
                    }



                }
            });

        } catch (Exception ex) {
            Toast.makeText(InventoryFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        final TextView textView = binding.textInventory;
        pickListViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return inflated;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar();
    }

    private final ActivityResultLauncher<Intent> scanBarcodeActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Batch_Unique_114.setText(MainActivity.sharedPreferences.getString("Barcode_Result",""));
                        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                        Batch_Unique_114.dispatchKeyEvent(event);
                    }
                }
            });

    private void setUpToolbar() {
        mainActivity.setSupportActionBar(mainActivity.toolbar);
        NavController navController = NavHostFragment.findNavController(this);
        AppBarConfiguration mAppBarConfiguration = mainActivity.mAppBarConfiguration;
        NavigationUI.setupWithNavController(mainActivity.toolbar, navController, mAppBarConfiguration);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class SyncData extends BackgroundTask {
        String msg = "";

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
                        String query = globalVariable.getSql_command("114");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,Batch_Unique_114.getText().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_114 = new Class_114_item_list(
                                        rs.getString("Batch_Unique"),
                                        rs.getString("Location"),
                                        rs.getString("Batch_Number"),
                                        rs.getString("Part_Number"),
                                        rs.getString("Part_Description"),
                                        rs.getString("Quantity_In_Location"),
                                        rs.getString("Stock_UOM"),
                                        rs.getString("Allocated_Quantity"),
                                        rs.getString("Date_Received"),
                                        rs.getString("Supplier_Name"));

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
                Toast.makeText(InventoryFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    Location.setText(ItemArrayList_114.getLocation());
                    Batch_Number.setText(ItemArrayList_114.getBatch_Number());
                    Part_Number.setText(ItemArrayList_114.getPart_Number());
                    Part_Description.setText(ItemArrayList_114.getPart_Description());
                    Quantity_In_Location.setText(ItemArrayList_114.getQuantity_In_Location());
                    Stock_UOM.setText(ItemArrayList_114.getStock_UOM());
                    Date_Received.setText(ItemArrayList_114.getDate_Received());
                    Supplier_Name.setText(ItemArrayList_114.getSupplier_Name());
                } catch (Exception ex) {
                    Toast.makeText(InventoryFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
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

                    String query = globalVariable.getSql_command("1141");

                    String Employee_Ref = globalVariable.getUser();

                    if(cb_label.isChecked()) Label = 1;
                    else Label = 0;


                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setString("p_batchNumber",MainActivity.sharedPreferences.getString("Batch_Unique_114",""));
                    cs.setString("p_Location",Location.getText().toString());
                    cs.setFloat("p_Adjustment_Quantity", adjustmentQuantity);
                    cs.setString("p_owner",Employee_Ref);
                    cs.setString("p_source",Employee_Ref);
                    cs.setInt("p_label",Label);

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
            progressBar.setVisibility(View.INVISIBLE);
            if (!success)
            {
                Toast.makeText(InventoryFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    Navigation.findNavController(inflated).navigate(R.id.action_nav_inventory_to_nav_home);
                    Toast.makeText(InventoryFragment.this.getContext(), "Leltár módosítva:" + adjustmentQuantity, Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(InventoryFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
