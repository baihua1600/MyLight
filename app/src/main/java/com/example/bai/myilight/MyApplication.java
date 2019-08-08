package com.example.bai.myilight;

import android.app.Application;

public class MyApplication extends Application {
    private String token;

    private String token_dev;

    private String stream_name;

    private int device_id;

    private int product_id;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MyApplication(){

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_dev() {
        return token_dev;
    }

    public void setToken_dev(String token_dev) {
        this.token_dev = token_dev;
    }

    public String getStream_name() {
        return stream_name;
    }

    public void setStream_name(String stream_name) {
        this.stream_name = stream_name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }
}
