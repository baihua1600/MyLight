package com.example.bai.myilight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class Create_datapointsActivity extends AppCompatActivity {

    EditText datapoints_x;
    EditText datapoints_y;
    EditText datapoints_z;
    Button create_point;
    String x_name;
    int y_state;
    double z_v;

    MyApplication myApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_datapoints);

        datapoints_x = findViewById(R.id.datapoints_x);
        datapoints_y = findViewById(R.id.datapoints_y);
        datapoints_z = findViewById(R.id.datapoints_z);


        create_point = findViewById(R.id.create_point);
        myApp = (MyApplication) getApplication();
        create_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        x_name = datapoints_x.getText().toString();
                        y_state = Integer.parseInt(datapoints_y.getText().toString());
                        z_v = Double.parseDouble(datapoints_z.getText().toString());
                        JSONObject object = new JSONObject();
                        JSONObject post = new JSONObject();
                        object.put("x", x_name);
                        object.put("y",y_state);
                        object.put("z",z_v);
                        post.put("datapoint", object);

                        String json = post.toJSONString();
                        Log.i("json",json);

                        String url = "https://iot.espressif.cn/v1/datastreams/"+myApp.getStream_name()+"/datapoint/?deliver_to_device=true"; //test1写死，改为动态

                        try {
                            Response response = HTTPUtil.post_with_header(url,myApp.getToken_dev(),json);
                            Log.i("response",response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Intent intent = new Intent(Create_datapointsActivity.this, DatapointsActivity.class);
                startActivity(intent);
            }
        });

    }
}
