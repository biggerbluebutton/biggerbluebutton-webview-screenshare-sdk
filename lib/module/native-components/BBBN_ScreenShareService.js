import { NativeModules } from 'react-native';
const ScreenShareService = NativeModules.BBBN_ScreenShareService;
export function initializeScreenShare() {
  console.log('BBBN_ScreenShareService ', ScreenShareService);
  ScreenShareService.initializeScreenShare();
}
export function createScreenShareOffer(stunTurnJson) {
  ScreenShareService.createScreenShareOffer(stunTurnJson);
}
export function setScreenShareRemoteSDP(remoteSDP) {
  ScreenShareService.setScreenShareRemoteSDP(remoteSDP);
}
export function addScreenShareRemoteIceCandidate(remoteCandidateJson) {
  ScreenShareService.addScreenShareRemoteIceCandidate(remoteCandidateJson);
}
export function stopScreenShareBroadcastExtension() {
  ScreenShareService.stopScreenShareBroadcastExtension();
}
//# sourceMappingURL=BBBN_ScreenShareService.js.map