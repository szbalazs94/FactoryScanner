package com.example.factoryscanner.ui.purchase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.databinding.FragmentPurchaseItemDetailsBinding;

import java.sql.CallableStatement;
import java.sql.Connection;

public class PurchaseItemDetailsFragment extends Fragment {

    GlobalClass globalVariable;
    TextView Purchase_Order_No_TextView;
    TextView textView;
    TextView Item;
    TextView Date;
    TextView Stock_Type_Code;
    TextView Part_No;
    TextView Part_Description;
    TextView Qty_Outstanding;
    TextView Order_Unit;
    EditText Batch_No;
    EditText GOIN;
    EditText Location;
    EditText Memo;
    Button Button_GOIN;
    Integer Label;
    CheckBox cb_label;
    String adviceNoteNumber;
    ProgressBar progressBar;
    private boolean success = false;

    private FragmentPurchaseItemDetailsBinding binding;
    private View inflated;
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPurchaseItemDetailsBinding.inflate(inflater, container, false);
        inflated = inflater.inflate(R.layout.activity_11111_bmtetelekreszlet, container, false);
        mainActivity = ((MainActivity) getActivity());

        try {
            textView = inflated.findViewById(R.id.tv_supplier_code_11111);
            Purchase_Order_No_TextView = inflated.findViewById(R.id.tv_po_number_11111);
            globalVariable = GlobalClass.getInstance();
            Date = inflated.findViewById(R.id.tv_date_11111);
            Stock_Type_Code = inflated.findViewById(R.id.tv_stock_type_code_11111);
            Part_No = inflated.findViewById(R.id.tv_part_no_11111);
            Part_Description = inflated.findViewById(R.id.tv_part_description_11111);
            Qty_Outstanding = inflated.findViewById(R.id.tv_qty_outstanding_11111);
            Batch_No = inflated.findViewById(R.id.edittxt_batch_no_11111);
            GOIN = inflated.findViewById(R.id.edittxt_goin_11111);
            Location = inflated.findViewById(R.id.edittxt_location_11111);
            Memo = inflated.findViewById(R.id.edittxt_memo_11111);


            cb_label = inflated.findViewById(R.id.cb_label);
            progressBar = inflated.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.INVISIBLE);
            final Button Button_GOIN = inflated.findViewById(R.id.btn_GOIN_11111);
            Button_GOIN.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!Location.getText().toString().trim().equals(""))
                    {
                        SyncData2 orderData2 = new SyncData2(mainActivity);
                        orderData2.execute();
                        progressBar.setVisibility(View.VISIBLE);
                    }



                }
            });

            cb_label.setChecked(MainActivity.sharedPreferences.getBoolean("label_print_purchase",false));
            textView.setText(MainActivity.sharedPreferences.getString("Supplier_Name",""));
            Purchase_Order_No_TextView.setText(String.valueOf(MainActivity.sharedPreferences.getInt("Purchase_Order_No", 0)) + "/" + String.valueOf(MainActivity.sharedPreferences.getInt("Item", 0)));
            Date.setText(MainActivity.sharedPreferences.getString("Date",""));
            Stock_Type_Code.setText(MainActivity.sharedPreferences.getString("Stock_Type_Code",""));
            Part_No.setText(MainActivity.sharedPreferences.getString("Part_No",""));
            Part_Description.setText(MainActivity.sharedPreferences.getString("Part_Description",""));
            Qty_Outstanding.setText(MainActivity.sharedPreferences.getString("Qty_Outstanding",""));
            Batch_No.setText(MainActivity.sharedPreferences.getString("Batch_No",""));
            GOIN.setText(String.valueOf(MainActivity.sharedPreferences.getInt("GOIN",0)));
            Location.setText(MainActivity.sharedPreferences.getString("Location",""));
            adviceNoteNumber = MainActivity.sharedPreferences.getString("Advice_Note_No", "");



        }
        catch (Exception ex)
        {
            Toast.makeText(this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        return inflated;
    }

    private void setUpToolbar() {

        mainActivity.setSupportActionBar(mainActivity.toolbar);
        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupActionBarWithNavController(mainActivity,navController);
        NavigationUI.setupWithNavController(mainActivity.navigationView,navController);
        mainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentDescription = "Open navigation drawer";
                if( !view.getContentDescription().toString().equals(contentDescription)){
                    navController.navigateUp();

                } else {
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mainActivity, mainActivity.drawer, mainActivity.toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    toggle.setDrawerIndicatorEnabled(true);
                }

            }
        });

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

                    String query = globalVariable.getSql_command("111111");

                    String Employee_Ref = globalVariable.getUser();

                    if(cb_label.isChecked()) Label = 1;
                    else Label = 0;

                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setInt("p_PONumber",MainActivity.sharedPreferences.getInt("Purchase_Order_No", 0));
                    cs.setInt("p_POItemNumber",MainActivity.sharedPreferences.getInt("Item", 0));
                    cs.setString("p_Part_Number",(Part_No.getText().toString()));
                    cs.setString("p_AdviceNoteNo",adviceNoteNumber);
                    cs.setString("p_batchNumber",Batch_No.getText().toString());
                    cs.setString("p_location",Location.getText().toString());
                    cs.setDouble("p_QtyReceived",Double.parseDouble(GOIN.getText().toString()));
                    cs.setString("p_ExpiryDate","");
                    cs.setString("p_SerialNumber","");
                    cs.setString("p_Memo_Field",Memo.getText().toString());
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
                Toast.makeText(PurchaseItemDetailsFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    Navigation.findNavController(inflated).navigate(R.id.action_purchaseItemDetailsFragment_to_purchaseItemFragment);


                } catch (Exception ex)
                {
                    Toast.makeText(PurchaseItemDetailsFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
