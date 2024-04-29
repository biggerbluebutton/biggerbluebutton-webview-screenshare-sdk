"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.createFullAudioOffer = createFullAudioOffer;
exports.setFullAudioRemoteSDP = setFullAudioRemoteSDP;

var _reactNative = require("react-native");

const FullAudioService = _reactNative.NativeModules.BBBN_FullAudioService; // export function initializeFullAudio() {
//   FullAudioService.initializeFullAudio();
// }

function createFullAudioOffer(stunTurnJson) {
  FullAudioService.createFullAudioOffer(stunTurnJson);
}

function setFullAudioRemoteSDP(remoteSDP) {
  FullAudioService.setFullAudioRemoteSDP(remoteSDP);
} // export function addFullAudioRemoteIceCandidate(remoteCandidateJson: string) {
//   FullAudioService.addFullAudioRemoteIceCandidate(remoteCandidateJson);
// }
//# sourceMappingURL=BBBN_FullAudioService.js.map