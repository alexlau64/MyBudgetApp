package com.example.mybudgetapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryGridViewAdapter extends BaseAdapter {
    private ArrayList<Category> dataList;
    private Context context;
    LayoutInflater layoutInflater;
    public CategoryGridViewAdapter(Context context, ArrayList<Category> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }
    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = layoutInflater.inflate(R.layout.activity_category_grid_view_adapter, null);
        }
        TextView gridCaption = view.findViewById(R.id.txtname);
        gridCaption.setText(dataList.get(i).getCategoryName());
        return view;
    }
}