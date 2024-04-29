package com.bigbluebuttontabletsdk.utils;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class EventEmitterData {
  public static final String onBroadcastStarted = "onBroadcastStarted";
  public static final String onBroadcastPaused = "onBroadcastPaused";
  public static final String onBroadcastResumed = "onBroadcastResumed";
  public static final String onBroadcastFinished = "onBroadcastFinished";
  public static final String onBroadcastRequested = "onBroadcastRequested";
  public static final String onScreenShareOfferCreated = "onScreenShareOfferCreated";
  public static final String onSetScreenShareRemoteSDPCompleted = "onSetScreenShareRemoteSDPCompleted";
  public static final String onScreenShareLocalIceCandidate = "onScreenShareLocalIceCandidate";
  public static final String onScreenShareSignalingStateChange = "onScreenShareSignalingStateChange";
  public static final String onAddScreenShareRemoteIceCandidateCompleted = "onAddScreenShareRemoteIceCandidateCompleted";
  public static final String onFullAudioOfferCreated = "onFullAudioOfferCreated";
  public static final String onSetFullAudioRemoteSDPCompleted = "onSetFullAudioRemoteSDPCompleted";


  public static  void emitEvent(ReactApplicationContext reactContext,String eventName, String eventData) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, eventData);
  }

}
