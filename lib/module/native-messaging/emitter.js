import * as reactNative from 'react-native'; // ...

const emitter = reactNative.Platform.OS === 'ios' ? new reactNative.NativeEventEmitter(reactNative.NativeModules.ReactNativeEventEmitter) : reactNative.DeviceEventEmitter;
export default emitter;
//# sourceMappingURL=emitter.js.map