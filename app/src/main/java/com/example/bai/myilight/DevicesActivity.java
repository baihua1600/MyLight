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
import android.widget.Button;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class DevicesActivity extends AppCompatActivity {

    //设备列表
    /*先查询到所有产品，依次查处每个产品的所有设备，一般一个产品对应一个设备*/
    private static final int UPDATE_DEVICE_LIST_VIEW = 6;
    private static final int PRODUCT_LIST_GET = 7;
    //声明控件
    private ListView devices_list;
    private Button create_device;

    //变量
    private List<Product> productList = new ArrayList<>();
    private List<Device> devices = new ArrayList<>();
    Handler handler;

    private MyApplication myApp;

    //String token;
    private int product_id;;
    DeviceAdapter deviceAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devices);

        //Device device1 = new Device(298560,8011,"test","desc",R.drawable.ic_lightig);
        //devices.add(device1);

       /* handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == UPDATE_DEVICE_LIST_VIEW){
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        };*/


        //intent
        myApp = (MyApplication) getApplication();
        Intent intent = getIntent();
        product_id = intent.getIntExtra("product_id",0);

        devices_list = findViewById(R.id.devices_list);



        deviceAdapter = new DeviceAdapter(DevicesActivity.this,R.layout.devices_item, devices);

        get_products();
        //get_all_devices();
        //get_devices();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == UPDATE_DEVICE_LIST_VIEW){
                    devices_list.setAdapter(deviceAdapter);
                    deviceAdapter.notifyDataSetChanged();
                    Log.e("msg","更新UI");
                }else if(msg.what == PRODUCT_LIST_GET){
                    get_all_devices();
                }
            }
        };

        create_device = findViewById(R.id.create_device);
        //点击设备
        devices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = devices.get(position);
                get_token_dev(device.getId());
                Intent intent = new Intent(DevicesActivity.this,DataStreamActivity.class);
                myApp.setDevice_id(device.getId());
                intent.putExtra("product_id",device.getProduct_id());
                intent.putExtra("id",device.getId());
                intent.putExtra("name",device.getName());
                Log.i("!!!!!!!!!!!",device.getName());
                startActivity(intent);


            }
        });

        //点击创建设备,转到创建设备
        create_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicesActivity.this,CreateDeviceActivity.class);
                startActivity(intent);
            }
        });

    }

    //获取产品
    private void get_products(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String url = "https://iot.espressif.cn/v1/products/";
                try {

                    Response response = HTTPUtil.get_with_header(url,myApp.getToken());
                    String json_String = response.body().string();
                    JSONObject object = (JSONObject) JSON.parse(json_String);
                    JSONArray products_array = object.getJSONArray("products");
                    Log.i("!!!", json_String+myApp.getToken());
                    for(int i = 0; i < products_array.size(); i++){
                        JSONObject item = (JSONObject) products_array.get(i);
                        String name = item.getString("name");
                        int id = item.getInteger("id");
                        int ptype = item.getInteger("ptype");
                        int img_id = 0;
                        switch (ptype){
                            case 27388:
                                img_id = R.drawable.ic_sensor;
                                break;
                            case 45772:
                                img_id = R.drawable.ic_lightig;
                                break;
                        }
                        int product_id = item.getInteger("id");
                        Log.i("item",item.toJSONString());
                        Product product = new Product(id, name, img_id, ptype, product_id);
                        productList.add(product);
                    }
                    Log.i("message",json_String);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                msg.what = PRODUCT_LIST_GET;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void get_all_devices(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                for(int i = 0; i<productList.size(); i++){
                    //拼接url

                    String url = "https://iot.espressif.cn/v1/products/"+productList.get(i).getProduct_id()+"/devices/";
                    Log.i("url",url);
                    try {
                        Response response = HTTPUtil.get_with_header(url,myApp.getToken());
                        String json_String = response.body().string();
                        Log.i("response",json_String);
                        JSONObject object = (JSONObject) JSONObject.parse(json_String);
                        JSONArray devices_array = object.getJSONArray("devices");
                        for(int j = 0; j < devices_array.size(); j++){
                            JSONObject item = (JSONObject) devices_array.get(j);
                            int id = item.getInteger("id");
                            int product_id = item.getInteger("product_id");
                            String name = item.getString("name");
                            String description = item.getString("description");
                            int ptype = item.getInteger("ptype");
                            int img_id = 0;
                            switch (ptype){
                                case 27388:
                                    img_id = R.drawable.ic_sensor;
                                    break;
                                case 45772:
                                    img_id = R.drawable.ic_lightig;
                                    break;
                            }
                            Device device = new Device(id, product_id, name, description, img_id);
                            devices.add(device);

                        }
                        Log.i("!!!",json_String);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                msg.what = UPDATE_DEVICE_LIST_VIEW;
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void get_devices(){
        new Thread(new Runnable() {

            String url = "https://iot.espressif.cn/v1/products/"+product_id+"/devices/";

            @Override
            public void run() {
                Message msg = new Message();
                Log.i("message","success!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                try {
                    Response response = HTTPUtil.get_with_header(url,myApp.getToken());
                    String json_String = response.body().string();
                    Log.i("response",json_String);
                    JSONObject object = (JSONObject) JSONObject.parse(json_String);
                    JSONArray devices_array = object.getJSONArray("devices");
                    for(int i = 0; i < devices_array.size(); i++){
                        JSONObject item = (JSONObject) devices_array.get(i);
                        int id = item.getInteger("id");
                        int product_id = item.getInteger("product_id");
                        String name = item.getString("name");
                        String description = item.getString("description");
                        int ptype = item.getInteger("ptype");
                        int img_id = 0;
                        switch (ptype){
                            case 27388:
                                img_id = R.drawable.ic_sensor;
                                break;
                            case 45772:
                                img_id = R.drawable.ic_lightig;
                                break;
                        }
                        Device device = new Device(id, product_id, name, description, img_id);
                        devices.add(device);

                    }
                    Log.i("!!!",json_String);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Device device1 = new Device(298560,8011,"test","desc",R.drawable.ic_lightig);
                //devices.add(device1);
                msg.what = UPDATE_DEVICE_LIST_VIEW;
                handler.sendMessage(msg);
            }
        }).start();
    }

    //获取设备token
    private void get_token_dev(final int dev_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://iot.espressif.cn/v1/keys/?scope=device&device_id="+dev_id;
                try {
                    Response response = HTTPUtil.get_with_header(url, myApp.getToken());
                    //提取master——key
                    String json_String = response.body().string();
                    Log.i("response",json_String);
                    JSONObject object = JSON.parseObject(json_String);
                    JSONArray key_array = object.getJSONArray("keys");
                    for(int i = 0; i < key_array.size(); i++){
                        JSONObject item = (JSONObject) key_array.get(i);
                        int is_master_key = item.getInteger("is_master_key");
                        if(is_master_key == 1){
                            String token_dev = item.getString("token");
                            myApp.setToken_dev("token "+token_dev);
                        }

                    }
    ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
