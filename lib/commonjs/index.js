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

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _getRequireWildcardCache(nodeInterop) { if (typeof WeakMap !== "function") return null; var cacheBabelInterop = new WeakMap(); var cacheNodeInterop = new WeakMap(); return (_getRequireWildcardCache = function (nodeInterop) { return nodeInterop ? cacheNodeInterop : cacheBabelInterop; })(nodeInterop); }

function _interopRequireWildcard(obj, nodeInterop) { if (!nodeInterop && obj && obj.__esModule) { return obj; } if (obj === null || typeof obj !== "object" && typeof obj !== "function") { return { default: obj }; } var cache = _getRequireWildcardCache(nodeInterop); if (cache && cache.has(obj)) { return cache.get(obj); } var newObj = {}; var hasPropertyDescriptor = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var key in obj) { if (key !== "default" && Object.prototype.hasOwnProperty.call(obj, key)) { var desc = hasPropertyDescriptor ? Object.getOwnPropertyDescriptor(obj, key) : null; if (desc && (desc.get || desc.set)) { Object.defineProperty(newObj, key, desc); } else { newObj[key] = obj[key]; } } } newObj.default = obj; if (cache) { cache.set(obj, newObj); } return newObj; }

const data = {
  instances: 0
};

const renderPlatformSpecificComponents = () => _reactNative.Platform.select({
  ios: /*#__PURE__*/_react.default.createElement(_BBBN_SystemBroadcastPicker.default, null),
  android: null
});

const BigBlueButtonTablet = _ref => {
  let {
    url,
    style,
    onError,
    onSuccess,
    callState,
    onShouldStartLoadWithRequest,
    injectedJavaScript,
    onNavigationStateChange,
    onOpenWindow,
    renderTabItem,
    currentTab,
    tabsData
  } = _ref;
  const webViewRef = (0, _react.useRef)(null);
  const thisInstanceId = ++data.instances;
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
  const renderItem = (0, _react.useCallback)(_ref2 => {
    let {
      item
    } = _ref2;

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
    style: { ...style,
      marginTop: 0
    },
    contentMode: 'mobile',
    onMessage: msg => (0, _messageHandler.handleWebviewMessage)(thisInstanceId, webViewRef, msg, callState),
    onOpenWindow: onOpenWindow,
    applicationNameForUserAgent: "BigBlueButton-Tablet",
    allowsInlineMediaPlayback: true,
    injectedJavaScript: injectedJavaScript,
    javaScriptEnabled: true,
    mediaCapturePermissionGrantType: 'grant',
    onShouldStartLoadWithRequest: onShouldStartLoadWithRequest,
    onNavigationStateChange: onNavigationStateChange,
    onLoadEnd: content => {
      if (typeof content.nativeEvent.code !== 'undefined') {
        if (onError) onError(content);
      } else {
        if (onSuccess) onSuccess(content);
      }
    }
  })));
};

exports.BigBlueButtonTablet = BigBlueButtonTablet;
//# sourceMappingURL=index.js.map