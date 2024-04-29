import type { MutableRefObject } from 'react';
import type { WebViewMessageEvent } from 'react-native-webview';
export declare function handleWebviewMessage(instanceId: Number, webViewRef: MutableRefObject<any>, event: WebViewMessageEvent): void;
declare const _default: {
    handleWebviewMessage: typeof handleWebviewMessage;
};
export default _default;
