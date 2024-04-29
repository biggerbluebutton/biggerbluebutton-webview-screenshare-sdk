import nativeEmitter from '../native-messaging/emitter';
export function setupListener(_webViewRef) {
  // Resolve promise when SDP offer is available
  return nativeEmitter.addListener('onScreenShareLocalIceCandidate', jsonEncodedIceCandidate => {
    let iceCandidate = JSON.parse(jsonEncodedIceCandidate);

    if (typeof iceCandidate === 'string') {
      iceCandidate = JSON.parse(iceCandidate);
    }

    const event = {
      candidate: iceCandidate
    };

    _webViewRef.current.injectJavaScript(`window.bbbMobileScreenShareIceCandidateCallback && window.bbbMobileScreenShareIceCandidateCallback(${JSON.stringify(event)});`);
  });
}
//# sourceMappingURL=onScreenShareLocalIceCandidate.js.map