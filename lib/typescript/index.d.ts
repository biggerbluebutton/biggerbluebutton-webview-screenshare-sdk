import { ViewStyle } from 'react-native';
import React from 'react';
type BigBlueButtonTabletSdkProps = {
    url: string;
    style: ViewStyle;
    onError?: (content: any) => void;
    onSuccess?: (content: any) => void;
    setCallState?: any;
    onShouldStartLoadWithRequest?: (navState: any) => boolean;
    injectedJavaScript?: string;
    onNavigationStateChange?: (navState: any) => void;
    onOpenWindow?: (event: any) => void;
    renderTabItem?: ({ item, }: {
        item: {
            id: number;
            url: string;
        };
    }) => React.ReactElement | null;
    currentTab?: any;
    tabsData?: any;
};
export declare const BigBlueButtonTablet: ({ url, style, onError, onSuccess, setCallState, onShouldStartLoadWithRequest, injectedJavaScript, onNavigationStateChange, onOpenWindow, renderTabItem, currentTab, tabsData, }: BigBlueButtonTabletSdkProps) => React.JSX.Element;
export {};
