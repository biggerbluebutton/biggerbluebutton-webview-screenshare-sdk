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
  private static final String TAG = "BBBSampleHandler-->";
  private static final String CHANNEL_ID = "ScreenShareChannel";
  private ScreenBroadcasterService mScreenBroadcasterService;
  private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
  private PreferencesUtils sharedPreferences;
  private boolean listenersRegistered = false;
  private boolean cleanedUp = false;

  public class LocalBinder extends Binder {
    public BBBSampleHandler getService() {
      return BBBSampleHandler.this;
    }
  }

  private final IBinder binder = new LocalBinder();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Utils.showLogs(TAG+ "onStartCommand "+ mScreenBroadcasterService);
    final String action = intent != null ? intent.getAction() : null;
    if ("STOP_SERVICE".equals(action)) {
      cleanupAndStop();
      return START_NOT_STICKY;
    }

    cleanupWebRTC();

    createNotificationChannel();
    Notification notification = buildNotification();
      startForeground(1128, notification);

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
    listenersRegistered = true;
    mScreenBroadcasterService = new ScreenBroadcasterService();
    mScreenBroadcasterService.initializePeerConnection(this,data);

  }

//  private void finishBroadcastGracefully() {
//    stopSelf();
//    stopForeground(true);
//  }

  private void unregisterPreferenceListener() {
    if (listenersRegistered && sharedPreferences != null && onSharedPreferenceChangeListener != null) {
      try { sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener); }
      catch (Exception ignored) {}
      onSharedPreferenceChangeListener = null;
      listenersRegistered = false;
    }
  }


  /** Fully tear down capturer, tracks, MediaProjection, PeerConnection, threads, etc. */
  private void cleanupWebRTC() {
    if (mScreenBroadcasterService != null) {
      try { mScreenBroadcasterService.clear(); } catch (Exception ignored) {}
      mScreenBroadcasterService = null;    // <— THIS was commented out in your code; keep it!
    }
  }

  private void cleanupAndStop() {
    cleanupWebRTC();
    unregisterPreferenceListener();
    try { stopForeground(true); } catch (Exception ignored) {}
    try { stopSelf(); } catch (Exception ignored) {}
  }

  private void finishBroadcastGracefully() {
    cleanupAndStop();
  }




  @Override
  public void onDestroy() {
    cleanupAndStop();
    super.onDestroy();
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }
  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
//    stopSelf();
    cleanupAndStop();
  }
}
