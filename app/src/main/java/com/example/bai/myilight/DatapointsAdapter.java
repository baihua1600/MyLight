package com.example.bai.myilight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DatapointsAdapter extends ArrayAdapter {
    private int resource;

    public DatapointsAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resource,parent,false);

        Datapoints datapoints = (Datapoints) getItem(position);
        TextView datapoints_item = view.findViewById(R.id.datapoints_item_);
        datapoints_item.setText(""+datapoints.getX_name());
        return view;
    }
}
