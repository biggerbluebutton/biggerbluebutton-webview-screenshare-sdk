package com.bigbluebuttontabletsdk.broadcastScreen;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;

public interface AudioWebRTCClientInterface {


  void webRTCClient(AudioWebRTCClient audioWebRTCClient, IceCandidate iceCandidate);

  void webRTCClient(AudioWebRTCClient audioWebRTCClient, PeerConnection.SignalingState signalingState);

  void webRTCClient(AudioWebRTCClient audioWebRTCClient, PeerConnection.IceGatheringState iceGatheringState);

  void webRTCClient(AudioWebRTCClient audioWebRTCClient, PeerConnection.IceConnectionState iceConnectionState);
}
