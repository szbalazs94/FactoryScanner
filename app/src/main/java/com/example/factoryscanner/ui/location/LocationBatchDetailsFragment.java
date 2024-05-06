package com.example.factoryscanner.ui.location;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.databinding.FragmentLocationBatchDetailsBinding;
import com.example.factoryscanner.databinding.FragmentLocationDetailsBinding;
import com.example.factoryscanner.ui.home.HomeFragment;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;

public class LocationBatchDetailsFragment extends Fragment {

    GlobalClass globalVariable;
    TextView Batch_Number;
    TextView Location;
    TextView Supplier_Name;
    TextView Date_Received;
    TextView Quantity_In_Location;
    TextView Stock_UOM;
    TextView Allocated_Quantity;
    TextView Part_Number;
    private TextView barcode;
    private TextView barcodeString;
    Button labelPrinting;

    private boolean success = false;

    private FragmentLocationBatchDetailsBinding binding;
    private MainActivity mainActivity;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocationViewModel pickListViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

        binding = FragmentLocationBatchDetailsBinding.inflate(inflater, container, false);
        View inflated = inflater.inflate(R.layout.activity_11511_adagreszlet, container, false);
        mainActivity = ((MainActivity) getActivity());

        try {
            globalVariable = GlobalClass.getInstance();
            labelPrinting = inflated.findViewById(R.id.bt_label_11511);
            progressBar = inflated.findViewById(R.id.progress_bar_11511);
            barcode = inflated.findViewById(R.id.textViewBarcode);
            barcodeString = inflated.findViewById(R.id.tv_barcode_string_11511);
            Part_Number = inflated.findViewById(R.id.tv_part_number_11511);
            Batch_Number = inflated.findViewById(R.id.tv_batch_number_11511);
            Location = inflated.findViewById(R.id.tv_location_11511);
            Supplier_Name = inflated.findViewById(R.id.tv_supplier_name_11511);
            Date_Received = inflated.findViewById(R.id.tv_date_received_11511);
            Quantity_In_Location = inflated.findViewById(R.id.tv_quantity_in_location11511);
            Stock_UOM = inflated.findViewById(R.id.tv_stock_UOM_11511);
            Allocated_Quantity = inflated.findViewById(R.id.tv_allocated_quantity_11511);
            barcodeString.setText(MainActivity.sharedPreferences.getString("BarCodeString",""));
            Part_Number.setText(MainActivity.sharedPreferences.getString("Part_Number",""));
            Batch_Number.setText(MainActivity.sharedPreferences.getString("Batch_Number",""));
            Location.setText(MainActivity.sharedPreferences.getString("Location",""));
            Supplier_Name.setText(MainActivity.sharedPreferences.getString("Supplier_Name",""));
            Date_Received.setText(MainActivity.sharedPreferences.getString("Date_Received",""));
            Quantity_In_Location.setText(MainActivity.sharedPreferences.getString("Quantity_in_Location",""));
            Stock_UOM.setText(MainActivity.sharedPreferences.getString("Stock_UOM",""));
            Allocated_Quantity.setText(MainActivity.sharedPreferences.getString("Allocated_Quantity",""));
            progressBar.setVisibility(View.INVISIBLE);

        }
        catch (Exception ex)
        {
            Toast.makeText(LocationBatchDetailsFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        labelPrinting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SyncDataLabelPrint labelPrint = new SyncDataLabelPrint(mainActivity);
                labelPrint.execute();
            }
        });


        final TextView textView = binding.textLocationBatchDetailsFragment;
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

                    String query = globalVariable.getSql_command("91");

                    String Employee_Ref = globalVariable.getUser();


                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setString("p_batchNumber",barcodeString.getText().toString());
                    cs.setString("p_Location",Location.getText().toString());
                    cs.setFloat("p_Quantity", Float.parseFloat(Quantity_In_Location.getText().toString()));
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
                Toast.makeText(LocationBatchDetailsFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    Toast.makeText(LocationBatchDetailsFragment.this.getContext(), "Címke kinyomtatva!", Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(LocationBatchDetailsFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
