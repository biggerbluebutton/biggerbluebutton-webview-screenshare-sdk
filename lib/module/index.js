import { Platform, NativeModules, SafeAreaView, View, FlatList } from 'react-native';
import React, { useEffect, useRef, useCallback } from 'react';
import BBBN_SystemBroadcastPicker from './native-components/BBBN_SystemBroadcastPicker';
import { WebView } from 'react-native-webview';
import { handleWebviewMessage } from './webview/message-handler';
import * as onScreenShareLocalIceCandidate from './events/onScreenShareLocalIceCandidate';
import * as onScreenShareSignalingStateChange from './events/onScreenShareSignalingStateChange';
import * as onBroadcastFinished from './events/onBroadcastFinished';
const data = {
  instances: 0
};

const renderPlatformSpecificComponents = () => Platform.select({
  ios: /*#__PURE__*/React.createElement(BBBN_SystemBroadcastPicker, null),
  android: null
});

export const BigBlueButtonTablet = _ref => {
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
  const webViewRef = useRef(null);
  const thisInstanceId = ++data.instances;
  useEffect(() => {
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
  useEffect(() => {
    return () => {
      if (Platform.OS === 'android') {
        NativeModules.BBBN_ScreenShareService.handleBackPress();
      }
    };
  }, []);
  const renderItem = useCallback(_ref2 => {
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
  return /*#__PURE__*/React.createElement(SafeAreaView, {
    style: {
      flex: 1
    }
  }, (tabsData === null || tabsData === void 0 ? void 0 : tabsData.length) > 1 && /*#__PURE__*/React.createElement(View, {
    style: {
      flexDirection: 'row'
    }
  }, /*#__PURE__*/React.createElement(FlatList, {
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
  })), /*#__PURE__*/React.createElement(View, {
    style: {
      flex: 1
    }
  }, renderPlatformSpecificComponents(), /*#__PURE__*/React.createElement(WebView, {
    ref: webViewRef,
    source: {
      uri: (currentTab === null || currentTab === void 0 ? void 0 : currentTab.url) || url
    },
    style: { ...style,
      marginTop: 0
    },
    contentMode: 'mobile',
    onMessage: msg => handleWebviewMessage(thisInstanceId, webViewRef, msg, callState),
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
//# sourceMappingURL=index.js.map