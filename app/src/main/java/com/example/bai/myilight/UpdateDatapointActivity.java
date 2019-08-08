package com.example.bai.myilight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Response;

public class UpdateDatapointActivity extends AppCompatActivity {

    //控制设备，监听开关事件，更改数据点，实时显示电压


    private final static int UPDATE = 8;
    //控件
    private TextView dev_name;
    private Switch switch1;
    private Switch switch2;
    private TextView dev_v;


    //变量
    private String x_name;

    private int y_state;
    private double z_v;
    private MyApplication myApp;

    //灯
    private int light1;
    private int light2;

    Handler handler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_datapoints);

        myApp = (MyApplication) getApplication();
        dev_name = findViewById(R.id.dev_name);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        dev_v = findViewById(R.id.dev_v);

        get_state();


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    light1 = 1;
                }else{
                    light1 = 0;
                }
                set_state();
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    light2 = 1;
                }else{
                    light2 = 0;
                }
                set_state();
            }
        });


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == UPDATE){
                    dev_name.setText(x_name);

                    switch (myApp.getStream_name()){
                        case "device_light_1001":
                            switch (msg.arg1){
                                case 0:     //00
                                    switch1.setChecked(false);
                                    //switch2.setChecked(false);
                                    break;
                                case 1:     //01
                                    switch1.setChecked(true);
                                    //switch2.setChecked(true);
                                    break;
                            }
                            switch (msg.arg2){
                                case 0:     //00
                                    switch2.setChecked(false);
                                    //switch2.setChecked(false);
                                    break;
                                case 1:     //01
                                    switch2.setChecked(true);
                                    //switch2.setChecked(true);
                                    break;
                            }
                            break;
                        case "device_light_1000":
                            switch1.setClickable(false);
                            if(msg.arg2 == 0){
                                switch2.setChecked(false);
                            }
                            else if(msg.arg2 == 1){
                                switch2.setChecked(true);
                            }
                            break;
                    }
                    dev_v.setText(""+z_v);
                }
            }
        };


    }

    private void get_state(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = new Message();
                String url = "https://iot.espressif.cn/v1/products/"+myApp.getProduct_id()+"/devices/"+myApp.getDevice_id()+"/datastreams/"+myApp.getStream_name()+"/datapoint/";

                try {
                    Response response = HTTPUtil.get_with_header(url,myApp.getToken());
                    String s_response = response.body().string();

                    JSONObject object = JSON.parseObject(s_response);

                    JSONObject datapoint = (JSONObject) object.get("datapoint");
                    Log.i("response",s_response);
                    Log.i("json",datapoint.toJSONString());
                    x_name = datapoint.getString("x");
                    y_state = datapoint.getIntValue("y");
                    z_v = datapoint.getDoubleValue("z");

                    Log.i("xyz",x_name+" "+y_state+" "+z_v);
                    //dev_name.setText(x_name);

                            switch (y_state){
                                case 0:     //00
                                    msg.arg1 = 0;
                                    msg.arg2 = 0;
                                    //switch1.setChecked(false);
                                    //switch2.setChecked(false);
                                    break;
                                case 1:     //01
                                    //switch1.setChecked(false);
                                    //switch2.setChecked(true);
                                    msg.arg1 = 0;
                                    msg.arg2 = 1;
                                    break;
                                case 2:     //10
                                    //switch1.setChecked(true);
                                    //switch2.setChecked(false);
                                    msg.arg1 = 1;
                                    msg.arg2 = 0;
                                    break;
                                case 3:     //11
                                    //switch1.setChecked(true);
                                    //switch2.setChecked(true);
                                    msg.arg1 = 1;
                                    msg.arg2 = 1;
                                    break;
                            }

                    //dev_v.setText(""+z_v);

                    msg.what = UPDATE;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void set_state(){


        y_state = (light1<<1) | light2;
        Log.i("l1l2",light1+""+light2);
        Log.i("state",y_state+"");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://iot.espressif.cn/v1/datastreams/"+myApp.getStream_name()+"/datapoint/?deliver_to_device=true";
                // String url = "https://iot.espressif.cn/v1/products/"+myApp.getProduct_id()+"/devices/"+myApp.getDevice_id()+"/datastreams/"+myApp.getStream_name()+"/datapoint/?deliver_to_device = true";

                JSONObject object = new JSONObject();
                JSONObject json_object = new JSONObject();

                object.put("y",y_state);

                json_object.put("datapoint",object);

                String json_post = json_object.toJSONString();

                Log.i("post",json_post);
                try {
                    Response response = HTTPUtil.post_with_header(url, myApp.getToken_dev(), json_post);
                    String s_response = response.body().string();
                    Log.i("response",s_response);



                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();


       /* if(light1 == 0 && light2 == 0){
            y_state = 0;
        }else if(light1 == 0 && light2 == 1){

        }else if(light1 == 1 && light2 == 0){

        }else if(light1 == 1 && light2 == 1){

        }*/

    }
}
