import { ViewStyle } from 'react-native';
import React from 'react';
declare type BigBlueButtonTabletSdkProps = {
    url: string;
    style: ViewStyle;
    onError?: (content: any) => void;
    onSuccess?: (content: any) => void;
    callState?: any;
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
export declare const BigBlueButtonTablet: ({ url, style, onError, onSuccess, callState, onShouldStartLoadWithRequest, injectedJavaScript, onNavigationStateChange, onOpenWindow, renderTabItem, currentTab, tabsData, }: BigBlueButtonTabletSdkProps) => JSX.Element;
export {};
