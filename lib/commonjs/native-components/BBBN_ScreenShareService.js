"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.addScreenShareRemoteIceCandidate = addScreenShareRemoteIceCandidate;
exports.createScreenShareOffer = createScreenShareOffer;
exports.initializeScreenShare = initializeScreenShare;
exports.setScreenShareRemoteSDP = setScreenShareRemoteSDP;
exports.stopScreenShareBroadcastExtension = stopScreenShareBroadcastExtension;

var _reactNative = require("react-native");

const ScreenShareService = _reactNative.NativeModules.BBBN_ScreenShareService;

function initializeScreenShare() {
  console.log('BBBN_ScreenShareService ', ScreenShareService);
  ScreenShareService.initializeScreenShare();
}

function createScreenShareOffer(stunTurnJson) {
  ScreenShareService.createScreenShareOffer(stunTurnJson);
}

function setScreenShareRemoteSDP(remoteSDP) {
  ScreenShareService.setScreenShareRemoteSDP(remoteSDP);
}

function addScreenShareRemoteIceCandidate(remoteCandidateJson) {
  ScreenShareService.addScreenShareRemoteIceCandidate(remoteCandidateJson);
}

function stopScreenShareBroadcastExtension() {
  ScreenShareService.stopScreenShareBroadcastExtension();
}
//# sourceMappingURL=BBBN_ScreenShareService.js.map