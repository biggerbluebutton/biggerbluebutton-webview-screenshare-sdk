import nativeEmitter from '../native-messaging/emitter';
export function setupListener(_webViewRef) {
  // Resolve promise when SDP offer is available
  return nativeEmitter.addListener('onBroadcastFinished', () => {
    console.log(`Broadcast finished`);

    _webViewRef.current.injectJavaScript(`window.bbbMobileScreenShareBroadcastFinishedCallback && window.bbbMobileScreenShareBroadcastFinishedCallback();`);
  });
}
//# sourceMappingURL=onBroadcastFinished.js.map