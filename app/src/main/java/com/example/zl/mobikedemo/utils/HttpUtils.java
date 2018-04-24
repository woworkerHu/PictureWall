package com.example.zl.mobikedemo.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by zl on 18/4/20.
 */

public class HttpUtils {
    static OkHttpClient okHttpClient;
    static ExecutorService threadPool;

    static {
        okHttpClient = new OkHttpClient();
        threadPool = Executors.newCachedThreadPool();
    }

    public static String doGet(String url) {
        Request build = new Request.Builder().url(url).build();
        try {
            Response execute = okHttpClient.newCall(build).execute();
            return execute.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void doGetStream(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public static void execute(Runnable runnable){
        threadPool.execute(runnable);
    }

}
