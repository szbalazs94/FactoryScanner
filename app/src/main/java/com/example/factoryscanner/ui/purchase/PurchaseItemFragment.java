package com.example.factoryscanner.ui.purchase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.factoryscanner.databinding.FragmentPurchaseItemBinding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PurchaseItemFragment extends Fragment {

    private FragmentPurchaseItemBinding binding;

    TextView supplierNameTextView, Purchase_Order_TextView;
    GlobalClass globalVariable;
    private ListView ListView_11111;
    private View inflated;
    private ArrayList<Class_11111_item_list> ItemArrayList_11111;
    private MyAppAdapter myAppAdapter;
    private AppBarConfiguration mAppBarConfiguration;
    private boolean success = false;
    EditText AdviceNoteNo;
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PurchaseViewModel purchaseViewModel =
                new ViewModelProvider(this).get(PurchaseViewModel.class);

        binding = FragmentPurchaseItemBinding.inflate(inflater, container, false);
        inflated = inflater.inflate(R.layout.activity_1111_bmtetelek, container, false);
        mainActivity = ((MainActivity) getActivity());

        try {
            supplierNameTextView = inflated.findViewById(R.id.tv_supplier_name);
            Purchase_Order_TextView = inflated.findViewById(R.id.tv_purchase_order_number);
            AdviceNoteNo = inflated.findViewById(R.id.edittxt_advicenoteno_1111);

            globalVariable = GlobalClass.getInstance();
            supplierNameTextView.setText(MainActivity.sharedPreferences.getString("Supplier_Name",""));
            Purchase_Order_TextView.setText(String.valueOf(MainActivity.sharedPreferences.getInt("Purchase_Order_No",0)));
            ListView_11111 = inflated.findViewById(R.id.list_1111);
            ItemArrayList_11111 = new ArrayList<Class_11111_item_list>();
            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();



        }
        catch (Exception ex)
        {
            Toast.makeText(PurchaseItemFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        final TextView textView = binding.textPurchaseItem;
        purchaseViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
                    String query = globalVariable.getSql_command("1111");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setInt(1, MainActivity.sharedPreferences.getInt("Purchase_Order_No",0));
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_11111.add(new Class_11111_item_list(
                                        rs.getString("Item"),
                                        rs.getString("Date"),
                                        rs.getString("Stock_Type_Code"),
                                        rs.getString("Part_Number"),
                                        rs.getString("Part_Description"),
                                        rs.getString("Qty_Outstanding"),
                                        rs.getString("Order_Unit"),
                                        rs.getString("Batch_No"),
                                        rs.getString("GOIN"),
                                        rs.getString("Location"),
                                        rs.getString("Description")
                                        ));
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
            if (!success) {
                Toast.makeText(PurchaseItemFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_11111, PurchaseItemFragment.this.getContext());
                    ListView_11111.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_11111.setAdapter(myAppAdapter);
                    if (myAppAdapter.isEmpty()) {
                        Navigation.findNavController(inflated).navigate(R.id.action_purchaseItemFragment_to_nav_purchase);
                    }
                } catch (Exception ex) {
                    Toast.makeText(PurchaseItemFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class MyAppAdapter extends BaseAdapter         //has a class viewholder which holds
    {
        public class ViewHolder {
            TextView PO_Item;
            TextView PO_Date;
            TextView Part_Number;
            TextView Part_Description;
            TextView Qty_Outstanding;

        }

        public List<Class_11111_item_list> parkingList;

        public Context context;
        ArrayList<Class_11111_item_list> arraylist;

        private MyAppAdapter(List<Class_11111_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Class_11111_item_list>();
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
                rowView = inflater.inflate(R.layout.activity_1111_bmtetelek_list, parent, false);
                viewHolder = new MyAppAdapter.ViewHolder();
                viewHolder.Part_Number = (TextView) rowView.findViewById(R.id.tv_part_number_1111);
                viewHolder.Part_Description = (TextView) rowView.findViewById(R.id.tv_part_description_1111);
                viewHolder.PO_Date = (TextView) rowView.findViewById(R.id.tv_date_1111);
                viewHolder.PO_Item = (TextView) rowView.findViewById(R.id.btn_item_1111);
                viewHolder.Qty_Outstanding = (TextView) rowView.findViewById(R.id.tv_quantity_outstanding_1111);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (MyAppAdapter.ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.Part_Number.setText(parkingList.get(position).getPartNo());
            viewHolder.Part_Description.setText(parkingList.get(position).getPartDescription());
            viewHolder.PO_Date.setText(parkingList.get(position).getDate());
            viewHolder.PO_Item.setText(parkingList.get(position).getItem());
            viewHolder.Qty_Outstanding.setText(parkingList.get(position).getQtyOutstanding());

            viewHolder.PO_Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!AdviceNoteNo.getText().toString().trim().equals("")) {
                        MainActivity.sharedPreferences.edit().putInt("Item",Integer.parseInt(parkingList.get(position).item)).apply();
                        MainActivity.sharedPreferences.edit().putString("Date",(parkingList.get(position).date)).apply();
                        MainActivity.sharedPreferences.edit().putString("Stock_Type_Code",(parkingList.get(position).stockTypeCode)).apply();
                        MainActivity.sharedPreferences.edit().putString("Part_No",(parkingList.get(position).partNo)).apply();
                        MainActivity.sharedPreferences.edit().putString("Advice_Note_No",(AdviceNoteNo.getText().toString())).apply();
                        MainActivity.sharedPreferences.edit().putString("Part_Description",(parkingList.get(position).partDescription)).apply();
                        MainActivity.sharedPreferences.edit().putString("Qty_Outstanding",(parkingList.get(position).qtyOutstanding)).apply();
                        MainActivity.sharedPreferences.edit().putString("Order_Unit",(parkingList.get(position).orderUnit)).apply();
                        MainActivity.sharedPreferences.edit().putInt("GOIN",Integer.parseInt(parkingList.get(position).goin)).apply();
                        MainActivity.sharedPreferences.edit().putString("Location",(parkingList.get(position).location)).apply();
                        MainActivity.sharedPreferences.edit().putString("Description",(parkingList.get(position).description)).apply();
                        MainActivity.sharedPreferences.edit().putString("Batch_No",(parkingList.get(position).batchNumber)).apply();

                        Navigation.findNavController(inflated).navigate(R.id.action_purchaseItemFragment_to_purchaseItemDetailsFragment);

                    } else {
                        Toast.makeText(PurchaseItemFragment.this.getContext(), "Töltse ki a szállítólevél számát!", Toast.LENGTH_SHORT).show();
                    }


                }
            });


            return rowView;
        }

    }

}
