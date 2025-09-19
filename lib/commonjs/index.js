"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.BigBlueButtonTablet = void 0;
var _reactNative = require("react-native");
var _react = _interopRequireWildcard(require("react"));
var _BBBN_SystemBroadcastPicker = _interopRequireDefault(require("./native-components/BBBN_SystemBroadcastPicker"));
var _reactNativeWebview = require("react-native-webview");
var _messageHandler = require("./webview/message-handler");
var onScreenShareLocalIceCandidate = _interopRequireWildcard(require("./events/onScreenShareLocalIceCandidate"));
var onScreenShareSignalingStateChange = _interopRequireWildcard(require("./events/onScreenShareSignalingStateChange"));
var onBroadcastFinished = _interopRequireWildcard(require("./events/onBroadcastFinished"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function _interopRequireWildcard(e, t) { if ("function" == typeof WeakMap) var r = new WeakMap(), n = new WeakMap(); return (_interopRequireWildcard = function (e, t) { if (!t && e && e.__esModule) return e; var o, i, f = { __proto__: null, default: e }; if (null === e || "object" != typeof e && "function" != typeof e) return f; if (o = t ? n : r) { if (o.has(e)) return o.get(e); o.set(e, f); } for (const t in e) "default" !== t && {}.hasOwnProperty.call(e, t) && ((i = (o = Object.defineProperty) && Object.getOwnPropertyDescriptor(e, t)) && (i.get || i.set) ? o(f, t, i) : f[t] = e[t]); return f; })(e, t); }
const data = {
  instances: 0
};
const renderPlatformSpecificComponents = () => _reactNative.Platform.select({
  ios: /*#__PURE__*/_react.default.createElement(_BBBN_SystemBroadcastPicker.default, null),
  android: null
});
const BigBlueButtonTablet = ({
  url,
  style,
  onError,
  onSuccess,
  setCallState,
  onShouldStartLoadWithRequest,
  injectedJavaScript,
  onNavigationStateChange,
  onOpenWindow,
  renderTabItem,
  currentTab,
  tabsData
}) => {
  const webViewRef = (0, _react.useRef)(null);
  const thisInstanceId = ++data.instances;
  const TARGET_SCALE = 0.85;
  const zoomOutBeforeLoad = `
  (function(){
    try {
      var s=${TARGET_SCALE};
      var meta = document.querySelector('meta[name="viewport"]');
      if (!meta) {
        meta = document.createElement('meta');
        meta.name = 'viewport';
        document.head.appendChild(meta);
      }
      // Set an initial zoom-out, allow pinch-zoom afterwards
      meta.content = 'width=device-width, initial-scale=' + s + ', minimum-scale=0.5, maximum-scale=3, user-scalable=yes, viewport-fit=cover';
      document.documentElement.style.overflowY = 'auto';
      document.body.style.overflowY = 'auto';
      document.documentElement.style.height = 'auto';
      document.body.style.height = 'auto';
    } catch(e) {}
  })();
  true;
  `;
  const autoFitAfterLoad = `
(function(){
  try {
    var s=${TARGET_SCALE};
    var floor = 0.75;   // don't go below 75%
    var step  = 0.05;   // shrink by 5% per pass
    function setViewportScale(x){
      var m = document.querySelector('meta[name="viewport"]');
      if (!m) return;
      m.content = 'width=device-width, initial-scale=' + x + ', minimum-scale=0.5, maximum-scale=3, user-scalable=yes, viewport-fit=cover';
    }
    function applyTransformFallback(x){
      // Final fallback: visually scale everything and widen layout to avoid cropping
      var html = document.documentElement, body = document.body;
      html.style.transformOrigin = 'top left';
      html.style.transform = 'scale(' + x + ')';
      html.style.width = (100/x) + 'vw';
      body.style.width = (100/x) + 'vw';
      html.style.height = 'auto';
      body.style.height = 'auto';
      // ensure nothing hides under a footer
      body.style.paddingBottom = '12px';
    }
    function fits(){
      var H = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);
      var C = Math.max(document.documentElement.clientHeight, window.innerHeight||0);
      return H <= C + 2; // allow tiny rounding
    }
    function tryFit(){
      if (fits()) return;
      if (s > floor) {
        s = Math.max(floor, s - step);
        setViewportScale(s);
        setTimeout(tryFit, 120);
      } else {
        // Still taller? Force visual scale.
        applyTransformFallback(s);
      }
    }
    // kick after layout stabilizes
    setTimeout(tryFit, 120);
  } catch(e){}
})();
true;
`;
  (0, _react.useEffect)(() => {
    const logPrefix = `[${thisInstanceId}] - ${url.substring(8, 16)}`;
    console.log(`${logPrefix} - addingListeners`);
    const listeners = [];
    listeners.push(onScreenShareLocalIceCandidate.setupListener(webViewRef));
    listeners.push(onScreenShareSignalingStateChange.setupListener(webViewRef));
    listeners.push(onBroadcastFinished.setupListener(webViewRef));
    return () => {
      console.log(`${logPrefix} - Removing listeners`);
      listeners.forEach((listener, index) => {
        console.log(`${logPrefix} - Removing listener ${index}`);
        listener.remove();
      });
    };
  }, [webViewRef, thisInstanceId, url]);
  (0, _react.useEffect)(() => {
    return () => {
      if (_reactNative.Platform.OS === 'android') {
        _reactNative.NativeModules.BBBN_ScreenShareService.handleBackPress();
      }
    };
  }, []);
  const renderItem = (0, _react.useCallback)(({
    item
  }) => {
    if (renderTabItem) {
      return renderTabItem({
        item
      });
    }
    return null;
  }, [renderTabItem]);
  return /*#__PURE__*/_react.default.createElement(_reactNative.SafeAreaView, {
    style: {
      flex: 1
    }
  }, (tabsData === null || tabsData === void 0 ? void 0 : tabsData.length) > 1 && /*#__PURE__*/_react.default.createElement(_reactNative.View, {
    style: {
      flexDirection: 'row'
    }
  }, /*#__PURE__*/_react.default.createElement(_reactNative.FlatList, {
    horizontal: true,
    data: tabsData,
    renderItem: renderItem,
    keyExtractor: item => item.id.toString(),
    initialNumToRender: tabsData.length,
    getItemLayout: (_, index) => ({
      length: 100,
      offset: 100 * index,
      index
    })
  })), /*#__PURE__*/_react.default.createElement(_reactNative.View, {
    style: {
      flex: 1
    }
  }, renderPlatformSpecificComponents(), /*#__PURE__*/_react.default.createElement(_reactNativeWebview.WebView, {
    ref: webViewRef,
    source: {
      uri: (currentTab === null || currentTab === void 0 ? void 0 : currentTab.url) || url
    },
    style: {
      ...style,
      flex: 1,
      marginTop: 0
    },
    contentMode: "mobile",
    applicationNameForUserAgent: "BigBlueButton-Tablet",
    allowsInlineMediaPlayback: true,
    javaScriptEnabled: true,
    mediaCapturePermissionGrantType: "grant",
    scrollEnabled: true,
    bounces: true
    // automaticallyAdjustContentInsets={false}
    // contentInsetAdjustmentBehavior="never"
    ,
    nestedScrollEnabled: true,
    overScrollMode: "always"
    // setSupportMultipleWindows={false}
    ,
    onOpenWindow: onOpenWindow,
    injectedJavaScriptBeforeContentLoaded: zoomOutBeforeLoad,
    injectedJavaScript: autoFitAfterLoad && injectedJavaScript,
    onMessage: msg => (0, _messageHandler.handleWebviewMessage)(0, webViewRef, msg, setCallState),
    onShouldStartLoadWithRequest: onShouldStartLoadWithRequest,
    onNavigationStateChange: onNavigationStateChange,
    onLoadEnd: content => {
      if (typeof content.nativeEvent.code !== 'undefined') {
        onError === null || onError === void 0 || onError(content);
      } else {
        onSuccess === null || onSuccess === void 0 || onSuccess(content);
      }
    }
  })));
};
exports.BigBlueButtonTablet = BigBlueButtonTablet;
//# sourceMappingURL=index.js.map