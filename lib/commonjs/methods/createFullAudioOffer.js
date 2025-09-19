"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _BBBN_FullAudioService = require("../native-components/BBBN_FullAudioService");
var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
// Reference to the resolver of last call
let resolve = a => {
  console.log(`default resolve function called, this should never happen: ${a}`);
};

// Resolve promise when SDP offer is available
_emitter.default.addListener('onFullAudioOfferCreated', sdp => {
  resolve(sdp);
});

// Entry point of this method
function createFullAudioOffer(instanceId, stunTurnJson) {
  return new Promise((res, rej) => {
    // store the resolver for later call (when event is received)
    resolve = res;
    try {
      console.log(`[${instanceId}] - >nativeCreateFullAudioOffer (${stunTurnJson})`);
      // call native swift method that triggers the broadcast popup
      (0, _BBBN_FullAudioService.createFullAudioOffer)(stunTurnJson);
    } catch (e) {
      rej(`Call to nativeCreateFullAudioOffer failed`);
    }
  });
}
var _default = exports.default = createFullAudioOffer;
//# sourceMappingURL=createFullAudioOffer.js.map