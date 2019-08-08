package com.example.bai.myilight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter {

    private int resource_id;

    public DeviceAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resource_id = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Device device = (Device) getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resource_id, parent, false);

        ImageView device_img = view.findViewById(R.id.device_img);
        TextView name = view.findViewById(R.id.device_name);
        TextView description = view.findViewById(R.id.device_description);
        name.setText(device.getName());
        description.setText(device.getDescription());
        device_img.setImageResource(device.getImg_id());


        return view;
    }
}
