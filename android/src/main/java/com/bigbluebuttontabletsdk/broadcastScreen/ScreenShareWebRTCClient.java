package com.bigbluebuttontabletsdk.broadcastScreen;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.util.Log;

import com.bigbluebuttontabletsdk.utils.BBBSharedData;
import com.bigbluebuttontabletsdk.utils.PreferencesUtils;
import com.bigbluebuttontabletsdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ScreenShareWebRTCClient implements PeerConnection.Observer {
  private static final String TAG = "ScreenShareWebRTCClient  ";
  private PeerConnectionFactory factory;
  private MediaStream mediaStream;
  private ScreenCapturerAndroid screenCapturer;
  private ScreenShareWebRTCClientInterface delegate;
  private PeerConnection peerConnection;
  private MediaConstraints mediaConstraints;
  private VideoSource videoSource;
  private VideoCapturer videoCapturer;
  private VideoTrack localVideoTrack;
  private SurfaceTextureHelper surfaceTextureHelper;
  private boolean isRatioDefined = false;
  private Context mContext;

  public ScreenShareWebRTCClient(Context context,List<PeerConnection.IceServer> iceServers,VideoCapturer videoCapturer ,ScreenShareWebRTCClientInterface delegate) {
    Utils.showLogs(TAG+ "ScreenShareWebRTCClient>>>> ");
    this.mContext = context;
    this.delegate = delegate;
    this.videoCapturer = videoCapturer;

    mediaConstraints=new MediaConstraints();
    mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"));
    mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
    // Initialize PeerConnectionFactory
    PeerConnectionFactory.InitializationOptions initializationOptions =
      PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions();
    PeerConnectionFactory.initialize(initializationOptions);

    EglBase eglBase = EglBase.create();
    DefaultVideoEncoderFactory videoEncoderFactory = new DefaultVideoEncoderFactory(
      eglBase.getEglBaseContext(), true, true);
    DefaultVideoDecoderFactory videoDecoderFactory = new DefaultVideoDecoderFactory(
      eglBase.getEglBaseContext());

    // Create PeerConnectionFactory with Encoder and Decoder factories
    PeerConnectionFactory.Builder factoryBuilder = PeerConnectionFactory.builder()
      .setOptions(new PeerConnectionFactory.Options())
      .setVideoEncoderFactory(videoEncoderFactory)
      .setVideoDecoderFactory(videoDecoderFactory);
    factory = factoryBuilder.createPeerConnectionFactory();
    // Create media constraints and configuration for PeerConnection
    MediaConstraints medias = new MediaConstraints();
    medias.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));

    PeerConnection.RTCConfiguration config = new PeerConnection.RTCConfiguration(iceServers);
    config.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
    config.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_ONCE;
    // Create PeerConnection
    peerConnection = factory.createPeerConnection(config, medias, this);
    surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread",
      eglBase.getEglBaseContext());

    createMediaSenders();
  }
  private void createMediaSenders() {
    String streamId = "stream";
    // Video
    VideoTrack videoTrack = createVideoTrack2();
    if (videoTrack!=null)
      peerConnection.addTrack(videoTrack, Collections.singletonList(streamId));

    // Audio
//    AudioTrack audioTrack = createAudioTrack();
//    if (audioTrack!= null)
//      peerConnection.addTrack(audioTrack, Collections.singletonList(streamId));
  }
  public VideoTrack createVideoTrack2() {
    videoSource = factory.createVideoSource(true);
    videoCapturer.initialize(surfaceTextureHelper, mContext, videoSource.getCapturerObserver());
    // Start capturing video data
    videoCapturer.startCapture(1280, 720, 30);
    // Create a video track using the factory, with a specific trackId
    VideoTrack videoTrack = factory.createVideoTrack("video0", videoSource);
    // Enable the video track
    videoTrack.setEnabled(true);
    return videoTrack;
  }

  private AudioTrack createAudioTrack() {
    // Create an AudioSource with some audio constraints if necessary
    AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
    AudioTrack localAudioTrack = factory.createAudioTrack("audio0", audioSource);
    localAudioTrack.setEnabled(true);
    return localAudioTrack;
  }
  public void offer() {
    Utils.showLogs(TAG+ "createOffer>>>> ");
    peerConnection.createOffer(new SdpObserver() {
      private SessionDescription mSessionDescription;

      @Override
      public void onCreateSuccess(SessionDescription sessionDescription) {
        mSessionDescription = sessionDescription;
        Utils.showLogs(TAG+ "createOffer  onCreateSuccess "+Utils.toJson(mSessionDescription));

        peerConnection.setLocalDescription(new SdpObserver() {
          @Override
          public void onCreateSuccess(SessionDescription sessionDescription) {
            Utils.showLogs(TAG+ "createOffer setLocalDescription onCreateSuccess "+Utils.toJson(sessionDescription));

          }

          @Override
          public void onSetSuccess() {
            Utils.showLogs(TAG+ " createOffer setLocalDescription onSetSuccess ");

          }

          @Override
          public void onCreateFailure(String s) {
            Utils.showLogs(TAG+ " createOffer setLocalDescription onCreateFailure "+s);
          }

          @Override
          public void onSetFailure(String s) {
            Utils.showLogs(TAG+ " createOffer setLocalDescription onSetFailure "+s);
          }
        }, sessionDescription);
        HashMap<String, String> properties = new HashMap<>();
        properties.put("sdp", sessionDescription.description);
        String stunTurnJson2 = BBBSharedData.generatePayload(properties);
        PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.screenShareOfferCreated,stunTurnJson2);

      }

      @Override
      public void onSetSuccess() {
        Utils.showLogs(TAG+ " createOffer onSetSuccess ");

      }

      @Override
      public void onCreateFailure(String s) {
        Utils.showLogs(TAG+ " createOffer onCreateFailure "+s);
      }

      @Override
      public void onSetFailure(String s) {
        Utils.showLogs(TAG+ " createOffer onSetFailure "+s);
      }
    }, mediaConstraints);
  }

  public void setRemoteSDP(String remoteSDP) {
    Utils.showLogs(TAG+ "setRemoteSDP >>>");
    SessionDescription rtcSessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, remoteSDP);
    peerConnection.setRemoteDescription(new SdpObserver() {
      @Override
      public void onCreateSuccess(SessionDescription sessionDescription) {
        Utils.showLogs(TAG+ "setRemoteSDP onCreateSuccess ");
      }

      @Override
      public void onSetSuccess() {
        Utils.showLogs(TAG+ "setRemoteSDP onSetSuccess ");
        PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.setScreenShareRemoteSDPCompleted,BBBSharedData.generatePayload(new HashMap<>()));

      }

      @Override
      public void onCreateFailure(String s) {
        Utils.showLogs(TAG+ "setRemoteSDP onSetFailure "+s);
      }

      @Override
      public void onSetFailure(String s) {
        Utils.showLogs(TAG+ "setRemoteSDP onSetFailure "+s);

      }
    }, rtcSessionDescription);
  }


  public void setRemoteCandidate(IceCandidate remoteIceCandidate) {
    peerConnection.addIceCandidate(remoteIceCandidate);
    PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.setScreenShareRemoteSDPCompleted,BBBSharedData.generatePayload(new HashMap<>()));
  }


  @Override
  public void onSignalingChange(PeerConnection.SignalingState signalingState) {
    delegate.webRTCClient(this, signalingState);
  }

  @Override
  public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
    delegate.webRTCClient(this, iceConnectionState);
  }

  @Override
  public void onIceConnectionReceivingChange(boolean b) {

  }

  @Override
  public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
    delegate.webRTCClient(this, iceGatheringState);
  }

  @Override
  public void onIceCandidate(IceCandidate iceCandidate) {
    delegate.webRTCClient(this, iceCandidate);
  }

  @Override
  public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

  }

  @Override
  public void onAddStream(MediaStream mediaStream) {}

  @Override
  public void onRemoveStream(MediaStream mediaStream) {}

  @Override
  public void onDataChannel(DataChannel dataChannel) {}

  @Override
  public void onRenegotiationNeeded() {}

  @Override
  public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

  }



  // Other methods such as setAudioEnabled(), muteAudio(), unmuteAudio(), speakerOn(), speakerOff(), etc., can be implemented similarly.
}

