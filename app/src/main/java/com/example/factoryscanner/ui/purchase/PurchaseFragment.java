package com.example.factoryscanner.ui.purchase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
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
import com.example.factoryscanner.databinding.FragmentPurchaseBinding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PurchaseFragment extends Fragment{

    private FragmentPurchaseBinding binding;
    private boolean success = false;
    GlobalClass globalVariable;
    private ListView ListView_111;
    private ArrayList<Class_111_item_list> ItemArrayList_111;
    private MyAppAdapter myAppAdapter;
    EditText filterTarolohely;
    ImageView imagesSearch;
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PurchaseViewModel purchaseViewModel =
                new ViewModelProvider(this).get(PurchaseViewModel.class);

        binding = FragmentPurchaseBinding.inflate(inflater, container, false);
        View inflated = inflater.inflate(R.layout.activity_111_bevetstart, container, false);
        try {
            // textView = (TextView) findViewById(R.id.tv_dolgozoneve);
            globalVariable = GlobalClass.getInstance();
            // textView.setText(globalVariable.getEmployee_name());
            ListView_111 = inflated.findViewById(R.id.list_111);
            ItemArrayList_111 = new ArrayList<Class_111_item_list>();
            mainActivity = ((MainActivity) getActivity());
            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();

            imagesSearch = inflated.findViewById(R.id.imageView_search_2);

            filterTarolohely = inflated.findViewById(R.id.editText_tarolohely_2);

            filterTarolohely.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    ItemArrayList_111 = new ArrayList<Class_111_item_list>();
                    imagesSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ItemArrayList_111.clear();
                            SyncData orderData = new SyncData(mainActivity);
                            orderData.execute();
                        }
                    });
                    filterTarolohely.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (!filterTarolohely.getText().toString().trim().equals("")){
                                if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                    ItemArrayList_111.clear();
                                    SyncData orderData = new SyncData(mainActivity);
                                    orderData.execute();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ItemArrayList_111.clear();
                    SyncData orderData = new SyncData(mainActivity);
                    orderData.execute();
                }
            });

        }
        catch (Exception ex)
        {
            Toast.makeText(PurchaseFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        final TextView textView = binding.textPurchase;
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
                    String query = globalVariable.getSql_command("111");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    String s = filterTarolohely.getEditableText().toString();
                    stmt.setString(1,s);
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                ItemArrayList_111.add(new Class_111_item_list(rs.getString("Purchase_Order_No"), rs.getString("Supplier_Code"), rs.getString("Supplier_Name"), rs.getString("Date"),rs.getString("Lines")));
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
        public void onPostExecute(String msg)
        {
            if (!success) {
                Toast.makeText(PurchaseFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(ItemArrayList_111, PurchaseFragment.this.getContext());
                    ListView_111.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    ListView_111.setAdapter(myAppAdapter);
                } catch (Exception ex) {
                    Toast.makeText(PurchaseFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class MyAppAdapter extends BaseAdapter
    {
        public class ViewHolder {
            TextView Purchase_Order_No;
            TextView Supplier_Name;
            TextView Date;
            TextView Lines;

        }

        public List<Class_111_item_list> parkingList;

        public Context context;
        List<Class_111_item_list> arraylist;

        private MyAppAdapter(List<Class_111_item_list> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = parkingList;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
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
                rowView = inflater.inflate(R.layout.activity_111_bevetstart_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.Purchase_Order_No = (TextView) rowView.findViewById(R.id.btn_purchase_order);
                viewHolder.Supplier_Name = (TextView) rowView.findViewById(R.id.tv_supplier_name);
                viewHolder.Date = (TextView) rowView.findViewById(R.id.tv_date_11111);
                viewHolder.Lines = (TextView) rowView.findViewById(R.id.tv_lines);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (MyAppAdapter.ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.Purchase_Order_No.setText(parkingList.get(position).getPurchase_Order_No());
            viewHolder.Supplier_Name.setText(parkingList.get(position).getSupplier_Name());
            viewHolder.Date.setText(parkingList.get(position).getDate());
            viewHolder.Lines.setText(parkingList.get(position).getLines());

            viewHolder.Purchase_Order_No.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.sharedPreferences.edit().putString("Supplier_Name",(parkingList.get(position).getSupplier_Name())).apply();
                    MainActivity.sharedPreferences.edit().putInt("Purchase_Order_No",Integer.parseInt(parkingList.get(position).getPurchase_Order_No())).apply();
                    Navigation.findNavController(v).navigate(R.id.action_nav_purchase_to_purchaseItemFragment);
                }
            });

            return rowView;
        }

    }
}