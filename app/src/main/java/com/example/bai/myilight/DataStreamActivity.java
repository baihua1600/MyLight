package com.example.bai.myilight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class DataStreamActivity extends AppCompatActivity {

    private static final int UPDATE_DATASTREAM = 7;



    MyApplication myApp;

    private TextView device_name;
    private TextView device_type;
    private ListView datapoint;
    private Button create_devi;
    Handler handler;
    DatastreamAdapter datastreamAdapter;
    DatapointsAdapter datapointsAdapter;

    //变量
    private String name;
    private int id;
    private int product_id;
    private List<Datastream> datastreamList = new ArrayList<>();
    private List<Datapoints> datapointsList = new ArrayList<>();
    public String stream_name;      //数据模型名称，用于查数据点接口参数，点击数据模型时获取

    //public int flag = SHOW_DATASTREAM;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_info);

        myApp = (MyApplication) getApplication();
        Intent intent = getIntent();
        product_id = intent.getIntExtra("product_id",0);
        id = intent.getIntExtra("id",0);
        name= intent.getStringExtra("name");


        Log.i("!!!!!",name);
        //初始化控件
        device_name = findViewById(R.id.device_name);

        datapoint = findViewById(R.id.datapoints);

        create_devi = findViewById(R.id.create_devi);

        create_devi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataStreamActivity.this, CreateDatastreamActivity.class);
                startActivity(intent);
            }
        });

        get_data_stream();
        datastreamAdapter = new DatastreamAdapter(DataStreamActivity.this,R.layout.datastream_item,datastreamList);
        datapointsAdapter = new DatapointsAdapter(DataStreamActivity.this, R.layout.datapoints_item, datapointsList);           //忘记改layout，报空指针
        datapoint.setAdapter(datastreamAdapter);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == UPDATE_DATASTREAM){
                    datastreamAdapter.notifyDataSetChanged();
                    device_name.setText("设备名称："+name);
                }
            }
        };

        /*//test 更改LISTVIEW适配器
        if(flag == SHOW_DATASTREAM){
            datapoint.setAdapter(datastreamAdapter);
        }*/

        //点击数据模型,跳转到控制设备
        datapoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Datastream datastream = datastreamList.get(position);

                //Intent intent = new Intent(DataStreamActivity.this, DatapointsActivity.class);
                Intent intent = new Intent(DataStreamActivity.this, UpdateDatapointActivity.class);
                stream_name = datastream.getName();
                myApp.setStream_name(stream_name);
                intent.putExtra("stream_name",stream_name);
                startActivity(intent);
                //get_datapoints();
                //datapoint.setAdapter(datapointsAdapter);
                Log.i("test","it is work");
            }
        });




    }

    private void get_data_stream(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String url = "https://iot.espressif.cn/v1/products/"+product_id+"/datastreamTmpls/";
                try {
                    Response response = HTTPUtil.get_with_header(url,myApp.getToken());
                    String json_String = response.body().string();
                    Log.i("!!!",json_String);
                    JSONObject object = (JSONObject) JSONObject.parse(json_String);
                    JSONArray datastream_array = object.getJSONArray("datastreamTmpls");
                    for(int i = 0; i < datastream_array.size(); i++){
                        JSONObject item = datastream_array.getJSONObject(i);
                        String name = item.getString("name");
                        Datastream datastream = new Datastream(name);
                        datastreamList.add(datastream);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                msg.what = UPDATE_DATASTREAM;
                handler.sendMessage(msg);
            }
        }).start();
    }

/*
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
                        double x = item.getDoubleValue("x");
                        Log.i("x", ""+x);
                        Datapoints datapoints = new Datapoints(x);
                        datapointsList.add(datapoints);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                msg.what = UPDATE_DATAPOINTS;
                handler.sendMessage(msg);
            }
        }).start();
    }*/
}
