package com.example.factoryscanner.ui.issuing;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.databinding.FragmentQuantityPickBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Objects;

public class QuantityPickFragment extends Fragment {

    FragmentQuantityPickBinding binding;
    GlobalClass globalVariable;
    private MainActivity mainActivity;
    private ArrayList<Class_11312_item_list> ItemArrayList_11312;
    private boolean success = false;
    private ProgressBar progress;
    TextView Works_Order_Number,Description;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private CustomAdapter customAdapter;

    private View inflated;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IssuingViewModel issuingViewModel =
                new ViewModelProvider(this).get(IssuingViewModel.class);



        binding = FragmentQuantityPickBinding.inflate(inflater, container, false);
        inflated = inflater.inflate(R.layout.activity_11312_adagkivalasztas, container, false);
        mainActivity = ((MainActivity) getActivity());
        globalVariable = GlobalClass.getInstance();
        recyclerView = inflated.findViewById(R.id.recycler_view_11312);
        mLayoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemArrayList_11312 = new ArrayList<>();
        customAdapter = new CustomAdapter(ItemArrayList_11312);
        recyclerView.setAdapter(customAdapter);
        try {
            progress = inflated.findViewById(R.id.progress_bar_11312);
            Works_Order_Number = inflated.findViewById(R.id.tv_works_order_number_11312);
            Description = inflated.findViewById(R.id.tv_description_11312);
            Works_Order_Number.setText(MainActivity.sharedPreferences.getString("Works_Order_Input",""));
            Description.setText(MainActivity.sharedPreferences.getString("Description",""));

            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();
        }  catch (Exception ex)
        {
            Toast.makeText(QuantityPickFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


        final TextView textView = binding.textQuantityPickFragment;
        issuingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
                    String query = globalVariable.getSql_command("11312");
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, MainActivity.sharedPreferences.getInt("Component_Unique",0));
                    ResultSet rs = stmt.executeQuery();

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                ItemArrayList_11312.add(new Class_11312_item_list(
                                        rs.getString("Component_Unique"),
                                        rs.getString("Batch_Number"),
                                        rs.getString("Location"),
                                        rs.getString("Supplier"),
                                        rs.getString("Date_Recd"),
                                        rs.getString("Quantity_in_Location"),
                                        rs.getString("Allocated_Qty"),
                                        rs.getString("Stock_Measure"),
                                        rs.getFloat("Issue_Quantity"),
                                        rs.getString("Material_Type"),
                                        rs.getString("Batch_Unique")));
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
            progress.setVisibility(View.INVISIBLE);

            if (!success)
            {
                Toast.makeText(QuantityPickFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    customAdapter = new CustomAdapter(ItemArrayList_11312);
                    recyclerView.setAdapter(customAdapter);
                    if (ItemArrayList_11312.isEmpty()){
                        Snackbar snackbar = Snackbar.make(recyclerView,"Nincs szabad készelet vagy teljeses ki van adva!",Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                } catch (Exception ex)
                {
                    Toast.makeText(QuantityPickFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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

                    String query = globalVariable.getSql_command("11313");

                    String Employee_Ref = globalVariable.getUser();



                    CallableStatement cs = conn.prepareCall(query);
                    cs.setString("p_Material_Type",MainActivity.sharedPreferences.getString("Material_Type",""));
                    cs.setDouble("p_worksOrderNo",Double.parseDouble(MainActivity.sharedPreferences.getString("Works_Order_Input","0.0")));
                    cs.setInt("p_Component_Unique",MainActivity.sharedPreferences.getInt("Component_Unique",0));
                    cs.setInt("p_Batch_Unique", MainActivity.sharedPreferences.getInt("Batch_Unique",0));
                    /*Integer.parseInt(ItemArrayList_11312.get(0).Batch_Unique));*/
                    cs.setString("p_Location",MainActivity.sharedPreferences.getString("Location",""));
                    cs.setDouble("p_Issue_Quantity",Double.parseDouble(MainActivity.sharedPreferences.getString("Issue_Quantity","0." )));
                    cs.setString("p_owner",Employee_Ref);
                    cs.setString("p_source",Employee_Ref);

                    cs.execute();

                    msg = "Kiadott menniység: " +  Double.parseDouble(MainActivity.sharedPreferences.getString("Issue_Quantity","0." ));
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


            progress.setVisibility(View.INVISIBLE);
            try
            {
                 if (!Objects.equals(msg, ""))
                    {
                        Toast.makeText(QuantityPickFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                 if (!success)
                    {
                       Toast.makeText(QuantityPickFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
                    }

                 Navigation.findNavController(inflated).navigate(R.id.action_quantity_pick_fragment_to_nav_issuing);

            } catch (Exception ex)

            {

                Toast.makeText(QuantityPickFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();

            }




/*
            if (!success)
            {
                Toast.makeText(QuantityPickFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
                this.finish();*/
                /*TODO: */
                /*VISSZA AZ ELŐZŐ FRAGMENTRE?
            }
            else {
                try {
                    Navigation.findNavController(mainActivity,R.id.recycler_view_11312).navigate(R.id.action_quantity_pick_fragment_to_nav_issuing);
                    Toast.makeText(QuantityPickFragment.this.getContext(), "Kiadás megtörtént!", Toast.LENGTH_SHORT).show();


                } catch (Exception ex)
                {
                    Toast.makeText(QuantityPickFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
 */
        }
    }

    private  class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<Class_11312_item_list> localDataSet;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private static final String TAG = "Custom adapter";
            private final TextView Batch_Number, Location,Supplier,Date_Recd,Quantity_in_Location,Allocated_Quantity;
            private final EditText Issue_Quantity;
            private final Button Kiadas;

            public ViewHolder(View view) {
                super(view);

                Batch_Number = view.findViewById(R.id.tv_batch_number_11312);
                Location = view.findViewById(R.id.tv_location_11312);
                Supplier = view.findViewById(R.id.tv_supplier_11312);
                Date_Recd = view.findViewById(R.id.tv_date_recd_11312);
                Quantity_in_Location = view.findViewById(R.id.tv_quantity_in_location_11312);
                Allocated_Quantity = view.findViewById(R.id.tv_allocated_quantity_11312);
                Issue_Quantity = view.findViewById(R.id.tv_edit_text_test);
                Kiadas = view.findViewById(R.id.btn_kiadas_11312);

                view.setOnClickListener(view1 -> Log.d(TAG, "Element" + getAdapterPosition() + "clicked"));
            }

            public TextView getBatch_Number() {
                return Batch_Number;
            }
            public TextView getLocation() {
                return Location;
            }
            public TextView getQuantity_in_Location() {
                return Quantity_in_Location;
            }
            public TextView getAllocated_Quantity() {
                return Allocated_Quantity;
            }
            public EditText getIssue_Quantity() {
                return Issue_Quantity;
            }
        }

        public CustomAdapter(ArrayList<Class_11312_item_list> dataSet) {
            if (dataSet != null) {
                localDataSet = dataSet;
            } else
                dataSet = new ArrayList<>();

        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycle_view_11312, viewGroup, false);
 
            return new CustomAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, final int position) {
            viewHolder.Batch_Number.setText(localDataSet.get(viewHolder.getAdapterPosition()).getBatch_Number());
            viewHolder.Location.setText(localDataSet.get(viewHolder.getAdapterPosition()).getLocation());
            viewHolder.Supplier.setText(localDataSet.get(viewHolder.getAdapterPosition()).getSupplier());
            viewHolder.Date_Recd.setText(localDataSet.get(viewHolder.getAdapterPosition()).getDate_Recd());
            viewHolder.Quantity_in_Location.setText(localDataSet.get(viewHolder.getAdapterPosition()).getQuantity_in_Location());
            viewHolder.Allocated_Quantity.setText(localDataSet.get(viewHolder.getAdapterPosition()).getAllocated_Qty());
            viewHolder.Issue_Quantity.setText(localDataSet.get(viewHolder.getAdapterPosition()).getIssue_Quantity().toString());
            viewHolder.Issue_Quantity.setHint(localDataSet.get(viewHolder.getAdapterPosition()).getStock_Measure());

            viewHolder.Kiadas.setOnClickListener(v -> {
                if (Double.parseDouble(localDataSet.get(viewHolder.getAdapterPosition()).getIssue_Quantity().toString())!=0);
                {
                    viewHolder.Issue_Quantity.getText().toString();

                    MainActivity.sharedPreferences.edit().remove("Material_Type").apply();
                    MainActivity.sharedPreferences.edit().putString("Material_Type",localDataSet.get(viewHolder.getAdapterPosition()).getMaterial_Type()).apply();

                    MainActivity.sharedPreferences.edit().remove("Batch_Unique").apply();
                    MainActivity.sharedPreferences.edit().putInt("Batch_Unique",Integer.parseInt(localDataSet.get(viewHolder.getAdapterPosition()).getBatch_Unique())).apply();

                    MainActivity.sharedPreferences.edit().remove("Location").apply();
                    MainActivity.sharedPreferences.edit().putString("Location",localDataSet.get(viewHolder.getAdapterPosition()).getLocation()).apply();

                    MainActivity.sharedPreferences.edit().remove("Issue_Quantity").apply();
                    MainActivity.sharedPreferences.edit().putString("Issue_Quantity", viewHolder.getIssue_Quantity().getText().toString()).apply();

                    SyncData2 orderData2 = new SyncData2(mainActivity);
                    orderData2.execute();
                    progress.setVisibility(View.VISIBLE);

                }



            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }
}
