package com.example.factoryscanner.ui.picklist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.databinding.FragmentPurchaseItemBinding;
import com.example.factoryscanner.ui.purchase.PurchaseViewModel;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PickListConfirmFragment extends Fragment {

    private View inflated;
    private FragmentPurchaseItemBinding binding;
    private MainActivity mainActivity;
    private ProgressBar progressBar;
    protected CustomAdapter customAdapter;
    protected RecyclerView recycleView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String [] mDataset;
    TextView Pick_List_1121;
    TextView Part_Number_1121;
    Button Submit, Cancel;

    GlobalClass globalVariable;
    private ArrayList<Class_1121_item_list> ItemArrayList_1121;
    private boolean success = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PurchaseViewModel purchaseViewModel =
                new ViewModelProvider(this).get(PurchaseViewModel.class);

        binding = FragmentPurchaseItemBinding.inflate(inflater, container, false);
        inflated = inflater.inflate(R.layout.activity_1121_kiszedesjovahagy, container, false);
        recycleView = inflated.findViewById(R.id.recycle_view1121);
        mLayoutManager = new LinearLayoutManager(mainActivity);
        recycleView.setLayoutManager(mLayoutManager);
        ItemArrayList_1121 = new ArrayList<Class_1121_item_list>();
        customAdapter = new CustomAdapter(ItemArrayList_1121);
        recycleView.setAdapter(customAdapter);
        mainActivity = ((MainActivity) getActivity());

        try {
            Pick_List_1121 = inflated.findViewById(R.id.tv_pick_list_1121);
            Part_Number_1121 = inflated.findViewById(R.id.tv_part_number_1121);
            // Submit = (Button) findViewById(R.id.button_submit_1121);
            progressBar = inflated.findViewById(R.id.progress_bar_1121);
            progressBar.setVisibility(View.VISIBLE);
            Cancel = inflated.findViewById(R.id.button_cancel_1121);
            Cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Navigation.findNavController(inflated).navigate(R.id.action_pickListConfirmFragment_to_nav_list);

                }
            });

            globalVariable = GlobalClass.getInstance();
            ItemArrayList_1121 = new ArrayList<Class_1121_item_list>();
            Part_Number_1121.setText(MainActivity.sharedPreferences.getString("Part_Number",""));
            Pick_List_1121.setText(MainActivity.sharedPreferences.getString("Pick_List",""));
            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();
        } catch (Exception ex) {
            Toast.makeText(PickListConfirmFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        final TextView textView = binding.textPurchaseItem;
        purchaseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return inflated;
    }

    private void initDataset() {
        mDataset = new String[60];
        for (int i = 0; i < 60; i++) {
            mDataset[i] = "This is element #" + i;
        }
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
                    String query = globalVariable.getSql_command("1121");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,Pick_List_1121.getText().toString());
                    stmt.setString(2,Part_Number_1121.getText().toString());
                    stmt.setString(3,MainActivity.sharedPreferences.getString("Issue_Number",""));
                    stmt.setString(4,Pick_List_1121.getText().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_1121.add(new Class_1121_item_list(
                                        rs.getString("Batch_Unique"),
                                        rs.getString("Location"),
                                        rs.getString("Batch_Number"),
                                        rs.getString("Date_Received"),
                                        rs.getString("Supplier_Name"),
                                        rs.getString("Quantity_In_Location"),
                                        rs.getString("Stock_UOM"),
                                        rs.getString("Quantity_To_Pick"),
                                        rs.getString("Allocated_Quantity")));
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
                Toast.makeText(PickListConfirmFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    recycleView = inflated.findViewById(R.id.recycle_view1121);
                    customAdapter = new CustomAdapter(ItemArrayList_1121);
                    recycleView.setAdapter(customAdapter);
                    recycleView.setLayoutManager(mLayoutManager);

                } catch (Exception ex) {
                    Toast.makeText(PickListConfirmFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<Class_1121_item_list> localDataSet;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private static final String TAG = "Custom adapter";
            private final TextView Quantity_In_Location, Date_Received, SupplierName;
            private final EditText Quantity_To_Pick;
            private final Button Submit;

            public ViewHolder(View view) {
                super(view);

                Quantity_In_Location = (TextView) view.findViewById(R.id.tv_quantity_in_location_1121);
                Date_Received = (TextView) view.findViewById(R.id.tv_date_1121);
                SupplierName = (TextView) view.findViewById(R.id.tv_supplier_name_1121);
                Quantity_To_Pick = (EditText) view.findViewById(R.id.tv_quantity_to_pick_1121);
                Submit = (Button) view.findViewById(R.id.button_submit_1121);


                // Define click listener for the ViewHolder's View
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Element" + getAdapterPosition() + "clicked");
                    }
                });
            }



            public TextView getQuantity_In_Location() {
                return Quantity_In_Location;
            }

            public TextView getDate_Received() {
                return Date_Received;
            }

            public TextView getSupplierName() {
                return SupplierName;
            }

            public EditText getQuantity_To_Pick() {
                return Quantity_To_Pick;
            }

            public Button getSubmit() {
                return Submit;
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public CustomAdapter(ArrayList<Class_1121_item_list> dataSet) {
            if (dataSet != null) {
                localDataSet = dataSet;
            } else
                dataSet = new ArrayList<>();

        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycle_view1121, viewGroup, false);

            return new CustomAdapter.ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.getQuantity_In_Location().setText(String.format("Készlet: %s Kiszedett: ", localDataSet.get(viewHolder.getAdapterPosition()).getQuantity_In_Location()));
            viewHolder.getDate_Received().setText(String.format("Érkezett: %s  ", localDataSet.get(viewHolder.getAdapterPosition()).getDate_Received()));
            viewHolder.getSupplierName().setText(localDataSet.get(viewHolder.getAdapterPosition()).getSupplier_Name());
            viewHolder.getQuantity_To_Pick().setText(localDataSet.get(viewHolder.getAdapterPosition()).getQuantity_To_Pick());
            viewHolder.getSubmit().setText(String.format("%s - %s", localDataSet.get(viewHolder.getAdapterPosition()).getLocation(), localDataSet.get(viewHolder.getAdapterPosition()).getBatch_Number()));

            viewHolder.Submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(localDataSet.get(viewHolder.getAdapterPosition()).getQuantity_In_Location())!=0)
                    {
                        MainActivity.sharedPreferences.edit().putString("Location",localDataSet.get(viewHolder.getAdapterPosition()).getLocation()).apply();
                        MainActivity.sharedPreferences.edit().putString("Batch_Unique",localDataSet.get(viewHolder.getAdapterPosition()).getBatch_Unique()).apply();
                        MainActivity.sharedPreferences.edit().remove("Quantity_To_Pick").apply();
                        MainActivity.sharedPreferences.edit().putString("Quantity_To_Pick", String.valueOf(viewHolder.getQuantity_To_Pick().getText())).apply();
                        SyncData2 orderData2 = new SyncData2(mainActivity);
                        orderData2.execute();
                    }
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }

    class SyncData2 extends BackgroundTask
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

                    String query = globalVariable.getSql_command("11211");

                    String Employee_Ref = globalVariable.getUser();

                    CallableStatement cs = (CallableStatement) conn.prepareCall(query);
                    cs.setInt("p_PicklistNo",Integer.parseInt(Pick_List_1121.getText().toString()));
                    cs.setString("p_Location",MainActivity.sharedPreferences.getString("Location",""));
                    cs.setString("p_BatchUniqueStr",MainActivity.sharedPreferences.getString("Batch_Unique",""));
                    cs.setDouble("p_QtyIssued", Double.parseDouble(MainActivity.sharedPreferences.getString("Quantity_To_Pick","")));
                    cs.setString("p_serialNumber","");
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
                Toast.makeText(PickListConfirmFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    customAdapter = new CustomAdapter(ItemArrayList_1121);
                    recycleView.setAdapter(customAdapter);
                    Navigation.findNavController(inflated).navigate(R.id.action_pickListConfirmFragment_to_nav_list);


                } catch (Exception ex)
                {
                    Toast.makeText(PickListConfirmFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
