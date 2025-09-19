"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
exports.handleWebviewMessage = handleWebviewMessage;
var _initializeScreenShare = _interopRequireDefault(require("../methods/initializeScreenShare"));
var _createScreenShareOffer = _interopRequireDefault(require("../methods/createScreenShareOffer"));
var _setScreenShareRemoteSDP = _interopRequireDefault(require("../methods/setScreenShareRemoteSDP"));
var _setFullAudioRemoteSDP = _interopRequireDefault(require("../methods/setFullAudioRemoteSDP"));
var _addScreenShareRemoteIceCandidate = _interopRequireDefault(require("../methods/addScreenShareRemoteIceCandidate"));
var _createFullAudioOffer = _interopRequireDefault(require("../methods/createFullAudioOffer"));
var _stopScreenShare = _interopRequireDefault(require("../methods/stopScreenShare"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function observePromiseResult(instanceId, webViewRef, sequence, prom) {
  prom.then(result => {
    console.log(`[${instanceId}] - Promise ${sequence} resolved!`, result);
    webViewRef.current.injectJavaScript(`window.nativeMethodCallResult(${sequence}, true ${result ? ',' + JSON.stringify(result) : ''});`);
  }).catch(exception => {
    console.error(`[${instanceId}] - Promise ${sequence} failed!`, exception);
    webViewRef.current.injectJavaScript(`window.nativeMethodCallResult(${sequence}, false ${exception ? ',' + JSON.stringify(exception) : ''});`);
  });
}
function handleWebviewMessage(instanceId, webViewRef, event, setCallState) {
  var _event$nativeEvent;
  const stringData = event === null || event === void 0 || (_event$nativeEvent = event.nativeEvent) === null || _event$nativeEvent === void 0 ? void 0 : _event$nativeEvent.data;
  const data = JSON.parse(stringData);
  console.log('handleWebviewMessage - ', instanceId);
  setCallState(data === null || data === void 0 ? void 0 : data.method);
  if (data !== null && data !== void 0 && data.method && data !== null && data !== void 0 && data.sequence) {
    let promise;
    switch (data === null || data === void 0 ? void 0 : data.method) {
      case 'initializeScreenShare':
        promise = (0, _initializeScreenShare.default)(instanceId);
        break;
      case 'createFullAudioOffer':
        promise = (0, _createFullAudioOffer.default)(instanceId, JSON.stringify(data === null || data === void 0 ? void 0 : data.arguments[0]));
        break;
      case 'createScreenShareOffer':
        promise = (0, _createScreenShareOffer.default)(instanceId, JSON.stringify(data === null || data === void 0 ? void 0 : data.arguments[0]));
        break;
      case 'setScreenShareRemoteSDP':
        promise = (0, _setScreenShareRemoteSDP.default)(instanceId, data === null || data === void 0 ? void 0 : data.arguments[0].sdp);
        break;
      case 'setFullAudioRemoteSDP':
        promise = (0, _setFullAudioRemoteSDP.default)(instanceId, data === null || data === void 0 ? void 0 : data.arguments[0].sdp);
        break;
      case 'addRemoteIceCandidate':
        promise = (0, _addScreenShareRemoteIceCandidate.default)(instanceId, JSON.stringify(data === null || data === void 0 ? void 0 : data.arguments[0]));
        break;
      case 'stopScreenShare':
        promise = (0, _stopScreenShare.default)(instanceId);
        break;
      default:
        throw `[${instanceId}] - Unknown method ${data === null || data === void 0 ? void 0 : data.method}`;
    }
    observePromiseResult(instanceId, webViewRef, data.sequence, promise);
  } else {
    console.log(`[${instanceId}] - Ignoring unknown message: $stringData`);
  }
}
var _default = exports.default = {
  handleWebviewMessage
};
//# sourceMappingURL=message-handler.js.map