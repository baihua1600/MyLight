package com.example.bai.myilight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class DatapointsActivity extends AppCompatActivity {

    private static final int UPDATE_DATAPOINTS = 8;
    private Button create_datapoints;
    private ListView datapointLV;

    //
    private MyApplication myApp;
    private Handler handler;
    private List<Datapoints> datapointsList = new ArrayList<>();
    private DatapointsAdapter datapointsAdapter;

    private String stream_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datapoints_list);

        create_datapoints = findViewById(R.id.create_datapoints);
        datapointLV = findViewById(R.id.datapoint_list);

        myApp = (MyApplication) getApplication();


        Intent intent = getIntent();
        stream_name =myApp.getStream_name();

        get_datapoints();
        //
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == UPDATE_DATAPOINTS){
                    datapointsAdapter = new DatapointsAdapter(DatapointsActivity.this, R.layout.datapoints_item,datapointsList);
                    datapointLV.setAdapter(datapointsAdapter);
                }
            }
        };
        myApp = (MyApplication) getApplication();
        create_datapoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatapointsActivity.this,Create_datapointsActivity.class);
                startActivity(intent);

            }
        });
    }

    //获取数据点
    private void get_datapoints(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                //String url = "https://iot.espressif.cn/v1/products/"+product_id+"/devices/"+id+"/datastreams/"+stream_name+"/datapoints/";

                String url = "https://iot.espressif.cn/v1/datastreams/"+stream_name+"/datapoints/";
                try {
                    Log.i("token",myApp.getToken_dev());
                    Log.i("stream",stream_name);
                    //token为设备的key
                    Response response = HTTPUtil.get_with_header(url, myApp.getToken_dev());

                    String json_String = response.body().string();
                    Log.i("response",json_String);

                    JSONObject object = (JSONObject) JSONObject.parse(json_String);
                    JSONArray datapoint_array = object.getJSONArray("datapoints");
                    for(int i = 0; i < datapoint_array.size(); i++){

                        JSONObject item = (JSONObject) datapoint_array.get(i);
                        String x = item.getString("x");
                        int y = item.getInteger("y");
                        double z = item.getDouble("z");
                        Log.i("x", ""+x);
                        Datapoints datapoints = new Datapoints(x,y,z);
                        datapointsList.add(datapoints);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                msg.what = UPDATE_DATAPOINTS;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
