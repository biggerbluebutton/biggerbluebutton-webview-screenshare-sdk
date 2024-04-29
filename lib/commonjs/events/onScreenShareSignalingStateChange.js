"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.setupListener = setupListener;

var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function setupListener(_webViewRef) {
  // Resolve promise when SDP offer is available
  return _emitter.default.addListener('onScreenShareSignalingStateChange', newState => {
    console.log(`Temos um novo state: ${newState}`);

    _webViewRef.current.injectJavaScript(`window.bbbMobileScreenShareSignalingStateChangeCallback && window.bbbMobileScreenShareSignalingStateChangeCallback(${JSON.stringify(newState)});`);
  });
}
//# sourceMappingURL=onScreenShareSignalingStateChange.js.map