package com.bigbluebuttontabletsdk.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class Utils {
  public static void showLogs(String msg){
    Log.d("DEBUG_KEY", "AndroidModule: "+msg);
  }

  public static String extractSdpFromPayload(String payload) {
    try {
      JSONObject jsonObject = new JSONObject(payload);
      return jsonObject.optString("sdp");
    } catch (JSONException e) {
      e.printStackTrace();
      showLogs("extractSdpFromPayload "+e.getMessage());
      return null;
    }
  }
  public static String extractDataFromPayload(String payload,String key) {
    try {
      JSONObject jsonObject = new JSONObject(payload);
      return jsonObject.optString(key);
    } catch (JSONException e) {
      e.printStackTrace();
      showLogs("extractDataFromPayload "+e.getMessage());
      return null;
    }
  }

  public static String toJson(Object myObject) {
    Gson gson = new Gson();
    JsonObject jsonObject = gson.toJsonTree(myObject).getAsJsonObject();
    return jsonObject.toString();
  }

  public static Object fromJson(String jsonString, Type type) {
    if (jsonString == null || jsonString.isEmpty()) return null;
    Object obj = null;
    try {
      obj = new Gson().fromJson(jsonString, type);
    }catch (Exception e){
      showLogs(e.getMessage());
    }
    return obj;
  }
}
