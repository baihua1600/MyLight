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
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Response;

public class ProductsActivity extends AppCompatActivity {



    private static final int UPDATALISTVIEW = 5;
    //声明
    private ListView products;
    private Button create;
    private List<Product> product_list = new LinkedList<>();
    //String token;
    MyApplication myApp;
    ProductAdapter productAdapter;
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);
        myApp = (MyApplication) getApplication();
        products = findViewById(R.id.products);
        create = findViewById(R.id.create_product);
        //intent
        //Intent intent = getIntent();
        //handle
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == UPDATALISTVIEW){
                    productAdapter.notifyDataSetChanged();

                }
            }
        };
        //token = intent.getStringExtra("token");
        //初始化产品列表
        get_products();
        Log.i("token",myApp.getToken());
        productAdapter = new ProductAdapter(ProductsActivity.this,R.layout.products_item,product_list);
        products.setAdapter(productAdapter);
        //productAdapter.notifyDataSetChanged();
        products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = product_list.get(position);
                Intent intent = new Intent(ProductsActivity.this, DevicesActivity.class);
                intent.putExtra("product_id", product.getProduct_id());
                myApp.setProduct_id(product.getProduct_id());
                startActivity(intent);
            }
        });
    }
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
                        product_list.add(product);
                    }
                    Log.i("message",json_String);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                msg.what = UPDATALISTVIEW;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
