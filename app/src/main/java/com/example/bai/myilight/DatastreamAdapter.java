package com.example.bai.myilight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DatastreamAdapter extends ArrayAdapter {

    private int resource;
    public DatastreamAdapter(Context context, int resource, List<Datastream> objects) {
        super(context, resource, objects);
        this.resource = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resource, parent,false);
        Datastream datastream = (Datastream) getItem(position);
        TextView datastream_name = view.findViewById(R.id.datastream_name);
        datastream_name.setText(datastream.getName());
        return view;
    }
}
