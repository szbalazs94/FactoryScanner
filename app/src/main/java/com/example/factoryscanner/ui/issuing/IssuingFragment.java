package com.example.factoryscanner.ui.issuing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.factoryscanner.databinding.FragmentIssuingBinding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IssuingFragment extends Fragment {

    private FragmentIssuingBinding binding;
    GlobalClass globalVariable;
    private MainActivity mainActivity;
    private EditText editTextWorksOrderNumber;
    private ImageButton scanWorksOrderNumber;
    private ArrayList<Class_11311_item_list> ItemArrayList_11311;
    private ProgressBar progressBar;
    private boolean success;
    private ListView ListView_11311;
    private MyAppAdapter myAppAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IssuingViewModel issuingViewModel =
                new ViewModelProvider(this).get(IssuingViewModel.class);

        binding = FragmentIssuingBinding.inflate(inflater, container, false);
        View inflated = inflater.inflate(R.layout.activity_1131_kiadmlvalasztas, container, false);
        mainActivity = ((MainActivity) getActivity());
        globalVariable = GlobalClass.getInstance();
        editTextWorksOrderNumber = inflated.findViewById(R.id.editText_1131);
        scanWorksOrderNumber = inflated.findViewById(R.id.scan_works_order_number_1131);
        progressBar = inflated.findViewById(R.id.progress_bar_1131);
        progressBar.setVisibility(View.INVISIBLE);
        globalVariable = GlobalClass.getInstance();
        editTextWorksOrderNumber.setText(MainActivity.sharedPreferences.getString("Works_Order_Input",""));
        ListView_11311 = inflated.findViewById(R.id.list_1131);
        ItemArrayList_11311 = new ArrayList<Class_11311_item_list>();
        ListView_11311.setAdapter(myAppAdapter);
        if (!editTextWorksOrderNumber.getText().toString().equals("")){
            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();
        }
        scanWorksOrderNumber.setOnClickListener(v -> {
            Intent intent = new Intent(IssuingFragment.this.getContext(), CustomScannerActivity.class);
            scanBarcodeActivityResultLauncher.launch(intent);
        });

        editTextWorksOrderNumber.setOnEditorActionListener((v, actionId, event) -> {
            if ( (actionId == EditorInfo.IME_ACTION_DONE) || actionId == 5 || event.getAction() == KeyEvent.ACTION_DOWN) {
                InputMethodManager inputManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(inflated.getWindowToken(), 0);
                MainActivity.sharedPreferences.edit().putString("Works_Order_Input",editTextWorksOrderNumber.getEditableText().toString()).apply();
                SyncData orderData = new SyncData(mainActivity);
                orderData.execute();
                ItemArrayList_11311.clear();
                progressBar.setVisibility(View.VISIBLE);

                return true;

            }

            return false;
        });

        final TextView textView = binding.textIssuingFragment;
        issuingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return inflated;
    }

    private final ActivityResultLauncher<Intent> scanBarcodeActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        editTextWorksOrderNumber.setText(MainActivity.sharedPreferences.getString("Barcode_Result",""));
                        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                        editTextWorksOrderNumber.dispatchKeyEvent(event);
                    }
                }
            });

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

    private class SyncData extends BackgroundTask
    {
        String msg = "";

        public SyncData(Activity activity) {
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
                    // Change below query according to your own database.
                    String query = globalVariable.getSql_command("11311");
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, String.valueOf(editTextWorksOrderNumber.getText()).trim());
                    stmt.setString(2,"");
                    stmt.setString(3,"");
                    stmt.setString(4,"");
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
        public void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {
            progressBar.setVisibility(View.INVISIBLE);

            if (!success)
            {
                Toast.makeText(IssuingFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_11311, IssuingFragment.this.getContext());
                    ListView_11311.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_11311.setAdapter(myAppAdapter);
                } catch (Exception ex)
                {
                    Toast.makeText(IssuingFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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
            TextView Quantity_In_Location;
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
            MyAppAdapter.ViewHolder viewHolder = null;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.activity_11311_kiadasmlreszlet_list, parent, false);
                viewHolder = new MyAppAdapter.ViewHolder();
                viewHolder.Material_Type = rowView.findViewById(R.id.tv_material_type_11311);
                viewHolder.Part_Number = rowView.findViewById(R.id.tv_part_number_11311);
                viewHolder.Description = rowView.findViewById(R.id.tv_description_11311);
                viewHolder.Issued_Qty = rowView.findViewById(R.id.tv_issued_qty_11311);
                viewHolder.Still_Required_Qty= rowView.findViewById(R.id.tv_still_required_qty_11311);
                viewHolder.AnyagKiadas = rowView.findViewById(R.id.button_anyag_kiadas_11311);
                viewHolder.Quantity_In_Location = rowView.findViewById(R.id.quantity_in_location_11311);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (MyAppAdapter.ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.Material_Type.setText(parkingList.get(position).getMaterial_Type());
            if (viewHolder.Material_Type.getText().equals("MA")) {
                viewHolder.Material_Type.setBackgroundColor(Color.argb(255,255,128,128));
            } else if (viewHolder.Material_Type.getText().equals("RM")) {
                viewHolder.Material_Type.setBackgroundColor(Color.argb(255,196,136,255));
            } else if (viewHolder.Material_Type.getText().equals("BI")) {
                viewHolder.Material_Type.setBackgroundColor(Color.YELLOW);
            }
            viewHolder.Part_Number.setText(parkingList.get(position).getPart_Number());
            viewHolder.Description.setText(parkingList.get(position).getDescription());
            viewHolder.Issued_Qty.setText(parkingList.get(position).getIssued_Qty());
            viewHolder.Still_Required_Qty.setText(parkingList.get(position).getStill_Quantity_Reqd_UOM().toString());
            viewHolder.Quantity_In_Location.setText(parkingList.get(position).getQuantity_In_Location());
            if (parkingList.get(position).getQC().equals("R") ){
                viewHolder.Quantity_In_Location.setBackgroundColor(Color.argb(255,229,23,63));
            }
            if (parkingList.get(position).getQC().equals("G") ){
                viewHolder.Quantity_In_Location.setBackgroundColor(Color.argb(255,128,255,128));
            }
            if (parkingList.get(position).getQC().equals("Y") ){
                viewHolder.Quantity_In_Location.setBackgroundColor(Color.argb(255,240,159,11));
            }
            viewHolder.AnyagKiadas.setOnClickListener(v -> {
                MainActivity.sharedPreferences.edit().putString("Description",parkingList.get(position).getDescription()).apply();
                MainActivity.sharedPreferences.edit().putInt("Component_Unique",Integer.parseInt(parkingList.get(position).getComponent_Unique())).apply();
                MainActivity.sharedPreferences.edit().putString("Material_Type",parkingList.get(position).getMaterial_Type()).apply();
                Navigation.findNavController(v).navigate(R.id.action_nav_issuing_to_quantity_pick_fragment);
            });

            return rowView;
        }


    }
}
