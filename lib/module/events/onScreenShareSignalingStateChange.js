import nativeEmitter from '../native-messaging/emitter';
export function setupListener(_webViewRef) {
  // Resolve promise when SDP offer is available
  return nativeEmitter.addListener('onScreenShareSignalingStateChange', newState => {
    console.log(`Temos um novo state: ${newState}`);
    _webViewRef.current.injectJavaScript(`window.bbbMobileScreenShareSignalingStateChangeCallback && window.bbbMobileScreenShareSignalingStateChangeCallback(${JSON.stringify(newState)});`);
  });
}
//# sourceMappingURL=onScreenShareSignalingStateChange.js.map