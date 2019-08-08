package com.example.bai.myilight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {

    //注册界面
    /*sharedPreferences
    * 判断是否曾经登录，并保存密码
    * 如果保存，跳转到Device(Product)Activity*/
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private SharedPreferences sharedPreferences;
    private MyApplication myApp;

    EditText username;
    EditText email;
    EditText password;
    TextView http_return;
    Button join;
    Button jump_login;
    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        myApp = (MyApplication) getApplication();
        Log.e("shared",""+sharedPreferences.getBoolean("remember",false));
        if(sharedPreferences.getBoolean("remember",false)){

            myApp.setToken(sharedPreferences.getString("token",""));
            Intent intent = new Intent(MainActivity.this,DevicesActivity.class);
            startActivity(intent);
        }
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        http_return = findViewById(R.id.http_return);
        join = findViewById(R.id.join);
        //test
        jump_login = findViewById(R.id.jump_login);
        jump_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        //String username_s = username.getText().toString();
        //http_return.setText(username_s);
        //String email_s = email.getText().toString();
        //String password_s = password.getText().toString();

        final Handler handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what ==1){
                    http_return.setText(text);
                    Log.i("message",text);
                }
            }
        };
        join.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String username_s = username.getText().toString();
                        String email_s = email.getText().toString();
                        String password_s = password.getText().toString();
                        /*JSONObject json = new JSONObject();
                        try {
                            json.put("username",username.getText().toString());
                            json.put("email",email.getText().toString());
                            json.put("password",password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        String url = "https://iot.espressif.cn/v1/user/join/";
                        String json = "{\"username\":\""+username_s+"\",\"email\":\""+email_s+"\",\"password\":\""+password_s+"\"}";
                        text = json;
                        //创建客户端
                        /*OkHttpClient client = new OkHttpClient();
                        //请求体
                        RequestBody body = RequestBody.create(JSON,json);

                        //请求
                        Request request = new Request.Builder()
                                .url("https://iot.espressif.cn/v1/user/join/")//"https://iot.espressif.cn/v1/user/join/"
                                .post(body)
                                .build();
                        //发送请求
                        try {
                            Response response = client.newCall(request).execute();
                            if(response.isSuccessful()){
                                text = text + response.body().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        try {
                            Response response = HTTPUtil.post_with_json(url,json);
                            if(response.isSuccessful()){
                                text = text + response.body().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }

                }).start();
                //Map post = new HashMap();

                //post.put("username",username_s);
                //post.put("email",email_s);
                //post.put("password",password_s);

                //Toast.makeText(MainActivity.this,username_s,Toast.LENGTH_SHORT).show();




            }
        });


    }

    Thread networkTask = new Thread() {
        @Override
        public void run() {
            String username_s = username.getText().toString();
            String email_s = email.getText().toString();
            String password_s = password.getText().toString();
            JSONObject json = new JSONObject();
            try {
                json.put("username",username.getText().toString());
                json.put("email",email.getText().toString());
                json.put("password",password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //创建客户端
            OkHttpClient client = new OkHttpClient();
            //http_return.setText(json.toString());

            RequestBody body;
            body = RequestBody.create(JSON,json.toString());

            Request request = new Request.Builder()
                    .url("https://www.baidu.com")//"https://iot.espressif.cn/v1/user/join/"
                    .get()
                    .build();
            try {
                if(client.newCall(request).execute().isSuccessful()){
                    Toast.makeText(MainActivity.this,"sfd",Toast.LENGTH_SHORT).show();
                } ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


}
