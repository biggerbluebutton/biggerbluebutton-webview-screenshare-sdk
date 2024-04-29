package com.bigbluebuttontabletsdk;

import static android.content.Context.BIND_AUTO_CREATE;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bigbluebuttontabletsdk.broadcastScreen.BBBSampleHandler;
import com.bigbluebuttontabletsdk.broadcastScreen.BigBlueButtonSDK;
import com.bigbluebuttontabletsdk.broadcastScreen.FullAudioService;
import com.bigbluebuttontabletsdk.utils.BBBSharedData;
import com.bigbluebuttontabletsdk.utils.EventEmitterData;
import com.bigbluebuttontabletsdk.utils.PreferencesUtils;
import com.bigbluebuttontabletsdk.utils.Utils;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionListener;
import com.google.gson.Gson;
import java.util.HashMap;



public class BBBN_ScreenShareService extends ReactContextBaseJavaModule implements ActivityEventListener, PermissionListener, LifecycleEventListener {
  public static final String REACT_CLASS = "BBBN_ScreenShareService";
  private MediaPlayer mediaPlayer;
  private ReactApplicationContext reactContext;

  private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;
  // List of mandatory application permissions.／
  private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
    "android.permission.RECORD_AUDIO", "android.permission.INTERNET","android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.SYSTEM_ALERT_WINDOW"};
  private static final int PERMISSION_REQUEST_CODE = 100;
  public static int sDeviceWidth;
  public static int sDeviceHeight;
  private BBBSampleHandler bbbSampleHandler;
  private static Intent mMediaProjectionPermissionResultData;
  boolean isAllDone = true;

  public BBBN_ScreenShareService(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addActivityEventListener(this);
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      DisplayMetrics metrics = new DisplayMetrics();
      currentActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
      sDeviceWidth = metrics.widthPixels;
      sDeviceHeight = metrics.heightPixels;
    }

  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @ReactMethod
  public void initializeScreenShare() {
    Utils.showLogs("initializeScreenShare "+getCurrentActivity());
   // activateAudioSession(true);
    Uri path = Uri.parse("android.resource://" + reactContext.getPackageName() + "/" + R.raw.music2);
    mediaPlayer = MediaPlayer.create(reactContext, path);
    mediaPlayer.start();
    playSoundInLoop();
//    Intent intent = new Intent(getCurrentActivity(), StartBroadcastActivity.class);
//    Objects.requireNonNull(getCurrentActivity()).startActivity(intent);
    checkAllDonePermission();
    EventEmitterData.emitEvent(reactContext,EventEmitterData.onBroadcastRequested, null);
  }

  @ReactMethod
  public void createScreenShareOffer(String stunTurnJson) {
    Utils.showLogs(stunTurnJson);
    PreferencesUtils.getInstance(reactContext).putString(BBBSharedData.SharedData.createScreenShareOffer, BBBSharedData.generatePayload(new HashMap<>()));
  }
  @ReactMethod
  public void setScreenShareRemoteSDP(String remoteSDP) {
    Utils.showLogs(remoteSDP);
    HashMap<String, String> properties = new HashMap<>();
    properties.put("sdp",remoteSDP);
    String stunTurnJson2 = BBBSharedData.generatePayload(properties);
    PreferencesUtils.getInstance(reactContext).putString(BBBSharedData.SharedData.setScreenShareRemoteSDP,stunTurnJson2);
//    fullAudioService.setRemoteSDP(remoteSDP);
//     EventEmitterData.emitEvent(reactContext,EventEmitterData.onSetFullAudioRemoteSDPCompleted, null);
  }
  @ReactMethod
  public void addScreenShareRemoteIceCandidate(String remoteCandidateJson) {
    Utils.showLogs(remoteCandidateJson);
    HashMap<String, String> properties = new HashMap<>();
    properties.put("candidate",remoteCandidateJson);
    String stunTurnJson2 = BBBSharedData.generatePayload(properties);
    PreferencesUtils.getInstance(reactContext).putString(BBBSharedData.SharedData.addScreenShareRemoteIceCandidate,stunTurnJson2);
  }
  @ReactMethod
  public void stopScreenShareBroadcastExtension() {
    Utils.showLogs("stopScreenShareBroadcastExtension");
    PreferencesUtils.getInstance(reactContext).putString(BBBSharedData.SharedData.onBroadcastStopped, BBBSharedData.generatePayload(new HashMap<>()));

  }


  private void activateAudioSession(boolean activate) {
    if (activate) {
      try {
        mediaPlayer.setLooping(true);
      } catch (Exception e) {
        Log.e(REACT_CLASS, "Error activating audio session", e);
      }
    } else {
      mediaPlayer.setLooping(false);
      mediaPlayer.stop();
      mediaPlayer.release();
    }
  }
  private void playSoundInLoop() {
    mediaPlayer.setOnCompletionListener(mp -> {
      Log.i(REACT_CLASS, "Restarting audio");
      mediaPlayer.seekTo(100);
      mediaPlayer.start();
    });
  }
  private void checkAndRequestPermissions() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      // Check each permission in the MANDATORY_PERMISSIONS array
      for (String permission : MANDATORY_PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(currentActivity, permission) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(currentActivity, MANDATORY_PERMISSIONS, PERMISSION_REQUEST_CODE);
          isAllDone = false;
          return; // Exit the method after requesting the permission
        }
      }
    }
  }

  private void checkAllDonePermission(){
    Activity currentActivity = getCurrentActivity();

    Utils.showLogs("checkAllDonePermission ..."+isAllDone);
    if(isAllDone){
      startScreenCapture();
    }else {
      if (currentActivity != null) {
        new AlertDialog.Builder(currentActivity)
          .setMessage("Something went wrong with permissions, Please check your permission in app settings and allow all permissions")
          .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
              Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              Uri uri = Uri.fromParts("package", currentActivity.getPackageName(), null);
              intent.setData(uri);
              currentActivity.startActivity(intent);
            }
          })
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
              paramDialogInterface.dismiss();
              currentActivity.finish();
            }
          }).show();
      }
    }
  }
  private void startScreenCapture() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      MediaProjectionManager mediaProjectionManager =
        (MediaProjectionManager)currentActivity.getApplication().getSystemService(
          Context.MEDIA_PROJECTION_SERVICE);
      currentActivity.startActivityForResult(
        mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
      Utils.showLogs("startScreenCapture");
    }
  }

  private final ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      Utils.showLogs("onServiceConnected ...");
   //   fullAudioService.createOffer(reactContext);
      BBBSampleHandler.LocalBinder binder = (BBBSampleHandler.LocalBinder) iBinder;
      bbbSampleHandler = binder.getService();
      bbbSampleHandler.startToObserveListeners(mMediaProjectionPermissionResultData);

    //  Activity currentActivity = getCurrentActivity();
