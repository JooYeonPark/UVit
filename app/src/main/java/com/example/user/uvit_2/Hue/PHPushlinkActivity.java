package com.example.user.uvit_2.Hue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * Activity which gives hint for manual pushlink. needs to add <activity
 * android:theme="@android:style/Theme.Dialog" /> in manifest file
 * 
 * 
 */

public class PHPushlinkActivity{
    private ProgressBar pbar;
    private static final int MAX_TIME=30;
    private PHHueSDK phHueSDK;
    private boolean isDialogShowing;

    protected void onCreate(Bundle savedInstanceState) {
        isDialogShowing=false;
        phHueSDK = PHHueSDK.getInstance();
        
        pbar.setMax(MAX_TIME);
        
        phHueSDK.getNotificationManager().registerSDKListener(listener);
    }

    protected void onStop(){
        phHueSDK.getNotificationManager().unregisterSDKListener(listener);
    }

    public void incrementProgress() {
        pbar.incrementProgressBy(1);
    }
    
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> arg0) {}

        @Override
        public void onAuthenticationRequired(PHAccessPoint arg0) {}

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {}

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {}

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {}

        @Override
        public void onConnectionResumed(PHBridge arg0) {}

        @Override
        public void onError(int code, final String message) {
            if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                incrementProgress();
            }
            else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                incrementProgress();

                if (!isDialogShowing) {
                    isDialogShowing=true;
                }
                
            }

        } // End of On Error

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {}
    };
    
    public void onDestroy() {
        if (listener !=null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
    }
    
}
