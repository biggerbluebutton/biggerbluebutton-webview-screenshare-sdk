import { createScreenShareOffer as nativeCreateScreenShareOffer } from '../native-components/BBBN_ScreenShareService';
import nativeEmitter from '../native-messaging/emitter';

// Reference to the resolver of last call
let resolve = a => {
  console.log(`default resolve function called, this should never happen: ${a}`);
};

// Resolve promise when SDP offer is available
nativeEmitter.addListener('onScreenShareOfferCreated', sdp => {
  console.log('broadcast onScreenShareOfferCreated');
  resolve(sdp);
});

// Entry point of this method
function createScreenShareOffer(instanceId, stunTurnJson) {
  return new Promise((res, rej) => {
    // store the resolver for later call (when event is received)
    resolve = res;
    try {
      console.log(`[${instanceId}] - >nativeCreateScreenShareOffer (${stunTurnJson})`);
      // call native swift method that triggers the broadcast popup
      nativeCreateScreenShareOffer(stunTurnJson);
    } catch (e) {
      rej(`Call to nativeCreateScreenShareOffer failed`);
    }
  });
}
export default createScreenShareOffer;
//# sourceMappingURL=createScreenShareOffer.js.map