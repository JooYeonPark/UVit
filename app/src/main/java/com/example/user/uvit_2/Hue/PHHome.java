package com.example.user.uvit_2.Hue;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

/**
 * PHHomeActivity - The starting point in your own Hue App.
 * <p>
 * For first time use, a Bridge search (UPNP) is performed and a list of all available bridges is displayed (and clicking one of them shows the PushLink dialog allowing authentication).
 * The last connected Bridge IP Address and Username are stored in SharedPreferences.
 * <p>
 * For subsequent usage the app automatically connects to the last connected bridge.
 * When connected the MyApplicationActivity Activity is started.  This is where you should start implementing your Hue App!  Have fun!
 * <p>
 * For explanation on key concepts visit: https://github.com/PhilipsHue/PhilipsHueSDK-Java-MultiPlatform-Android
 */
public class PHHome {
    private Activity mActivity;


    private PHHueSDK phHueSDK;
    public static final String TAG = "QuickStart";
    private HueSharedPreferences prefs;

    private boolean lastSearchWasIPScan = false;
    String lastUsername;
    String lastIpAddress;


    public PHHome(Activity ac) {
        Log.d(TAG, "QuickStart 시작!");
        mActivity = ac;

        phHueSDK = PHHueSDK.create();

        phHueSDK.setDeviceName(android.os.Build.MODEL);

        phHueSDK.getNotificationManager().registerSDKListener(listener);

        Log.d("ktest", mActivity.getPackageName());
        prefs = HueSharedPreferences.getInstance(mActivity);

        lastIpAddress = prefs.getLastConnectedIPAddress();
        lastUsername = prefs.getUsername();
        Log.d(TAG,lastIpAddress);
        Log.d(TAG,lastUsername);

        if (lastIpAddress != null && !lastIpAddress.equals("")) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
                phHueSDK.connect(lastAccessPoint);
                Log.d(TAG,"connect success");
            }
        } else {  // First time use, so perform a bridge search.
            doBridgeSearch();
        }


    }

    // Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found. " + accessPoint.size());

            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                Log.d(TAG, "맞짜?" + accessPoint.get(0));

                connectBridge(phHueSDK.getAccessPointsFound().get(0));
            }
    }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
//            Log.w(TAG, "On CacheUpdated"); update 할때마다~~~~~~~~~~~~~~~~~~

        }

        @Override
        public void onBridgeConnected(PHBridge phBridge, String s) {

        }

        public void onBridgeConnected(PHBridge phBridge) {
            phHueSDK.setSelectedBridge(phBridge);
            phHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(lastUsername);
            Log.w(TAG, "onBridgeConnected");

        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);

        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);
            Toast.makeText(mActivity,"on Error Called : " + code + ":" + message,Toast.LENGTH_LONG).show();

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            } else if (code == PHHueError.AUTHENTICATION_FAILED || code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ");

            } else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) {  // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan = true;
                }
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError : parsingErrorsList) {
                Log.e(TAG, "ParsingError : " + parsingError.getMessage());
            }
        }
    };

    private void onDestroy() {
        if (listener !=null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
        phHueSDK.disableAllHeartbeat();
    }

    public void connectBridge(PHAccessPoint phAccessPoint){

        PHAccessPoint accessPoint = phAccessPoint;
        Log.d(TAG,"맞짜?"+ accessPoint);

        PHBridge connectedBridge = phHueSDK.getSelectedBridge();
        Log.d(TAG, "붹붹붹"+String.valueOf(connectedBridge));

        if (connectedBridge != null) {
            String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
            if (connectedIP != null) {   // We are already connected here:-
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
        }
        phHueSDK.connect(accessPoint);
    }




    public void doBridgeSearch() {
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
    }

    // Starting the main activity this way, prevents the PushLink Activity being shown when pressing the back button.
    public void startMainActivity() {
//        Intent intent = new Intent(getApplicationContext(), MyApplicationActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
//        startActivity(intent);

        Log.d(TAG,"///끝인데....");
    }

}