package com.bigbluebuttontabletsdk.broadcastScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bigbluebuttontabletsdk.utils.BBBSharedData;
import com.bigbluebuttontabletsdk.utils.EventEmitterData;
import com.bigbluebuttontabletsdk.utils.PreferencesUtils;
import com.bigbluebuttontabletsdk.utils.Utils;
import com.facebook.react.bridge.ReactApplicationContext;

import org.json.JSONException;
import org.json.JSONObject;

public class BigBlueButtonSDK {

  private static final String TAG = "BigBlueButtonSDK   ";
  private static String broadcastExtensionBundleId = "";
  private static String appGroupName = "";
  private static PreferencesUtils preferencesUtils;
  private static SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
  private static ReactApplicationContext mrReactContext;

  public static void initialize(Context context,ReactApplicationContext reactContext) {
    mrReactContext = reactContext;
    preferencesUtils = PreferencesUtils.getInstance(context);

    onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
      switch (key) {
        case BBBSharedData.SharedData.broadcastStarted:
          Utils.showLogs(TAG+ "Detected a change for key broadcastStarted");
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onBroadcastStarted,null);
          break;
        case BBBSharedData.SharedData.screenShareOfferCreated:
          String payload = sharedPreferences.getString(key, null);
          Utils.showLogs(TAG+ "Detected a change for key screenShareOfferCreated"+payload);
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onScreenShareOfferCreated, Utils.extractSdpFromPayload(payload));
          break;
        case BBBSharedData.SharedData.setScreenShareRemoteSDPCompleted:
          Utils.showLogs(TAG+ "Detected a change  for key setScreenShareRemoteSDPCompleted");
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onSetScreenShareRemoteSDPCompleted, null);
          break;
        case BBBSharedData.SharedData.onScreenShareLocalIceCandidate:
          String payload2 = sharedPreferences.getString(key, null);
          Utils.showLogs(TAG+ "Detected a change for key onScreenShareLocalIceCandidate "+payload2);
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onScreenShareLocalIceCandidate, Utils.extractDataFromPayload(payload2,"iceJson"));
          break;
        case BBBSharedData.SharedData.onScreenShareSignalingStateChange:
          String payload3 = sharedPreferences.getString(key, null);
          Utils.showLogs(TAG+ "Detected a change for key onScreenShareSignalingStateChange "+payload3);
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onScreenShareSignalingStateChange, Utils.extractDataFromPayload(payload3,"newState"));
          break;
        case BBBSharedData.SharedData.addScreenShareRemoteIceCandidateCompleted:
          Utils.showLogs(TAG+ "Detected a change for key addScreenShareRemoteIceCandidateCompleted ");
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onAddScreenShareRemoteIceCandidateCompleted, null);
          break;
        case BBBSharedData.SharedData.broadcastFinished:
          Utils.showLogs(TAG+ "Detected a change for key broadcastFinished ");
          EventEmitterData.emitEvent(mrReactContext,EventEmitterData.onBroadcastFinished, null);
          break;

        default:
          Utils.showLogs(TAG+ "Unknown key: " + key);
          break;
      }
    };

    preferencesUtils.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
  }

  public static void deinitialize() {
    if (preferencesUtils != null && onSharedPreferenceChangeListener != null) {
      preferencesUtils.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
  }

  public static void onAppTerminated(Context context) {
    Utils.showLogs(TAG+ "onAppTerminated called");
    preferencesUtils.putString(BBBSharedData.SharedData.onApplicationTerminated, "");
  }

  public static void handleDeepLink(Context context, Bundle bundle) {
    // Implement your logic for handling deep links
  }


}

