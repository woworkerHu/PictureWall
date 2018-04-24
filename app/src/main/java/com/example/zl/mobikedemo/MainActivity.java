package com.example.zl.mobikedemo;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.example.zl.mobikedemo.utils.HttpUtils;
import com.example.zl.mobikedemo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class MainActivity extends AppCompatActivity {

    String url = "http://image.so.com/j?q=mobike&sn=0&pn=50";
    static ArrayList<Info> infoArrayList = new ArrayList<>();
    static WeakHashMap<String, Activity> weakHashMap = new WeakHashMap<>();

    static GridView gridView;

    static myHandler handler = new myHandler();


    private static class myHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = weakHashMap.get("context");
            imageAdapter imageAdapter = new imageAdapter(new ImageLoader(activity), infoArrayList);
            imageAdapter.setContext(weakHashMap.get("context"));
            gridView.setAdapter(imageAdapter);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        getJsonStr();

    }

    private void initUI() {
        gridView = findViewById(R.id.gridview);
        weakHashMap.put("context",this);

    }

    private void getJsonStr() {
        final String[] result = new String[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                result[0] = HttpUtils.doGet(url);
                JSONArray jsonArray = JsonUtils.getJsonArray(result[0]);
                handleJsonArray(jsonArray);

            }
        }).start();
    }

    private void handleJsonArray(JSONArray jsonArray)  {
        for(int i =0; i< jsonArray.length();i++){
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String title = JsonUtils.getJsonValue(jsonObject, "title");
            String img = JsonUtils.getJsonValue(jsonObject, "img");
            Info info = new Info();
            info.setTitle(title);
            info.setUrl(img);
            infoArrayList.add(info);
        }
        handler.sendEmptyMessage(0);
    }

}
