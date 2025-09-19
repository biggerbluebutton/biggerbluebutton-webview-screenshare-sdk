"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _BBBN_ScreenShareService = require("../native-components/BBBN_ScreenShareService");
var _emitter = _interopRequireDefault(require("../native-messaging/emitter"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
// Reference to the resolver of last call
let resolve = a => {
  console.log(`default resolve function called, this should never happen: ${a}`);
};

// Resolve promise when broadcast is started (this event means that user confirmed the screenshare)
_emitter.default.addListener('onBroadcastFinished', () => {
  resolve(null);
});

// Entry point of this method
function stopScreenShare(instanceId) {
  return new Promise((res, rej) => {
    // store the resolver for later call (when event is received)
    resolve = res;
    try {
      // call native swift method that triggers the broadcast popup
      console.log(`[${instanceId}] - >stopScreenShare`);
      (0, _BBBN_ScreenShareService.stopScreenShareBroadcastExtension)();
    } catch (e) {
      rej(`Call to stopScreenShare failed zzy`);
    }
  });
}
var _default = exports.default = stopScreenShare;
//# sourceMappingURL=stopScreenShare.js.map