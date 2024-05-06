package com.example.factoryscanner.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.CustomScannerActivity;
import com.example.factoryscanner.databinding.FragmentHomeBinding;
import com.example.factoryscanner.ui.inventory.Class_114_item_list;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static SharedPreferences sharedPreferences;
    private View inflated;
    private MainActivity mainActivity;
    private GlobalClass globalVariable;
    private boolean success = false;
    private ProgressBar progressBar;
    private EditText firstScanResult;
    private EditText secondScanResult;
    private Button buttonPicklistHome;
    private Button buttonIssuingHome;
    private Button buttonLabelPrint;
    private ImageButton scanBarcodeFirst;
    private ImageButton scanBarcodeSecond;
    TextView Batch_Number;
    private EditText Location;
    EditText Quantity;
    TextView Stock_UOM;
    TextView Part_Number;
    TextView Part_Description;

    private Class_114_item_list ItemArrayList_114;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        inflated = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = binding.textHome;

        PreferenceManager.setDefaultValues(this.getContext(), R.xml.root_preferences, true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        textView.setText(sharedPreferences.getString("employee_name",""));
        try {
            globalVariable = GlobalClass.getInstance();
            mainActivity = ((MainActivity) getActivity());
            progressBar = inflated.findViewById(R.id.progress_bar_home);
            firstScanResult = inflated.findViewById(R.id.first_scan_result);
            secondScanResult = inflated.findViewById(R.id.second_scan_result);
            scanBarcodeFirst = inflated.findViewById(R.id.scan_1);
            scanBarcodeSecond = inflated.findViewById(R.id.scan_2);
            progressBar.setVisibility(View.INVISIBLE);
            ItemArrayList_114 = new Class_114_item_list();
            firstScanResult.setOnEditorActionListener((v, actionId, event) -> {
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
            });
            secondScanResult.setOnEditorActionListener((v, actionId, event) -> {
                int keyEventCode = 0;
                if (event != null) {
                    keyEventCode = event.getAction();
                } else {
                    keyEventCode = 66;
                }
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || keyEventCode == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager inputManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(inflated.getWindowToken(), 0);
                    if(buttonLabelPrint.isEnabled() && !secondScanResult.getText().toString().isEmpty()) {
                        buttonPicklistHome.setEnabled(success);
                        buttonIssuingHome.setEnabled(success);
                    }
                    return true;

                }

                return false;
            });
            buttonLabelPrint = inflated.findViewById(R.id.bt_label_home);
            buttonLabelPrint.setEnabled(false);
            buttonIssuingHome = inflated.findViewById(R.id.button_issuing_home);
            buttonIssuingHome.setEnabled(false);
            buttonPicklistHome = inflated.findViewById(R.id.button_picklist_home);
            buttonPicklistHome.setEnabled(false);
            Part_Number = inflated.findViewById(R.id.tv_part_number_home);
            Part_Description = inflated.findViewById(R.id.tv_part_description_home);
            Batch_Number = inflated.findViewById(R.id.tv_batch_number_home);
            Stock_UOM = inflated.findViewById(R.id.tv_stock_UOM_home);
            Location = inflated.findViewById(R.id.tv_location_home);
            Quantity = inflated.findViewById(R.id.edittxt_quantity_in_location_home);
            Quantity.setOnEditorActionListener((v, actionId, event) -> {
                if ( (actionId == EditorInfo.IME_ACTION_DONE)|| actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager inputManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(inflated.getWindowToken(), 0);
                    return true;

                }

                return false;
            });
            scanBarcodeFirst.setOnClickListener(v -> scanFromFragmentFirst(view));

            scanBarcodeSecond.setOnClickListener(v -> scanFromFragmentSecond(view));

            buttonLabelPrint.setOnClickListener(v -> {
                SyncDataLabelPrint labelPrint = new SyncDataLabelPrint(mainActivity);
                labelPrint.execute();
            });
            buttonPicklistHome.setOnClickListener(v -> {
                SyncDataPickList pickList = new SyncDataPickList(mainActivity);
                pickList.execute();
            });
            buttonIssuingHome.setOnClickListener(v -> {
                SyncDataIssuing issuing = new SyncDataIssuing(mainActivity);
                issuing.execute();
            });

        } catch (Exception ex) {
            Toast.makeText(HomeFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return inflated;
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncherFirst = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("HomeFragment", "Cancelled scan");
                        Toast.makeText(HomeFragment.this.getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("HomeFragment", "Cancelled scan due to missing camera permission");
                        Toast.makeText(HomeFragment.this.getContext(), "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("HomeFragment", "Scanned");
                    Toast.makeText(HomeFragment.this.getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    MainActivity.sharedPreferences.edit().putString("Barcode_Result",result.getContents()).apply();
                    firstScanResult.setText(MainActivity.sharedPreferences.getString("Barcode_Result",""));
                }
            });

    private final ActivityResultLauncher<ScanOptions> barcodeLauncherSecond = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("HomeFragment", "Cancelled scan");
                        Toast.makeText(HomeFragment.this.getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("HomeFragment", "Cancelled scan due to missing camera permission");
                        Toast.makeText(HomeFragment.this.getContext(), "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("HomeFragment", "Scanned");
                    Toast.makeText(HomeFragment.this.getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    MainActivity.sharedPreferences.edit().putString("Barcode_Result",result.getContents()).apply();
                    secondScanResult.setText(MainActivity.sharedPreferences.getString("Barcode_Result",""));
                }
            });

    public void scanFromFragmentFirst(View view) {
        ScanOptions options = new ScanOptions().setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class);
        barcodeLauncherFirst.launch(options);
    }

    public void scanFromFragmentSecond(View view) {
        ScanOptions options = new ScanOptions().setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class);
        barcodeLauncherSecond.launch(options);
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
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1,firstScanResult.getText().toString());
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
                if (e.getMessage().equals("Invalid batch unique number")) {
                    msg = "Érvénytelen vonalkód!";
                    return msg;
                } else msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        public void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {
            progressBar.setVisibility(View.INVISIBLE);
            buttonLabelPrint.setEnabled(success);
            if (!success) {
                Toast.makeText(HomeFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    Location.setText(ItemArrayList_114.getLocation());
                    Batch_Number.setText(ItemArrayList_114.getBatch_Number());
                    Part_Number.setText(ItemArrayList_114.getPart_Number());
                    Part_Description.setText(ItemArrayList_114.getPart_Description());
                    Quantity.setText(ItemArrayList_114.getQuantity_In_Location());
                    Stock_UOM.setText(ItemArrayList_114.getStock_UOM());
                } catch (Exception ex) {
                    Toast.makeText(HomeFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class SyncDataLabelPrint extends BackgroundTask
    {
        String msg = "";

        public SyncDataLabelPrint(Activity activity) {
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

                    String quantity = Quantity.getText().toString();

                    String query = globalVariable.getSql_command("91");

                    String Employee_Ref = globalVariable.getUser();


                    CallableStatement cs = conn.prepareCall(query);
                    cs.setString("p_batchNumber",firstScanResult.getText().toString());
                    cs.setString("p_Location",Location.getText().toString());
                    cs.setFloat("p_Quantity", Float.parseFloat(Quantity.getText().toString()));
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
            progressBar.setVisibility(View.INVISIBLE);
            if (!success)
            {
                Toast.makeText(HomeFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    Toast.makeText(HomeFragment.this.getContext(), "Címke kinyomtatva!", Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(HomeFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class SyncDataPickList extends BackgroundTask
    {
        String msg = "";

        public SyncDataPickList(Activity activity) {
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

                    String query = globalVariable.getSql_command("92");

                    String Employee_Ref = globalVariable.getUser();


                    CallableStatement cs = conn.prepareCall(query);
                    cs.setString("p_batchNumber",firstScanResult.getText().toString());
                    cs.setString("p_Location",Location.getText().toString());
                    cs.setFloat("p_Quantity", Float.parseFloat(Quantity.getText().toString()));
                    cs.setString("p_Barcode_Result",secondScanResult.getText().toString());
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
            progressBar.setVisibility(View.INVISIBLE);
            if (!success)
            {
                Toast.makeText(HomeFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    Toast.makeText(HomeFragment.this.getContext(), "Kiadás sikeres!", Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(HomeFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class SyncDataIssuing extends BackgroundTask
    {
        String msg = "";

        public SyncDataIssuing(Activity activity) {
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

                    String query = globalVariable.getSql_command("93");

                    String Employee_Ref = globalVariable.getUser();


                    CallableStatement cs = conn.prepareCall(query);
                    cs.setString("p_batchNumber",firstScanResult.getText().toString());
                    cs.setString("p_Location",Location.getText().toString());
                    cs.setFloat("p_Quantity", Float.parseFloat(Quantity.getText().toString()));
                    cs.setString("p_Barcode_Result",secondScanResult.getText().toString());
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
            progressBar.setVisibility(View.INVISIBLE);
            if (!success)
            {
                Toast.makeText(HomeFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    Toast.makeText(HomeFragment.this.getContext(), "Munkalapra kiadás sikeres!", Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(HomeFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}