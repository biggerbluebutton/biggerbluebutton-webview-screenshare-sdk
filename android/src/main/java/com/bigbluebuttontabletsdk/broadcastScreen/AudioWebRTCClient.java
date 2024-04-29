package com.bigbluebuttontabletsdk.broadcastScreen;

import android.content.Context;
import android.media.AudioManager;

import com.bigbluebuttontabletsdk.utils.EventEmitterData;
import com.bigbluebuttontabletsdk.utils.Utils;
import com.facebook.react.bridge.ReactApplicationContext;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import java.util.Collections;
import java.util.List;

public class AudioWebRTCClient implements PeerConnection.Observer{
  private static final String TAG = "AudioWebRTCClient";
  private Context mContext;
  private AudioWebRTCClientInterface delegate;
  private MediaConstraints mediaConstraints;
  private PeerConnectionFactory factory;
  private PeerConnection peerConnection;
  private SurfaceTextureHelper surfaceTextureHelper;
  private AudioSource audioSource;
  private AudioTrack audioTrack;
  private AudioManager audioManager;
  public AudioWebRTCClient(Context context, List<PeerConnection.IceServer> iceServers, AudioWebRTCClientInterface delegate){
    Utils.showLogs(TAG+ "ScreenShareWebRTCClient>>>> ");
    this.mContext = context;
    this.delegate = delegate;
    this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    mediaConstraints=new MediaConstraints();
    mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "True"));
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
    configureAudioSession();
  }

  private void createMediaSenders() {
    String streamId = "stream";
    // Video
    AudioTrack audioTrack1 = createAudioTrack();
    if (audioTrack1!=null)
      peerConnection.addTrack(audioTrack1, Collections.singletonList(streamId));
  }
  public void configureAudioSession() {
    try {
      // Request audio focus for playback
      int result = audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
          if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Audio focus was lost, consider lowering the volume or pausing playback
            restoreAudioSession();
          }
        }
      }, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);

      if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
        // Set mode to handle voice communications
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        // Set the audio stream for voice calls to be played
        audioManager.setSpeakerphoneOn(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private void restoreAudioSession() {
    // Releasing the audio focus
    if (audioManager != null) {
      audioManager.abandonAudioFocus(null);
    }

    // Reset the audio mode to normal
    audioManager.setMode(AudioManager.MODE_NORMAL);

    // Optionally, turn off the speakerphone if it was enabled
    audioManager.setSpeakerphoneOn(false);
  }
  public AudioTrack createAudioTrack() {
    audioSource = factory.createAudioSource(new MediaConstraints());
    audioTrack = factory.createAudioTrack("audio0", audioSource);

    // Enable the audio track
    audioTrack.setEnabled(true);
    return audioTrack;
  }

  public void offer(ReactApplicationContext context) {
    Utils.showLogs(TAG+ "createOfferAudio");
    peerConnection.createOffer(new SdpObserver() {
      private SessionDescription mSessionDescription;

      @Override
      public void onCreateSuccess(SessionDescription sessionDescription) {
        mSessionDescription = sessionDescription;
        Utils.showLogs(TAG+ "AudioCreateOffer"+Utils.toJson(mSessionDescription));
        EventEmitterData.emitEvent(context,EventEmitterData.onFullAudioOfferCreated, sessionDescription.toString());
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
//        HashMap<String, String> properties = new HashMap<>();
//        properties.put("sdp", sessionDescription.description);
//        String stunTurnJson2 = BBBSharedData.generatePayload(properties);
//        PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.screenShareOfferCreated,stunTurnJson2);

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
    Utils.showLogs(TAG+ "setRemoteSDPAudio");
    SessionDescription rtcSessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, remoteSDP);
    peerConnection.setRemoteDescription(new SdpObserver() {
      @Override
      public void onCreateSuccess(SessionDescription sessionDescription) {
        Utils.showLogs(TAG+ "setRemoteSDP onCreateSuccess ");
      }

      @Override
      public void onSetSuccess() {
        Utils.showLogs(TAG+ "setRemoteSDP onSetSuccess ");
      //  PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.setScreenShareRemoteSDPCompleted,BBBSharedData.generatePayload(new HashMap<>()));

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
  //  PreferencesUtils.getInstance(mContext).putString(BBBSharedData.SharedData.setScreenShareRemoteSDPCompleted,BBBSharedData.generatePayload(new HashMap<>()));
  }
  @Override
  public void onSignalingChange(PeerConnection.SignalingState signalingState) {

  }

  @Override
  public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

  }

  @Override
  public void onIceConnectionReceivingChange(boolean b) {

  }

  @Override
  public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

  }

  @Override
  public void onIceCandidate(IceCandidate iceCandidate) {

  }

  @Override
  public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

  }

  @Override
  public void onAddStream(MediaStream mediaStream) {

  }

  @Override
  public void onRemoveStream(MediaStream mediaStream) {

  }

  @Override
  public void onDataChannel(DataChannel dataChannel) {

  }

  @Override
  public void onRenegotiationNeeded() {

  }
}
