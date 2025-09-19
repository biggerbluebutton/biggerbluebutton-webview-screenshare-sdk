import {
  EmitterSubscription,
  Platform,
  ViewStyle,
  NativeModules,
  SafeAreaView,
  View,
  FlatList,
} from 'react-native';
import React, { useEffect, useRef, useCallback } from 'react';
import BBBN_SystemBroadcastPicker from './native-components/BBBN_SystemBroadcastPicker';
import { WebView } from 'react-native-webview';
import { handleWebviewMessage } from './webview/message-handler';
import * as onScreenShareLocalIceCandidate from './events/onScreenShareLocalIceCandidate';
import * as onScreenShareSignalingStateChange from './events/onScreenShareSignalingStateChange';
import * as onBroadcastFinished from './events/onBroadcastFinished';

type BigBlueButtonTabletSdkProps = {
  url: string;
  style: ViewStyle;
  setCallState?: any;
  onError?: (content: any) => void;
  onSuccess?: (content: any) => void;
  onShouldStartLoadWithRequest?: (navState: any) => boolean;
  injectedJavaScript?: string;
  onNavigationStateChange?: (navState: any) => void;
  onOpenWindow?: (event: any) => void;
  renderTabItem?: ({
    item,
  }: {
    item: { id: number; url: string };
  }) => React.ReactElement | null;
  currentTab?: any;
  tabsData?: any;
};

const data = {
  instances: 0,
};

const renderPlatformSpecificComponents = () =>
  Platform.select({
    ios: <BBBN_SystemBroadcastPicker />,
    android: null,
  });

export const BigBlueButtonTablet = ({
  url,
  style,
  setCallState,
  onError,
  onSuccess,
  onShouldStartLoadWithRequest,
  injectedJavaScript,
  onNavigationStateChange,
  onOpenWindow,
  renderTabItem,
  currentTab,
  tabsData,
}: BigBlueButtonTabletSdkProps) => {
  const webViewRef = useRef<WebView>(null);
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

  useEffect(() => {
    const logPrefix = `[${thisInstanceId}] - ${url.substring(8, 16)}`;

    console.log(`${logPrefix} - addingListeners`);
    const listeners: EmitterSubscription[] = [];
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
  const renderItem = useCallback(
    ({ item }: { item: { id: number; url: string } }) => {
      if (renderTabItem) {
        return renderTabItem({ item });
      }
      return null;
    },
    [renderTabItem]
  );

  return (
    <SafeAreaView style={{ flex: 1 }}>
      {tabsData?.length > 1 && (
        <View style={{ flexDirection: 'row' }}>
          <FlatList
            horizontal
            data={tabsData}
            renderItem={renderItem}
            keyExtractor={(item) => item.id.toString()}
            initialNumToRender={tabsData.length}
            getItemLayout={(_, index) => ({
              length: 100,
              offset: 100 * index,
              index,
            })}
          />
        </View>
      )}
      <View style={{ flex: 1 }}>
        {renderPlatformSpecificComponents()}

        <WebView
          ref={webViewRef}
          source={{ uri: currentTab?.url || url }}
          style={{ ...style, flex: 1, marginTop: 0 }}
          contentMode="mobile"
          applicationNameForUserAgent="BigBlueButton-Tablet"
          allowsInlineMediaPlayback
          javaScriptEnabled
          mediaCapturePermissionGrantType="grant"
          scrollEnabled
          bounces
          // automaticallyAdjustContentInsets={false}
          // contentInsetAdjustmentBehavior="never"
          nestedScrollEnabled
          overScrollMode="always"
          // setSupportMultipleWindows={false}
          onOpenWindow={onOpenWindow}
          injectedJavaScriptBeforeContentLoaded={zoomOutBeforeLoad}
          injectedJavaScript={autoFitAfterLoad && injectedJavaScript}
          onMessage={(msg) =>
            handleWebviewMessage(0, webViewRef, msg, setCallState)
          }
          onShouldStartLoadWithRequest={onShouldStartLoadWithRequest}
          onNavigationStateChange={onNavigationStateChange}
          onLoadEnd={(content: any) => {
            if (typeof content.nativeEvent.code !== 'undefined') {
              onError?.(content);
            } else {
              onSuccess?.(content);
            }
          }}
        />
      </View>
    </SafeAreaView>
  );
};
