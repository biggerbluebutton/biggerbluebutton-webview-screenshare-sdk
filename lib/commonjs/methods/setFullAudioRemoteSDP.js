"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _BBBN_FullAudioService = require("../native-components/BBBN_FullAudioService");
var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
// Reference to the resolver of last call
let resolve = value => {
  console.log(`default resolve function called, this should never happen: ${value}`);
};

// Resolve promise when SDP offer is available
_emitter.default.addListener('onSetFullAudioRemoteSDPCompleted', () => {
  resolve(undefined);
});

// Entry point of this method
function setFullAudioRemoteSDP(instanceId, remoteSdp) {
  return new Promise((res, rej) => {
    // store the resolver for later call (when event is received)
    resolve = res;
    try {
      console.log(`[${instanceId}] - >nativeSetFullAudioRemoteSDP ${remoteSdp}`);
      // call native swift method that triggers the broadcast popup
      (0, _BBBN_FullAudioService.setFullAudioRemoteSDP)(remoteSdp);
    } catch (e) {
      rej(`Call to nativeSetFullAudioRemoteSDP failed`);
    }
  });
}
var _default = exports.default = setFullAudioRemoteSDP;
//# sourceMappingURL=setFullAudioRemoteSDP.js.map