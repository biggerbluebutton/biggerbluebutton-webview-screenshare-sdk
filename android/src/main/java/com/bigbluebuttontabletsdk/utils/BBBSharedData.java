package com.bigbluebuttontabletsdk.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class BBBSharedData {
  private static final String TAG = "BBBSharedData";

  public static class SharedData {
    public static final String broadcastStarted = "broadcastStarted";
    public static final String broadcastPaused = "broadcastPaused";
    public static final String broadcastResumed = "broadcastResumed";
    public static final String broadcastFinished = "broadcastFinished";
    public static final String createScreenShareOffer = "createScreenShareOffer";
    public static final String screenShareOfferCreated = "screenShareOfferCreated";
    public static final String setScreenShareRemoteSDP = "setScreenShareRemoteSDP";
    public static final String setScreenShareRemoteSDPCompleted = "setScreenShareRemoteSDPCompleted";
    public static final String addScreenShareRemoteIceCandidate = "addScreenShareRemoteIceCandidate";
    public static final String addScreenShareRemoteIceCandidateCompleted = "addScreenShareRemoteIceCandidateCompleted";
    public static final String onScreenShareLocalIceCandidate = "onScreenShareLocalIceCandidate";
    public static final String onScreenShareSignalingStateChange = "onScreenShareSignalingStateChange";
    public static final String onApplicationTerminated = "onApplicationTerminated";
    public static final String onBroadcastStopped = "onBroadcastStopped";
  }



  public static String generatePayload(HashMap<String, String> properties) {
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
    String now = sdf.format(new Date());
    properties.put("uuid", UUID.randomUUID().toString());
    properties.put("timestamp", now);
    JSONObject payload = new JSONObject(properties);
    try {
      String jsonString = payload.toString();
      Utils.showLogs("generatePayload JSON= "+jsonString);
      return jsonString;
    } catch (Exception e) {
      Utils.showLogs("generatePayload JSON encoder error, returning empty object "+e.getMessage());
      return "{}";
    }
  }
}
