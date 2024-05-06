package com.example.factoryscanner.ui.inventory;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.factoryscanner.R;
import com.example.factoryscanner.ui.picklist.Class_1121_item_list;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Class_1121_item_list> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "Custom adapter";
        private final TextView Quantity_In_Location, Date_Received;
        private final EditText Quantity_To_Pick;
        private final Button Submit;

        public ViewHolder(View view) {
            super(view);

            Quantity_In_Location = (TextView) view.findViewById(R.id.tv_quantity_in_location_1121);
            Date_Received = (TextView) view.findViewById(R.id.tv_date_1121);
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
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_view1121, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getQuantity_In_Location().setText(String.format("Készlet: %s Kiszedett: ", localDataSet.get(position).getQuantity_In_Location()));
        viewHolder.getDate_Received().setText(String.format("Érkezett: %s - %s", localDataSet.get(position).getDate_Received(), localDataSet.get(position).getSupplier_Name()));
        viewHolder.getQuantity_To_Pick().setText(localDataSet.get(position).getQuantity_To_Pick());
        viewHolder.getSubmit().setText(String.format("%s - %s", localDataSet.get(position).getLocation(), localDataSet.get(position).getBatch_Number()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
