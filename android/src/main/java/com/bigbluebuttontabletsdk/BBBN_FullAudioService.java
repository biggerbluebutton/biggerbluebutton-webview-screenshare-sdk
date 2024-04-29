package com.bigbluebuttontabletsdk;

import androidx.annotation.NonNull;

import com.bigbluebuttontabletsdk.broadcastScreen.FullAudioService;
import com.bigbluebuttontabletsdk.utils.EventEmitterData;
import com.bigbluebuttontabletsdk.utils.Utils;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class BBBN_FullAudioService extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "BBBN_FullAudioService";
  private ReactApplicationContext reactContext;
//  private FullAudioService fullAudioService;

  public BBBN_FullAudioService(ReactApplicationContext reactContext) {
        super(reactContext);
    this.reactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

 @ReactMethod
    public void createFullAudioOffer(String stunTurnJson) {

       Utils.showLogs(stunTurnJson+"createFullAudioOffer");
//       fullAudioService.createOffer(reactContext);

    }
  @ReactMethod
  public void setFullAudioRemoteSDP(String remoteSDP) {
    Utils.showLogs(remoteSDP+"setFullAudioRemoteSDP");
   // fullAudioService.setRemoteSDP(remoteSDP);
   // EventEmitterData.emitEvent(reactContext,EventEmitterData.onSetFullAudioRemoteSDPCompleted, null);
  }

}
