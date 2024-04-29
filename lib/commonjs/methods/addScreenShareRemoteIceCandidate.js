"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;

var _BBBN_ScreenShareService = require("../native-components/BBBN_ScreenShareService");

var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

// Reference to the resolver of last call
let resolve = value => {
  console.log(`default resolve function called, this should never happen: ${value}`);
}; // Resolve promise when SDP offer is available


_emitter.default.addListener('onAddScreenShareRemoteIceCandidateCompleted', () => {
  resolve(undefined);
}); // Entry point of this method


function addScreenShareRemoteIceCandidate(instanceId, remoteCandidateJson) {
  return new Promise((res, rej) => {
    // store the resolver for later call (when event is received)
    resolve = res;

    try {
      console.log(`[${instanceId}] - >nativeAddScreenShareRemoteIceCandidate ${remoteCandidateJson}`); // call native swift method that triggers the broadcast popup

      (0, _BBBN_ScreenShareService.addScreenShareRemoteIceCandidate)(remoteCandidateJson);
    } catch (e) {
      rej(`Call to nativeAddScreenShareRemoteIceCandidate failed`);
    }
  });
}

var _default = addScreenShareRemoteIceCandidate;
exports.default = _default;
//# sourceMappingURL=addScreenShareRemoteIceCandidate.js.map