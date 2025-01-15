package com.bigbluebuttontabletsdk.broadcastScreen;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bigbluebuttontabletsdk.R;
import com.bigbluebuttontabletsdk.utils.BBBSharedData;
import com.bigbluebuttontabletsdk.utils.PreferencesUtils;
import com.bigbluebuttontabletsdk.utils.Utils;

import org.webrtc.IceCandidate;

public class BBBSampleHandler extends Service {
  private static final String TAG = "BBBSampleHandler";
  private static final String CHANNEL_ID = "ScreenShareChannel";
  private ScreenBroadcasterService mScreenBroadcasterService;
  private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
  private PreferencesUtils sharedPreferences;

  public class LocalBinder extends Binder {
    public BBBSampleHandler getService() {
      return BBBSampleHandler.this;
    }
  }

  private final IBinder binder = new LocalBinder();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Utils.showLogs(TAG+ "onStartCommand ");
    if (intent != null) {
      String action = intent.getAction();
      if ("STOP_SERVICE".equals(action)) {
        // Stop the service
        stopSelf();
      }
    }
    // Create the notification channel
    createNotificationChannel();
    // Create a notification
    Notification notification = buildNotification();
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//      startForeground(1122, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
//    } else {
      startForeground(1128, notification);
//    }

    return START_NOT_STICKY;
  }

  // Method to create the notification channel
  private void createNotificationChannel() {
    CharSequence name = "ForegroundServiceChannel";
    String description = "Channel for Foreground Service";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
    channel.setDescription(description);
    NotificationManager notificationManager = getSystemService(NotificationManager.class);
    notificationManager.createNotificationChannel(channel);
  }
  private Notification buildNotification() {
    // Create a notification
    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Screen sharing")
      .setContentText("Your screen share is running in background")
      .setSmallIcon(R.drawable.icon)

      .build();

    return notification;
  }
  public void startToObserveListeners(Intent data) {
    sharedPreferences = PreferencesUtils.getInstance(this);
    onSharedPreferenceChangeListener = (sharedPreferences1, key) -> {
      switch (key) {
        case BBBSharedData.SharedData.createScreenShareOffer:
          Utils.showLogs(TAG+"Observer detected a createScreenShareOffer request!");
          mScreenBroadcasterService.createOffer();// here data set inside create offer
          break;
        case BBBSharedData.SharedData.setScreenShareRemoteSDP:
          Utils.showLogs(TAG+ "Observer detected a setScreenShareRemoteSDP request!");
          mScreenBroadcasterService.setRemoteSDP(Utils.extractSdpFromPayload(sharedPreferences1.getString(key, null)));
          break;
        case BBBSharedData.SharedData.addScreenShareRemoteIceCandidate:
          Utils.showLogs(TAG+ "Observer detected a addScreenShareRemoteIceCandidate request!");
          String payload = Utils.extractDataFromPayload(sharedPreferences1.getString(key, null),"candidate");
          IceCandidate remoteCandidate = (IceCandidate) Utils.fromJson(payload,IceCandidate.class);
          mScreenBroadcasterService.addRemoteCandidate(remoteCandidate);
          break;
        case BBBSharedData.SharedData.onApplicationTerminated:
        case BBBSharedData.SharedData.onBroadcastStopped:
          Utils.showLogs(TAG+ "Observer detected a onApplicationTerminated or onBroadcastStopped request!");
          finishBroadcastGracefully();
          break;
        default:
          Utils.showLogs(TAG+ "Unknown key: " + key);
          break;
      }
    };
    sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    mScreenBroadcasterService = new ScreenBroadcasterService();
    mScreenBroadcasterService.initializePeerConnection(this,data);

  }

//  private void finishBroadcastGracefully() {
//    stopSelf();
//    stopForeground(true);
//  }
  private void finishBroadcastGracefully() {
    stopSelf();
    stopForeground(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // Clean up resources here
    stopForeground(true); // Stop the foreground service
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    stopSelf();
  }
}
