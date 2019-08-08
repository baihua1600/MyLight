package com.example.bai.myilight;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Response;

public class CreateDeviceActivity extends AppCompatActivity {

    //声明
    private Spinner spinnerProduct;
    private EditText name;
    private EditText description;
    private CheckBox is_private;
    private Button create;

    private ArrayAdapter adapter;
    private List<String> productsList = new ArrayList<>();
    private MyApplication myApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_device);

        myApp = (MyApplication) getApplication();
        //Intent intent = getIntent();
        //int ptype = intent.getIntExtra("ptype", 0);
        //final String token = intent.getStringExtra("token");

        /*1. 创建产品，发送消息
        * 2. 获得刚创建的产品id
        * 3. 用刚创建的产品创建新的设备
        * 4. 创建成功，返回设备列表*/
        spinnerProduct = findViewById(R.id.spinnerProducts);

        //
        productsList.add("灯");
        productsList.add("开关");
        productsList.add("路由器");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(adapter);

        /*spinnerProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        is_private = findViewById(R.id.is_private);
        create = findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //生成post需要的信息

                        //随机生成serial
                        String serial = randomSerial();

                        String name_s = name.getText().toString();
                        String description_s = description.getText().toString();
                        Boolean is_private_b = is_private.isChecked();
                        int is_private_i;
                        if(is_private_b){
                            is_private_i = 1;
                        }
                        else{
                            is_private_i = 0;
                        }

                        JSONObject object = new JSONObject();
                        object.put("serial",serial);
                        object.put("name",name_s);
                        object.put("description",description_s);
                        object.put("is_private",is_private_i);
                        object.put("status",1);
                        Log.i("message",object.toJSONString());

                        //获得刚创建的产品的id，拼接url，
                        String url = "https://iot.espressif.cn/v1/products/:product_id/devices/";
                        try {
                            Response response = HTTPUtil.post_with_header(url, myApp.getToken(), object.toJSONString());
                            Log.i("message","success!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            Log.i("message","success!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n"+response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });






    }

    private String randomSerial() {
        String[] alt= {"A","B","C","D","X","Y","Z"};
        String[] lower = {"a","b","c","d","e","f","g","h","i","j","x","y","z"};
        String rand = "";
        Random random = new Random();
        int x;
        x = random.nextInt(alt.length);
        rand = rand + alt[x];
        for(int i = 1; i<=2; i++){
            x = random.nextInt(lower.length);
            rand = rand + lower[x];
        }
        rand = rand + "-";
        for(int i = 4; i <= 8; i++){
            x = random.nextInt(lower.length);
            rand = rand + lower[x];
        }
        return rand;
    }

    //创建产品

}
