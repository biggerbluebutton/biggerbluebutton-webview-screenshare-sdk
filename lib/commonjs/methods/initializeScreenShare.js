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

// Log a message when broadcast is requested
_emitter.default.addListener('onBroadcastRequested', () => {
  console.log(`Broadcast requested`);
});

// Resolve promise when broadcast is started (this event means that user confirmed the screenshare)
_emitter.default.addListener('onBroadcastStarted', () => {
  console.log(`Broadcast onBroadcastStarted`);
  resolve(null);
});

// Entry point of this method
function initializeScreenShare(instanceId) {
  return new Promise((res, rej) => {
    // store the resolver for later call (when event is received)
    resolve = res;
    try {
      // call native swift method that triggers the broadcast popup
      console.log(`[${instanceId}] - >nativeInitializeScreenShare`);
      (0, _BBBN_ScreenShareService.initializeScreenShare)();
    } catch (e) {
      rej(`Call to nativeInitializeScreenShare failed zzy`);
    }
  });
}
var _default = exports.default = initializeScreenShare;
//# sourceMappingURL=initializeScreenShare.js.map