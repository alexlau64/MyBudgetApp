package com.example.mybudgetapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CategoryGridViewAdapter extends ArrayAdapter<Category> {
    public CategoryGridViewAdapter(@NonNull Context context, ArrayList<Category> dataModalArrayList) {
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
        Category dataModal = getItem(position);

        // initializing our UI components of list view item.
        TextView name = listitemView.findViewById(R.id.txtname);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        name.setText(dataModal.getCategory_name());

        // below line is use to add item
        // click listener for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                //Toast.makeText(getContext(), "Item clicked is : " + dataModal.getCategory_name(), Toast.LENGTH_SHORT).show();

                // Get the context from the view
                Context context = v.getContext();

                // Start a new intent to open the CategoryDetailActivity
                Intent intent = new Intent(context, CategoryDetail.class);

                // Pass the category id to the CategoryDetailActivity
                intent.putExtra("category_id", dataModal.getCategory_id());

                // Start the new activity
                context.startActivity(intent);
            }
        });
        return listitemView;
    }
}