//      if (currentActivity != null) {
//        currentActivity.finish();
//      }
    }
    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
  };

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    Utils.showLogs("outside ...");
    if (requestCode == CAPTURE_PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      BigBlueButtonSDK.initialize(activity, reactContext);
      EventEmitterData.emitEvent(reactContext, EventEmitterData.onBroadcastStarted, null);
      // Store or process the result data as needed
      mMediaProjectionPermissionResultData = data;
      Utils.showLogs("Inside ...");
      Intent serviceIntent = new Intent(reactContext, BBBSampleHandler.class);
      serviceIntent.putExtra("resultCode", resultCode);
      serviceIntent.putExtra("data", data);
      reactContext.startForegroundService(serviceIntent);
      reactContext.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

    }

  }



  @Override
  public void onNewIntent(Intent intent) {

  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      Gson gson = new Gson();
      Utils.showLogs("onRequestPermissionsResult: " + gson.toJson(grantResults));
      checkAllDonePermission();
      return true; // return true if the listener has consumed the event
    }
    return false;
  }

  @Override
  public void onHostResume() {
    Utils.showLogs("Module is active, similar to onStart of an Activity");
    checkAndRequestPermissions();
  }

  @Override
  public void onHostPause() {

  }

  @Override
  public void onHostDestroy() {
    Utils.showLogs("StartBroadcastActivity onDestroy");
  }
}
