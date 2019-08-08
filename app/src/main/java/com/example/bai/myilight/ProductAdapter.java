package com.example.bai.myilight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends ArrayAdapter {

    private int resource_id;

    public ProductAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resource_id = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = (Product) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resource_id,parent,false);

        ImageView product_item_img = view.findViewById(R.id.product_item_img);
        TextView product_item_name = view.findViewById(R.id.product_item_name);
        product_item_img.setImageResource(product.getImg_id());
        product_item_name.setText(product.getName());


        return view;
    }
}
