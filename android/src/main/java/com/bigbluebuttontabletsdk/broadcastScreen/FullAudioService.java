package com.bigbluebuttontabletsdk.broadcastScreen;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.bigbluebuttontabletsdk.utils.BBBSharedData;
import com.bigbluebuttontabletsdk.utils.PreferencesUtils;
import com.bigbluebuttontabletsdk.utils.Utils;
import com.facebook.react.bridge.ReactApplicationContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FullAudioService implements AudioWebRTCClientInterface {
  private static final String TAG = "FullAudioService";
  private AudioWebRTCClient audioWebRTCClient;
  private ReactApplicationContext mContext;
  public void createOffer(ReactApplicationContext context) {

    this.mContext = context;
    List<PeerConnection.IceServer> iceServers = new ArrayList<>();
    iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun1.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun2.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun3.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun4.l.google.com:19302"));
    audioWebRTCClient = new AudioWebRTCClient(context,iceServers,this);
    audioWebRTCClient.offer(context);

//    int createOfferIterations = 0;
//  while(true){
//    createOfferIterations++;
//
//
//  }
  }
  public void setRemoteSDP(String remoteSDP) {
    audioWebRTCClient.setRemoteSDP(remoteSDP);
  }

  public void addRemoteCandidate(IceCandidate remoteCandidate) {
    audioWebRTCClient.setRemoteCandidate(remoteCandidate);
  }
  @Override
  public void webRTCClient(AudioWebRTCClient audioWebRTCClient, IceCandidate iceCandidate) {
    try {
      JSONObject iceCandidateJson = new JSONObject();
      iceCandidateJson.put("sdp", iceCandidate.sdp);
      iceCandidateJson.put("sdpMid", iceCandidate.sdpMid);
      iceCandidateJson.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
      // Convert the JSONObject to a string
      String iceCandidateAsString = iceCandidateJson.toString();
      Utils.showLogs(TAG+ "ICE candidate: " + iceCandidateAsString);
//      HashMap<String, String> properties = new HashMap<>();
//      properties.put("iceJson", iceCandidateAsString);
    //  String stunTurnJson2 = BBBSharedData.generatePayload(properties);
     // PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.onScreenShareLocalIceCandidate,stunTurnJson2);
    } catch (JSONException e) {
      Utils.showLogs(TAG+ "Error handling ICE candidate: " + e.getMessage());
    }
  }

  @Override
  public void webRTCClient(AudioWebRTCClient audioWebRTCClient, PeerConnection.SignalingState signalingState) {
    String stateString = "";
    switch (signalingState) {
      case HAVE_LOCAL_OFFER:
        Utils.showLogs(TAG+ "new signaling state -> haveLocalOffer");
        stateString = "have-local-offer";
        break;
      case HAVE_LOCAL_PRANSWER:
        Utils.showLogs(TAG+"new signaling state -> haveLocalPrAnswer");
        stateString = "have-local-pranswer";
        break;
      case HAVE_REMOTE_OFFER:
        Utils.showLogs(TAG+ "new signaling state -> haveRemoteOffer");
        stateString = "have-remote-offer";
        break;
      case HAVE_REMOTE_PRANSWER:
        Utils.showLogs(TAG+ "new signaling state -> haveRemotePrAnswer");
        stateString = "have-remote-pranswer";
        break;
      case STABLE:
        Utils.showLogs(TAG+"new signaling state -> stable");
        stateString = "stable";
        break;
      case CLOSED:
        Utils.showLogs(TAG+"new signaling state -> closed");
        stateString = "closed";
        break;
      default:
        Utils.showLogs(TAG+ "new signaling state -> UNKNOWN");
        break;
    }
//    HashMap<String, String> properties = new HashMap<>();
//    properties.put("newState", stateString);
//    String stunTurnJson2 = BBBSharedData.generatePayload(properties);
//    PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.onScreenShareSignalingStateChange,stunTurnJson2);

  }

  @Override
  public void webRTCClient(AudioWebRTCClient audioWebRTCClient, PeerConnection.IceGatheringState iceGatheringState) {

  }

  @Override
  public void webRTCClient(AudioWebRTCClient audioWebRTCClient, PeerConnection.IceConnectionState iceConnectionState) {

  }


}
