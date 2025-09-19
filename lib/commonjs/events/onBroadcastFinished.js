"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.setupListener = setupListener;
var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function setupListener(_webViewRef) {
  // Resolve promise when SDP offer is available
  return _emitter.default.addListener('onBroadcastFinished', () => {
    console.log(`Broadcast finished`);
    _webViewRef.current.injectJavaScript(`window.bbbMobileScreenShareBroadcastFinishedCallback && window.bbbMobileScreenShareBroadcastFinishedCallback();`);
  });
}
//# sourceMappingURL=onBroadcastFinished.js.map