package com.bigbluebuttontabletsdk.service;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bigbluebuttontabletsdk.BBBN_ScreenShareService;
import com.bigbluebuttontabletsdk.R;
import com.bigbluebuttontabletsdk.broadcastScreen.BBBSampleHandler;

public class FloatingWidgetService extends Service {

  private WindowManager windowManager;
  private View floatingWidget;
  private WindowManager.LayoutParams params;
  private int screenWidth, screenHeight;
  long dt;


  public class LocalBinder extends Binder {
    public FloatingWidgetService getService() {
      return FloatingWidgetService.this;
    }
  }

  private final IBinder binder = new LocalBinder();

  @Override
  public void onCreate() {
    super.onCreate();
    floatingWidget = LayoutInflater.from(this).inflate(R.layout.floating_widget, null);

    params = new WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Use TYPE_PHONE for versions below Oreo
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT);
    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    windowManager.addView(floatingWidget, params);
    // Specify the widget position initially
//    params.gravity = Gravity.TOP | Gravity.LEFT;

    // Get the screen width
    Display display = windowManager.getDefaultDisplay();
    screenWidth = display.getWidth();
    screenHeight = display.getHeight();

    params.x = screenWidth/2;
    params.y = screenHeight/2;



    floatingWidget.setOnTouchListener(new View.OnTouchListener() {
      private int initialX;
      private int initialY;
      private float initialTouchX;
      private float initialTouchY;

      private int prevX;
      private int prevY;


      @Override
      public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            initialX = params.x;
            initialY = params.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();

            dt = event.getEventTime();
            prevX = initialX;
            prevY = initialY;
            return true;
//          case MotionEvent.ACTION_BUTTON_PRESS:
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setComponent(new ComponentName(getPackageName(), "com.example.bigbluebuttontabletsdk.MainActivity"));
//            startActivity(intent);
          case MotionEvent.ACTION_UP:
//            int middle = screenWidth / 2;
//            float nearestEdge = params.x >= middle ? screenWidth : 0;
//            animateToEdge(nearestEdge);


            //   boolean fastMoveX = Math.abs(params.x - prevX) > 15;


            animateToEdge(prevX, prevY);
//            if (fastMoveX){
//             // animateToEdge(params.x - prevX < 0);
//            }else{
//             // animateToEdge(params.x < screenWidth / 2);
//            }
            return true;
          case MotionEvent.ACTION_MOVE:
            prevX = params.x;
            prevY = params.y;

            params.x = initialX + (int) (event.getRawX() - initialTouchX);
            params.y = initialY + (int) (event.getRawY() - initialTouchY);

            dt = event.getEventTime() - dt;
            Log.d("Param","ParamX"+params.x +"-->"+ params.y);
            windowManager.updateViewLayout(floatingWidget, params);
            return true;
        }
        return false;
      }
    });


  }

  private void animateToEdge(int prevX, int prevY) {
    int leftEdge = (screenWidth/2)* (-1) + (floatingWidget.getWidth()/2 ) + 10;
    int topEdge = (screenHeight/2)* (-1) + (floatingWidget.getHeight()/2 ) + 10;


    // Determine movement directions and closest edges
    boolean movedLeft = params.x - prevX < 0;
    boolean movedUp = params.y - prevY < 0;

    int speedX = Math.abs(params.x - prevX);
    int speedY = Math.abs(params.y - prevY);


    // Determine closest edge based on current position and last movement direction

    boolean toLeftEdge;
    boolean toTopEdge = movedUp;

    if (speedX > 25) {
      toLeftEdge = movedLeft;
    } else {
      toLeftEdge = params.x < 0 ;
    }

//    boolean toLeftEdge = movedLeft ? true : params.x < midX;
//    boolean toTopEdge = movedUp ? true : params.y < midY;

    // Calculate the overshoot targets and final edge positions
    int overshootX = toLeftEdge ? leftEdge : leftEdge * (-1);
    int overshootY = toTopEdge ? topEdge : topEdge * (-1);

    // Choose axis based on the shortest distance to the edge
//    boolean animateX = Math.abs(params.x - (toLeftEdge ? 0 : screenWidth)) < Math.abs(params.y - (toTopEdge ? 0 : screenHeight));

    ValueAnimator animatorX;
    ValueAnimator animatorY;
//    if (animateX) {
    // Animate horizontally
    animatorX = ValueAnimator.ofInt(params.x, overshootX);
    animatorX.addUpdateListener(animation -> {
      params.x = (Integer) animation.getAnimatedValue();
      windowManager.updateViewLayout(floatingWidget, params);
    });

    animatorX.setDuration(Math.max(450 - speedX , 100));
    animatorX.setInterpolator(new OvershootInterpolator(2.5f));
    animatorX.start();

    if (speedY > 25 || Math.abs(params.y) > Math.abs(topEdge)){

      animatorY = ValueAnimator.ofInt(params.y, overshootY);
      animatorY.addUpdateListener(animation -> {
        params.y = (Integer) animation.getAnimatedValue();
        windowManager.updateViewLayout(floatingWidget, params);
      });
      Log.d("speedY",String.valueOf((dt * Math.abs(params.y-overshootY)) / (speedY+1)));

      animatorY.setDuration(Math.max(600 - speedY, 150));
      animatorY.setInterpolator(new OvershootInterpolator(3.5f));

      animatorY.start();
    }



//    } else
//    if (fastMoveY){
//      // Animate vertically
//      animator = ValueAnimator.ofInt(params.y, overshootY);
//      animator.addUpdateListener(animation -> {
//        params.y = (Integer) animation.getAnimatedValue();
//        windowManager.updateViewLayout(floatingWidget, params);
//      });
//    }

//    animator.setInterpolator(new LinearInterpolator());
//    animator.addListener(new android.animation.AnimatorListenerAdapter() {
//      @Override
//      public void onAnimationEnd(android.animation.Animator animation) {
//        // Animate back to the nearest edge
//        ValueAnimator returnAnimator = animateX ? ValueAnimator.ofInt(params.x, finalX) : ValueAnimator.ofInt(params.y, finalY);
//        returnAnimator.addUpdateListener(returnAnim -> {
//          if (animateX) {
//            params.x = (Integer) returnAnim.getAnimatedValue();
//          } else {
//            params.y = (Integer) returnAnim.getAnimatedValue();
//          }
//          windowManager.updateViewLayout(floatingWidget, params);
//        });
//        returnAnimator.setDuration(200);
//        returnAnimator.start();
//      }
//    });
//    animator.start();

  }

  //  private void animateToEdge(float dx, float dy) {
//    TranslateAnimation anim = new TranslateAnimation(0, dx, 0, dy);
//    anim.setDuration(500); // Animation duration
//    anim.setFillAfter(false);
//    anim.setAnimationListener(new Animation.AnimationListener() {
//      @Override
//      public void onAnimationStart(Animation animation) {}
//
//      @Override
//      public void onAnimationRepeat(Animation animation) {}
//
//      @Override
//      public void onAnimationEnd(Animation animation) {
//        params.x += dx;
//        params.y += dy;
//        windowManager.updateViewLayout(floatingWidget, params);
//      }
//    });
//
//    floatingWidget.startAnimation(anim);
//  }
  private void addOverlayView() {
    if (floatingWidget != null && floatingWidget.getParent() == null) {
      windowManager.addView(floatingWidget, params);
    }
  }

  private void removeOverlayView() {
    if (floatingWidget != null && floatingWidget.getParent() != null) {
      windowManager.removeView(floatingWidget);
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (floatingWidget != null) windowManager.removeView(floatingWidget);
  }

  public void finishMeeting() {
    if (floatingWidget != null) {
      windowManager.removeView(floatingWidget);
      floatingWidget = null;
    }
  }

}
