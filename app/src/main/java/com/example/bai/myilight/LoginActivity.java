package com.example.bai.myilight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    //登录
    /*sharedPreferences
    * 保存登录状态*/
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private SharedPreferences sharedPreferences;
    public static final int PRINT_LOG = 2;
    public static final int LOGIN_SUCCESS = 3;
    public static final int LOGIN_FAILED = 4;

    //控件
    private MyApplication myApp;
    private EditText login_email;
    private EditText login_password;
    private CheckBox remember;
    private Button login;

    //变量
    private String text;
    private Handler handler;
    public String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);

        //初始化控件
        myApp = (MyApplication) getApplication();
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        remember = findViewById(R.id.remember);
        login = findViewById(R.id.login);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PRINT_LOG:
                        Log.i("message",text);
                        break;
                    case LOGIN_SUCCESS:

                        break;
                    case LOGIN_FAILED:

                        break;
                }

            }
        };

        //点击登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //新建线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //创建message
                        Message msg = new Message();
                        //获取信息

                        String log_email_s = login_email.getText().toString();
                        String log_password_s = login_password.getText().toString();
                        int remember_i;
                        if(remember.isChecked()){
                            remember_i = 1;
                        }
                        else{
                            remember_i = 0;
                        }
                        //请求
                        String url = "https://iot.espressif.cn/v1/user/login/";
                        String json = "{\n" +
                                "  \"email\":\""+log_email_s+"\",\n" +
                                "  \"password\":\""+log_password_s+"\",\n" +
                                "  \"remember\": "+remember_i+"\n" +
                                "}";    //构造json字符串
                        text = json;    //日志
                        Log.i("login_json",json);
                        try {
                            Response response = HTTPUtil.post_with_json(url,json);
                            if(response.isSuccessful()){
                                String data = response.body().string();     //只能调用一次
                                text = text + data;     //日志
                                JSONObject object = (JSONObject) com.alibaba.fastjson.JSON.parseObject(data);
                                JSONObject key;
                                if((key = (JSONObject) object.get("key")) != null){
                                    //
                                    token = "token "+key.getString("token");
                                    myApp.setToken(token);
                                    text = text + token;        //日志
                                    msg.what = LOGIN_SUCCESS;
                                }
                                else{
                                    msg.what = LOGIN_FAILED;
                                }



                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //保存数据
                        Editor editor = sharedPreferences.edit();
                        editor.putBoolean("remember",remember.isChecked());
                        editor.putString("token",token);
                        //用户名密码之后一起加到子线程里
                        //提交
                        //editor.apply();
                        editor.commit();  //两种提交方式， apply后台进行
                        Log.e("shared",sharedPreferences.getString("token",""));
                        Intent intent = new Intent(LoginActivity.this,DevicesActivity.class);    // 转到设备列表
                        //intent.putExtra("token",myApp.getToken());
                        startActivity(intent);

                        msg.what = PRINT_LOG;
                        handler.sendMessage(msg);
                    }

                }).start();


                    }
                });

        };


}
