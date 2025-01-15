package com.bigbluebuttontabletsdk.broadcastScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.bigbluebuttontabletsdk.utils.BBBSharedData;
import com.bigbluebuttontabletsdk.utils.PreferencesUtils;
import com.bigbluebuttontabletsdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFrame;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScreenBroadcasterService implements ScreenShareWebRTCClientInterface {

  private static final String TAG = "ScreenShareService   ";
  private ScreenShareWebRTCClient webRTCClient;

  private boolean isConnected = false;
  private Display display;

private Context mContext;

  public void initializePeerConnection(Context context, Intent intent) {

    WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    display = wm.getDefaultDisplay();
    this.mContext = context;
    List<PeerConnection.IceServer> iceServers = new ArrayList<>();
    iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun1.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun2.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun3.l.google.com:19302"));
    iceServers.add(new PeerConnection.IceServer("stun:stun4.l.google.com:19302"));
    webRTCClient = new ScreenShareWebRTCClient(context,iceServers,createMediaProjection(intent),this);


  }

  private VideoCapturer createMediaProjection(Intent intent) {

    VideoCapturer videoCapturer = new ScreenCapturerAndroid(intent,
      new MediaProjection.Callback() {
        @Override
        public void onStop() {
          super.onStop();
          Utils.showLogs(TAG+ "User has revoked media projection permissions");
        }
      });
    Utils.showLogs(TAG+ "createMediaProjection videoCapture "+videoCapturer);
    return videoCapturer;
  }





  public void createOffer() {
    webRTCClient.offer();
  }

  public void setRemoteSDP(String remoteSDP) {
    webRTCClient.setRemoteSDP(remoteSDP);
  }

  public void addRemoteCandidate(IceCandidate remoteCandidate) {
    webRTCClient.setRemoteCandidate(remoteCandidate);
  }

  @Override
  public void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, IceCandidate rtcIceCandidate) {
    try {
      JSONObject iceCandidateJson = new JSONObject();
      iceCandidateJson.put("sdp", rtcIceCandidate.sdp);
      iceCandidateJson.put("sdpMid", rtcIceCandidate.sdpMid);
      iceCandidateJson.put("sdpMLineIndex", rtcIceCandidate.sdpMLineIndex);
      // Convert the JSONObject to a string
      String iceCandidateAsString = iceCandidateJson.toString();
      HashMap<String, String> properties = new HashMap<>();
      properties.put("iceJson", iceCandidateAsString);
      String stunTurnJson2 = BBBSharedData.generatePayload(properties);
      PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.onScreenShareLocalIceCandidate,stunTurnJson2);
    } catch (JSONException e) {
      Utils.showLogs(TAG+ "Error handling ICE candidate: " + e.getMessage());
    }
  }

  @Override
  public void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, PeerConnection.SignalingState signalingState) {
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
    isConnected = true;
    HashMap<String, String> properties = new HashMap<>();
    properties.put("newState", stateString);
    String stunTurnJson2 = BBBSharedData.generatePayload(properties);
    PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.onScreenShareSignalingStateChange,stunTurnJson2);

  }

  @Override
  public void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, PeerConnection.IceGatheringState iceGatheringState) {

  }

  @Override
  public void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, PeerConnection.IceConnectionState iceConnectionState) {

  }
}
