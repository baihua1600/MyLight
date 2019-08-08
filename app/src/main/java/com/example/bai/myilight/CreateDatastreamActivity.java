package com.example.bai.myilight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class CreateDatastreamActivity extends AppCompatActivity {

    //控件
    private EditText stream_name;
    private EditText stream_dim;
    private EditText stream_desc;
    private Button create_stream;


    //变量
    private String name_of_stream;
    private int dim_od_stream;
    private String desc_of_stream;
    private MyApplication myApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_datastream);

        myApp = (MyApplication) getApplication();
        stream_name = findViewById(R.id.stream_name);
        stream_dim = findViewById(R.id.stream_dim);
        stream_desc = findViewById(R.id.stream_desc);
        create_stream = findViewById(R.id.create_stream);

        create_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_parameter();
                createDatastream();
            }
        });

    }

    private void get_parameter(){
        name_of_stream = stream_name.getText().toString();
        dim_od_stream = Integer.parseInt(stream_dim.getText().toString());
        desc_of_stream = stream_desc.getText().toString();

    }

    private void createDatastream(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "https://iot.espressif.cn/v1/products/"+myApp.getProduct_id()+"/datastreamTmpls/";
                JSONObject object = new JSONObject();

                JSONArray datastreamTmpls = new JSONArray();

                JSONObject json_object = new JSONObject();

                object.put("name",name_of_stream);
                object.put("dimension",dim_od_stream);
                object.put("description",desc_of_stream);

                datastreamTmpls.add(object);

                json_object.put("datastreamTmpls",datastreamTmpls);
                Log.i("json",json_object.toJSONString());

                try {
                    Response response = HTTPUtil.post_with_header(url,myApp.getToken(),json_object.toJSONString());
                    String responce_s = response.body().string();
                    Log.i("response",responce_s);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
