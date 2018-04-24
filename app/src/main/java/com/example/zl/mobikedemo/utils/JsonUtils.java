package com.example.zl.mobikedemo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zl on 18/4/20.
 */

public class JsonUtils {
    public static JSONObject str2Json(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJsonValue(JSONObject jsonObject,String key){
        return jsonObject.optString(key, "");
    }

    public static JSONArray getJsonArray(String json){
        try {
            JSONArray list = new JSONObject(json).getJSONArray("list");
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
