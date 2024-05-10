import {
  EmitterSubscription,
  Platform,
  ViewStyle,
  NativeModules,
} from 'react-native';
import React, { useEffect, useRef } from 'react';
import BBBN_SystemBroadcastPicker from './native-components/BBBN_SystemBroadcastPicker';
import { WebView } from 'react-native-webview';
import { handleWebviewMessage } from './webview/message-handler';
import * as onScreenShareLocalIceCandidate from './events/onScreenShareLocalIceCandidate';
import * as onScreenShareSignalingStateChange from './events/onScreenShareSignalingStateChange';
import * as onBroadcastFinished from './events/onBroadcastFinished';

type BigBlueButtonTabletSdkProps = {
  url: string;
  style: ViewStyle;
  onError?: any;
  onSuccess?: any;
  callState?: any;
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
}: BigBlueButtonTabletSdkProps) => {
  const webViewRef = useRef(null);
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

  const jsCode = `
  (function () {
    var originalLog = console.log;
    console.log = function () {
        // Check if there is at least an eleventh argument
        if (arguments.length > 10) {
            var msg = ''; 
            if (arguments[10].includes("Audio Joined")) { 
                msg = "callStarted"; 
            } else if (arguments[10].includes("Audio ended without issue")) { 
                msg = "callStopped"; 
            }

            // If msg is set, create a JSON object and send it
            if (msg) {
                var dataToSend = JSON.stringify({ method: msg });
                window.ReactNativeWebView.postMessage(dataToSend);    
            }
        }
        // Call the original console.log with all its arguments
        originalLog.apply(console, arguments);    
    };
})();
    `;

  return (
    <>
      {renderPlatformSpecificComponents()}
      {
        <WebView
          ref={webViewRef}
          source={{ uri: url }}
          style={{ ...style }}
          contentMode={'mobile'}
          onMessage={(msg: any) =>
            handleWebviewMessage(thisInstanceId, webViewRef, msg, callState)
          }
          applicationNameForUserAgent="BigBlueButton-Tablet"
          allowsInlineMediaPlayback={true}
          injectedJavaScript={jsCode}
          javaScriptEnabled={true}
          mediaCapturePermissionGrantType={'grant'}
          onLoadEnd={(content: any) => {
            /*in case of success, the property code is not defined*/
            if (typeof content.nativeEvent.code !== 'undefined') {
              if (onError) onError(content);
            } else {
              if (onSuccess) onSuccess(content);
            }
          }}
        />
      }
    </>
  );
};
