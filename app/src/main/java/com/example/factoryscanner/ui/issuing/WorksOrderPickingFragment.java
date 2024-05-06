package com.example.factoryscanner.ui.issuing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import com.example.factoryscanner.BackgroundTask;
import com.example.factoryscanner.GlobalClass;
import com.example.factoryscanner.MainActivity;
import com.example.factoryscanner.R;
import com.example.factoryscanner.databinding.FragmentIssuingBinding;
import com.example.factoryscanner.databinding.FragmentWorksOrderPickingBinding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class WorksOrderPickingFragment extends Fragment {

    private FragmentWorksOrderPickingBinding binding;
    GlobalClass globalVariable;
    private MainActivity mainActivity;
    private EditText Works_Order_Input;
    Spinner dynamicSpinner,dropDownMaterialForm;
    String MaterialForm;
    ArrayList<String> data;
    String[] temp_data;
    private boolean success = false;
    private ProgressBar progress;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IssuingViewModel issuingViewModel =
                new ViewModelProvider(this).get(IssuingViewModel.class);

        binding = FragmentWorksOrderPickingBinding.inflate(inflater, container, false);
        View inflated = inflater.inflate(R.layout.activity_1131_kiadmlvalasztas, container, false);
        mainActivity = ((MainActivity) getActivity());
        globalVariable = GlobalClass.getInstance();
        try{
            Works_Order_Input.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (!Works_Order_Input.getText().toString().trim().equals("")){
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            globalVariable.setGlobal_settings("Works_Order_Input",Works_Order_Input.getEditableText().toString());
/*                            Intent openKiadMunkalapra = new Intent(Activity_1131_kiadmlvalasztas.this, Activity_11311_kiadmlreszlet.class);
                            Activity_1131_kiadmlvalasztas.this.startActivity(openKiadMunkalapra);*/
                            return true;
                        }
                    }
                    return false;
                }
            });

            String[] items2 = new String[] { "", "RM", "BI", "MA" };
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mainActivity,
                    android.R.layout.simple_spinner_item, items2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropDownMaterialForm.setAdapter(adapter2);
            dropDownMaterialForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.v("item", (String) parent.getItemAtPosition(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
            SyncData orderData = new SyncData(mainActivity);
            orderData.execute();
            progress.setVisibility(View.VISIBLE);

            String[] items = new String[] { "", "RM - Alapanyag", "BI - Kereskedelmi tétel", "MA - Gyártott tétel" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity,
                    android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dynamicSpinner.setAdapter(adapter);
            dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    Log.v("item", (String) parent.getItemAtPosition(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(WorksOrderPickingFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        final Button button3 = inflated.findViewById(R.id.scan_works_order_number_1131);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                globalVariable.setGlobal_settings("Works_Order_Input",Works_Order_Input.getEditableText().toString());
                globalVariable.setGlobal_settings("Material_Type",dynamicSpinner.getSelectedItem().toString().substring(0,2));
                globalVariable.setGlobal_settings("Material_Form",dropDownMaterialForm.getSelectedItem().toString().substring(0,2));
/*                Intent openEszkoz = new Intent(Activity_1131_kiadmlvalasztas.this, Activity_11311_kiadmlreszlet.class);
                Activity_1131_kiadmlvalasztas.this.startActivity(openEszkoz);*/
            }
        });


        final TextView textView = binding.textWorksOrderPickingFragment;
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

    private class SyncData extends BackgroundTask {
        String msg = getString(R.string.connection_error);

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
                } else {
                    String query = globalVariable.getSql_command("1131");
                    PreparedStatement stmt = (PreparedStatement) conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    data = new ArrayList<String>();
                    int x =0;

                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                String id = rs.getString("Material_Form");

                                data.add(id);
                                x += x + 1;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                msg = ex.getMessage();
                            }
                        }

                        success = true;
                    } else {
                        msg = "No Data found!";
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
            progress.setVisibility(View.INVISIBLE);


            if (!success)
            {
                Toast.makeText(WorksOrderPickingFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
            }
            else try {
                temp_data = new String[data.size()];
                temp_data = data.toArray(temp_data);
                String[] items2 = temp_data;
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(WorksOrderPickingFragment.this.getContext(),android.R.layout.simple_spinner_item, items2);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropDownMaterialForm.setAdapter(adapter2);


            } catch (Exception ex) {
                Toast.makeText(WorksOrderPickingFragment.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }
}
