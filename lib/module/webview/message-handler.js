import initializeScreenShare from '../methods/initializeScreenShare';
import createScreenShareOffer from '../methods/createScreenShareOffer';
import setScreenShareRemoteSDP from '../methods/setScreenShareRemoteSDP';
import setFullAudioRemoteSDP from '../methods/setFullAudioRemoteSDP';
import addScreenShareRemoteIceCandidate from '../methods/addScreenShareRemoteIceCandidate';
import createFullAudioOffer from '../methods/createFullAudioOffer';
import stopScreenShare from '../methods/stopScreenShare';

function observePromiseResult(instanceId, webViewRef, sequence, prom) {
  prom.then(result => {
    console.log(`[${instanceId}] - Promise ${sequence} resolved!`, result);
    webViewRef.current.injectJavaScript(`window.nativeMethodCallResult(${sequence}, true ${result ? ',' + JSON.stringify(result) : ''});`);
  }).catch(exception => {
    console.error(`[${instanceId}] - Promise ${sequence} failed!`, exception);
    webViewRef.current.injectJavaScript(`window.nativeMethodCallResult(${sequence}, false ${exception ? ',' + JSON.stringify(exception) : ''});`);
  });
}

export function handleWebviewMessage(instanceId, webViewRef, event) {
  var _event$nativeEvent;

  const stringData = event === null || event === void 0 ? void 0 : (_event$nativeEvent = event.nativeEvent) === null || _event$nativeEvent === void 0 ? void 0 : _event$nativeEvent.data;
  console.log('handleWebviewMessage - ', instanceId);
  const data = JSON.parse(stringData);

  if (data !== null && data !== void 0 && data.method && data !== null && data !== void 0 && data.sequence) {
    let promise;

    switch (data === null || data === void 0 ? void 0 : data.method) {
      case 'initializeScreenShare':
        promise = initializeScreenShare(instanceId);
        break;

      case 'createFullAudioOffer':
        promise = createFullAudioOffer(instanceId, JSON.stringify(data === null || data === void 0 ? void 0 : data.arguments[0]));
        break;

      case 'createScreenShareOffer':
        promise = createScreenShareOffer(instanceId, JSON.stringify(data === null || data === void 0 ? void 0 : data.arguments[0]));
        break;

      case 'setScreenShareRemoteSDP':
        promise = setScreenShareRemoteSDP(instanceId, data === null || data === void 0 ? void 0 : data.arguments[0].sdp);
        break;

      case 'setFullAudioRemoteSDP':
        promise = setFullAudioRemoteSDP(instanceId, data === null || data === void 0 ? void 0 : data.arguments[0].sdp);
        break;

      case 'addRemoteIceCandidate':
        promise = addScreenShareRemoteIceCandidate(instanceId, JSON.stringify(data === null || data === void 0 ? void 0 : data.arguments[0]));
        break;

      case 'stopScreenShare':
        promise = stopScreenShare(instanceId);
        break;

      default:
        throw `[${instanceId}] - Unknown method ${data === null || data === void 0 ? void 0 : data.method}`;
    }

    observePromiseResult(instanceId, webViewRef, data.sequence, promise);
  } else {
    console.log(`[${instanceId}] - Ignoring unknown message: $stringData`);
  }
}
export default {
  handleWebviewMessage
};
//# sourceMappingURL=message-handler.js.map