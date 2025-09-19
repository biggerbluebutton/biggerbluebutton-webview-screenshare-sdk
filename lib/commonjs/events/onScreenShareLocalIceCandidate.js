"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.setupListener = setupListener;
var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function setupListener(_webViewRef) {
  // Resolve promise when SDP offer is available
  return _emitter.default.addListener('onScreenShareLocalIceCandidate', jsonEncodedIceCandidate => {
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