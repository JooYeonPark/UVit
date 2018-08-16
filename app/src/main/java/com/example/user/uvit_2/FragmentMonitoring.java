package com.example.user.uvit_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentMonitoring extends Fragment {

    private static final String TAG = "FragmentMonitoring";

    private View view = null;
    private MySQLiteOpenHelper db;
    private GpsInfo gpsService;
    private MySQLiteOpenHelper dbHelper;


    TextView locationText, tempText, humText, uviText, uvbText, syncText;

    TextView actionText, vitamindText;

    Button synButton;

    LinearLayout monitoring;

    String ctime;
    double temp, hum, uvi, uvb;
    String action;
    double vitamind;

    public FragmentMonitoring() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_monitoring, container, false);
        db = new MySQLiteOpenHelper(view.getContext());
        gpsService = new GpsInfo(view.getContext());
        dbHelper = new MySQLiteOpenHelper(view.getContext());

        monitoring = (LinearLayout) view.findViewById(R.id.monitoring);

        locationText = (TextView) view.findViewById(R.id.monitoring_location);
        tempText = (TextView) view.findViewById(R.id.monitoring_temp);
        humText = (TextView) view.findViewById(R.id.monitoring_hum);
        uviText = (TextView) view.findViewById(R.id.monitoring_UVI);
        uvbText = (TextView) view.findViewById(R.id.monitoring_UVB);
        vitamindText = (TextView) view.findViewById(R.id.monitoring_D);
//        actionText = (TextView) view.findViewById(R.id.monitoring_action);
        syncText = (TextView) view.findViewById(R.id.lastsync);

        synButton = (Button) view.findViewById(R.id.sync);

        if (gpsService.isGetLocation()) {
        } else {
            gpsService.showSettingsAlert();
        }
        locationText.setText(setGPS());

        ValueObject vo = new ValueObject("p");
        Intent intent = new Intent("PUSH_SYN");
        intent.putExtra("SYNCHECK", vo.getSyncheck());
        getActivity().sendBroadcast(intent);

        String[] lastArr= dbHelper.getData();

        ctime = lastArr[0];
        temp =  Double.valueOf(lastArr[1]);
        hum =  Double.valueOf(lastArr[2]);
        uvi =  Double.valueOf(lastArr[3]);
        uvb =  Double.valueOf(lastArr[4]);
        action = lastArr[5];
        vitamind =  Double.valueOf(lastArr[6]);

        setValues();
        synButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueObject vo = new ValueObject("p");
                Intent intent = new Intent("PUSH_SYN");
                intent.putExtra("SYNCHECK", vo.getSyncheck());
                getActivity().sendBroadcast(intent);
                setValues();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(myReceiver, new IntentFilter("SEND_DATA"));
        getActivity().registerReceiver(myReceiver_gps, new IntentFilter("SEND_GPS"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
        getActivity().unregisterReceiver(myReceiver_gps);
    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ctime = intent.getStringExtra("TIME");
            temp = intent.getDoubleExtra("TEMP", 0.0);
            hum = intent.getDoubleExtra("HUM", 0.0);
            uvi = intent.getDoubleExtra("UVI", 0.0);
            uvb = intent.getDoubleExtra("UVB", 0.0);
            action = intent.getStringExtra("ACTION");
            vitamind = intent.getDoubleExtra("VITAMIND", 0.0);
            //UVI 자외선 지수에 따라 배경이미지 변경
            if (uvi <= 2.0)       {monitoring.setBackgroundResource(R.drawable.back_1);setValues();}
            else if (uvi <= 5.0) {monitoring.setBackgroundResource(R.drawable.back_2);setValues();}
            else if (uvi <= 7.0) {monitoring.setBackgroundResource(R.drawable.back_3);setValues();}
            else if (uvi <= 10.0){monitoring.setBackgroundResource(R.drawable.back_4);setValues();}
            else if (uvi > 11.0) {monitoring.setBackgroundResource(R.drawable.back_5);setValues();}
        }
    };

    BroadcastReceiver myReceiver_gps = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String adr = intent.getStringExtra("ADDRESS");
            locationText.setText(adr);
        }
    };


    public void setValues() {
        int end=3;
        tempText.setText(String.valueOf(temp));
        humText.setText(String.valueOf(hum));
        uviText.setText(String.valueOf(uvi).substring(0,3));
        if (uvi>=10){end=4;}else{ end=3;};
        uviText.setText(String.valueOf(uvi).substring(0,end));

//        uvbText.setText(String.valueOf(uvb).substring(0,3));
//        actionText.setText(action);
        if (vitamind>=10){end=4;}else{ end=3;};
        if (vitamind>=100.0){ vitamindText.setText("100");}
        vitamindText.setText(String.valueOf(vitamind).substring(0,end));

        syncText.setText(ctime);
    }

    public String setGPS() {
        String address = null;
        double lat, lon;
        lon = gpsService.getLongitude();
        lat = gpsService.getLatitude();
        address = gpsService.getAddress(lat, lon);
        return address;
    }
}
