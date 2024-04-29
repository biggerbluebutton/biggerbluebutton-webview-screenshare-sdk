package com.bigbluebuttontabletsdk.broadcastScreen;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;

public interface ScreenShareWebRTCClientInterface {

  void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, IceCandidate iceCandidate);

  void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, PeerConnection.SignalingState signalingState);

  void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, PeerConnection.IceGatheringState iceGatheringState);

  void webRTCClient(ScreenShareWebRTCClient screenShareWebRTCClient, PeerConnection.IceConnectionState iceConnectionState);
}
