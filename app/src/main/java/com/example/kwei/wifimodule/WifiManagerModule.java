package com.example.kwei.wifimodule;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import static android.content.Context.WIFI_SERVICE;


/*
TODO - Implement
BroadcastReceiver
WifiConfiguration - creation
ScanResult
WifiManager - startScan, stopScan
PermissionCheck - Fix

 */

public class WifiManagerModule extends ReactContextBaseJavaModule {

    static final int REQUEST_ACCESS_COARSE_LOCATION = 10;

    private static WifiManagerModule instance = null;

    private WifiManager mManager;

    private WifiManagerModule() {
        mManager = (WifiManager) getReactApplicationContext().getSystemService(WIFI_SERVICE);
    }

    @ReactMethod
    public static WifiManagerModule getInstance() {
        if (instance == null) {
            instance = new WifiManagerModule();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "WifiManagerModule";
    }

    @ReactMethod
    public void checkPermission(Callback errorCallback, Callback successCallback) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Custom permission dialog
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
                }
                // API 23+ can revoke permissions
            }
        }
        return;
    }

    /*
      @ReactMethod
  public void measureLayout(
      int tag,
      int ancestorTag,
      Callback errorCallback,
      Callback successCallback) {
    try {
      measureLayout(tag, ancestorTag, mMeasureBuffer);
      float relativeX = PixelUtil.toDIPFromPixel(mMeasureBuffer[0]);
      float relativeY = PixelUtil.toDIPFromPixel(mMeasureBuffer[1]);
      float width = PixelUtil.toDIPFromPixel(mMeasureBuffer[2]);
      float height = PixelUtil.toDIPFromPixel(mMeasureBuffer[3]);
      successCallback.invoke(relativeX, relativeY, width, height);
    } catch (IllegalViewOperationException e) {
      errorCallback.invoke(e.getMessage());
    }
  }
     */

}
