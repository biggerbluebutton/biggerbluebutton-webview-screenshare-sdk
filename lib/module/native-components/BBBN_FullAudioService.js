import { NativeModules } from 'react-native';
const FullAudioService = NativeModules.BBBN_FullAudioService;

// export function initializeFullAudio() {
//   FullAudioService.initializeFullAudio();
// }

export function createFullAudioOffer(stunTurnJson) {
  FullAudioService.createFullAudioOffer(stunTurnJson);
}
export function setFullAudioRemoteSDP(remoteSDP) {
  FullAudioService.setFullAudioRemoteSDP(remoteSDP);
}

// export function addFullAudioRemoteIceCandidate(remoteCandidateJson: string) {
//   FullAudioService.addFullAudioRemoteIceCandidate(remoteCandidateJson);
// }
//# sourceMappingURL=BBBN_FullAudioService.js.map