package com.example.mybudgetapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BudgetGridViewAdapter extends ArrayAdapter<Budget> {
    public BudgetGridViewAdapter(@NonNull Context context, ArrayList<Budget> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_category_grid_view_adapter, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Budget dataModal = getItem(position);

        // initializing our UI components of list view item.
        TextView name = listitemView.findViewById(R.id.txtname);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        name.setText(dataModal.getBudget_name());

        // below line is use to add item
        // click listener for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, BudgetDetail.class);
                intent.putExtra("budget_id", dataModal.getBudget_id());
                context.startActivity(intent);
            }
        });
        return listitemView;
    }
}