package com.example.user.uvit_2.Hue;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.user.uvit_2.R;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class FragmentLED extends Fragment {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    private  PHHome phHome;
    PHBridge bridge;

    private View view = null;

    private int ct, ill, red, green, blue;
    ToggleButton led;

    SeekBar bar_control_red ;
    SeekBar bar_control_green ;
    SeekBar bar_control_blue ;
    SeekBar bar_control_cct ;
    SeekBar bar_control_lux;

    public FragmentLED() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) view = inflater.inflate(R.layout.fragment_led, container, false);

         led = (ToggleButton) view.findViewById(R.id.led);

        final TextView tv_control_red = (TextView) view.findViewById(R.id.control_tv_red);
        final TextView tv_control_green = (TextView) view.findViewById(R.id.control_tv_green);
        final TextView tv_control_blue = (TextView) view.findViewById(R.id.control_tv_blue);

        final TextView tv_control_cct = (TextView) view.findViewById(R.id.control_tv_cct);
        final TextView tv_control_lux = (TextView) view.findViewById(R.id.control_tv_lux);


        SeekBar.OnSeekBarChangeListener seekbarAction = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                switch (seekBar.getId()) {
                    case R.id.control_bar_red:
                        tv_control_red.setText(progress + " %");
                        red = progress;
                        ledControl_RBG();
                        bar_control_cct.setProgress(0);
                        tv_control_cct.setText("2000 K");
                        break;
                    case R.id.control_bar_green:
                        tv_control_green.setText(progress + " %");
                        green = progress;
                        ledControl_RBG();
                        bar_control_cct.setProgress(0);
                        tv_control_cct.setText("2000 K");
                        break;
                    case R.id.control_bar_blue:
                        tv_control_blue.setText(progress + " %");
                        blue = progress;
                        ledControl_RBG();
                        bar_control_cct.setProgress(0);
                        tv_control_cct.setText("2000 K");
                        break;
                    case R.id.control_bar_cct:
                        tv_control_cct.setText(((progress * 45) + 2000) + " K");
                        ledControl_CCT();
                        bar_control_red.setProgress(0);
                        tv_control_red.setText("0 %");
                        bar_control_green.setProgress(0);
                        tv_control_green.setText("0 %");
                        bar_control_blue.setProgress(0);
                        tv_control_blue.setText("0 %");

                        ct = progress;
                        break;
                    case R.id.control_bar_lux:
                        tv_control_lux.setText((int) (progress * 2.5) + " Lux");
                        ill = progress;
                        ledControl_LUX();
                        break;
                }
            }
        };

        bar_control_red = (SeekBar) view.findViewById(R.id.control_bar_red);
        bar_control_green = (SeekBar) view.findViewById(R.id.control_bar_green);
        bar_control_blue = (SeekBar) view.findViewById(R.id.control_bar_blue);
        bar_control_red.setOnSeekBarChangeListener(seekbarAction);
        bar_control_green.setOnSeekBarChangeListener(seekbarAction);
        bar_control_blue.setOnSeekBarChangeListener(seekbarAction);
        bar_control_cct = (SeekBar) view.findViewById(R.id.control_bar_cct);
        bar_control_lux = (SeekBar) view.findViewById(R.id.control_bar_lux);
        bar_control_cct.setOnSeekBarChangeListener(seekbarAction);
        bar_control_lux.setOnSeekBarChangeListener(seekbarAction);
        led.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledonLights();
            }
        });


        phHueSDK = PHHueSDK.create();
        return view;
    }


    public void ledonLights() {
        bridge = phHueSDK.getSelectedBridge();

        PHLightState lightState = new PHLightState();

        Log.d(TAG, (String) led.getText());
        if (led.getText().equals("on")){
            lightState.setOn(false);
            ledOff();

            led.setBackgroundResource(R.drawable.img_control_feelux_off);
        }else if (led.getText().equals("off")){
            lightState.setOn(true);
            led.setBackgroundResource(R.drawable.img_control_feelux);
        }

        lightState.setBrightness(0);
        lightState.setHue(0);

        float[] xy = PHUtilities.calculateXYFromRGB(255, 255,255,  "LCT001");
        lightState.setX(xy[0]);
        lightState.setY(xy[1]);

        bridge.updateLightState("10", lightState, null);
//        bridge.updateLightState("11", lightState, null);

        // lightState.setHue(20000);// red 0, blue 25500, green 46920, white 65535
        // lightState.setSaturation(254); //채도 0 흭색, 254 포화
    }

    private void ledOff() {
        bar_control_red.setProgress(0);
        bar_control_green.setProgress(0);
        bar_control_blue.setProgress(0);
        bar_control_cct.setProgress(0);
        bar_control_lux.setProgress(0);
    }


    public void ledControl_RBG(){

            bridge = phHueSDK.getSelectedBridge();

            PHLightState lightState = new PHLightState();

            int r = (int) (red * 2.55);
            int g = (int) (green * 2.55);
            int b = (int) (blue * 2.55);
//            Log.d(TAG, r + ", " + g + ", " + b);

            float[] xy = PHUtilities.calculateXYFromRGB(r, g, b, "LCT001");
            lightState.setX(xy[0]);
            lightState.setY(xy[1]);
            bridge.updateLightState("10", lightState, null);
//            bridge.updateLightState("11", lightState, null);
        }

    public void ledControl_CCT(){
        // lightState.setCt(500); //색온도 153~500 (2000K~6500K)

        bridge = phHueSDK.getSelectedBridge();
        PHLightState lightState = new PHLightState();

        if (ct <=0) {
            lightState.setCt(500);
        } else {
            lightState.setCt(653-((ct*45)+2000)/13);
        }
        bridge.updateLightState("10", lightState, null);
//        bridge.updateLightState("11", lightState, null);
    }
    public void ledControl_LUX(){
        // lightState.setBrightness(0); // 조도 min 0, max 250

        bridge = phHueSDK.getSelectedBridge();
        PHLightState lightState = new PHLightState();

        if (ill <= 0) {
            lightState.setBrightness(0);
        } else {
            lightState.setBrightness((int) (ill * 2.5));
        }

        bridge.updateLightState("10", lightState, null);
//        bridge.updateLightState("11", lightState, null);
        Log.d(TAG, "BT : "+String.valueOf(lightState.getBrightness()));
    }
        PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };

        @Override
        public void onDestroy() {
            PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
