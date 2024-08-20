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
  onError?: (content: any) => void;
  onSuccess?: (content: any) => void;
  callState?: any;
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
  onError,
  onSuccess,
  callState,
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
          style={{ ...style, marginTop: 0 }}
          contentMode={'mobile'}
          onMessage={(msg: any) =>
            handleWebviewMessage(thisInstanceId, webViewRef, msg, callState)
          }
          onOpenWindow={onOpenWindow}
          applicationNameForUserAgent="BigBlueButton-Tablet"
          allowsInlineMediaPlayback={true}
          injectedJavaScript={injectedJavaScript}
          javaScriptEnabled={true}
          mediaCapturePermissionGrantType={'grant'}
          onShouldStartLoadWithRequest={onShouldStartLoadWithRequest}
          onNavigationStateChange={onNavigationStateChange}
          onLoadEnd={(content: any) => {
            if (typeof content.nativeEvent.code !== 'undefined') {
              if (onError) onError(content);
            } else {
              if (onSuccess) onSuccess(content);
            }
          }}
        />
      </View>
    </SafeAreaView>
  );
};
