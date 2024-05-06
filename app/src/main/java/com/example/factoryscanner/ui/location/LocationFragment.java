package com.example.factoryscanner.ui.location;

import static android.graphics.Color.parseColor;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import com.example.factoryscanner.databinding.FragmentLocationBinding;
import com.example.factoryscanner.ui.purchase.PurchaseFragment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class LocationFragment extends Fragment {

    private FragmentLocationBinding binding;
    GlobalClass globalVariable;
    private ArrayList<Class_115_item_list> ItemArrayList_115;
    private MainActivity mainActivity;
    private ProgressBar progressBar;
    private boolean success = false;
    private CustomAdapter customAdapter;
    EditText filterTarolohely;
    ImageView imagesSearch;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocationViewModel locationViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

        binding = FragmentLocationBinding.inflate(inflater, container, false);
        View inflated = inflater.inflate(R.layout.activity_115_tarolovalasztas, container, false);
        recyclerView = inflated.findViewById(R.id.recycler_view_115);
        mLayoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemArrayList_115 = new ArrayList<Class_115_item_list>();
        customAdapter = new CustomAdapter(ItemArrayList_115);
        recyclerView.setAdapter(customAdapter);
        mainActivity = ((MainActivity) getActivity());


        try {
            globalVariable = GlobalClass.getInstance();
            imagesSearch = inflated.findViewById(R.id.imageView_search_115);
            progressBar = inflated.findViewById(R.id.progress_bar_115);
            progressBar.setVisibility(View.INVISIBLE);
            filterTarolohely = inflated.findViewById(R.id.editText_tarolohely_115);
            ItemArrayList_115 = new ArrayList<Class_115_item_list>();
            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();
            filterTarolohely.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ItemArrayList_115 = new ArrayList<Class_115_item_list>();
                    imagesSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SyncData orderData = new SyncData(mainActivity);
                            orderData.execute();
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    filterTarolohely.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (!filterTarolohely.getText().toString().trim().equals("")){
                                if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
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
                    SyncData orderData = new SyncData(mainActivity);
                    orderData.execute();
                }
            });

        }
        catch (Exception ex)
        {
            Toast.makeText(LocationFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        final TextView textView = binding.textLocationFragment;
        locationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
                    String query = globalVariable.getSql_command("115");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    stmt.setString(1,(filterTarolohely.getEditableText().toString()));
                    ResultSet rs = stmt.executeQuery();
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        ItemArrayList_115.clear();
                        while (rs.next()) {
                            try {
                                ItemArrayList_115.add(new Class_115_item_list(rs.getString("Location"), rs.getString("Notes"), rs.getString("Location_Type"), rs.getString("Warehouse")));
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
                Toast.makeText(LocationFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                try {
                    customAdapter = new CustomAdapter(ItemArrayList_115);
                    recyclerView.setAdapter(customAdapter);
                } catch (Exception ex) {
                    Toast.makeText(LocationFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<Class_115_item_list> localDataSet;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private static final String TAG = "Custom adapter";
            private final TextView Location, Notes,Warehouse;

            public ViewHolder(View view) {
                super(view);

                Location = (TextView) (TextView) view.findViewById(R.id.btn_location);
                Notes = (TextView) view.findViewById(R.id.tv_notes);
                Warehouse = (TextView) view.findViewById(R.id.tv_warehouse);


                // Define click listener for the ViewHolder's View
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Element" + getAdapterPosition() + "clicked");
                    }
                });
            }



            public TextView getLocation() {
                return Location;
            }

            public TextView getNotes() {
                return Notes;
            }

            public TextView getWarehouse() {
                return Warehouse;
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public CustomAdapter(ArrayList<Class_115_item_list> dataSet) {
            if (dataSet != null) {
                localDataSet = dataSet;
            } else
                dataSet = new ArrayList<>();

        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycle_view_115, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.Location.setText(localDataSet.get(viewHolder.getAdapterPosition()).getLocation());
            viewHolder.Notes.setText(localDataSet.get(viewHolder.getAdapterPosition()).getNotes());
            viewHolder.Warehouse.setText(localDataSet.get(viewHolder.getAdapterPosition()).getWarehouse());

            if (!localDataSet.get(viewHolder.getAdapterPosition()).getLocation_Type().trim().equals(""))
            {
                viewHolder.Location.setBackgroundColor(Color.RED);
            }
            else {
                viewHolder.Location.setBackgroundColor(Color.BLUE);
            }

            viewHolder.Location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.sharedPreferences.edit().putString("Location",localDataSet.get(viewHolder.getAdapterPosition()).getLocation()).apply();
                    Navigation.findNavController(v).navigate(R.id.action_nav_location_to_location_details_fragment);
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
