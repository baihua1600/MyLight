package com.example.bai.myilight;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class HTTPUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final OkHttpClient client = new OkHttpClient();

    //post请求
    public static Response post_with_json(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
        Response response = client.newCall(request).execute();
        return response;
    }
    //get请求
    public static Response get_with_header(String url, String header) throws IOException {
        Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .addHeader("Authorization",header)
                            .build();
        Response response = client.newCall(request).execute();
        return response;
    }
    public static Response post_with_header(String url, String header, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",header)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

}
