package com.example.user.uvit_2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016-09-17.
 */
public class AlarmService {// extends Service
    private Activity mActivity;
    private static final String TAG = "AlarmService";
    MySQLiteOpenHelper dbHelper;


    // 알람 설정 값
    int sd_temp, sd_hum, sd_uvi, sd_vitamind, rss_temp, rss_hum;
    boolean rss_switch = true;

    // smart device 값
    double temp, hum, uvi, vitamind;

    // rss 값
    int rss_db_temp, rss_db_hum;
    String rss_pty;

    public AlarmService(Activity ac) {
        mActivity = ac;
        mActivity.registerReceiver(myReceiver_alarm, new IntentFilter("SEND_ALARM"));
        mActivity.registerReceiver(myReceiver, new IntentFilter("SEND_DATA"));

        dbHelper = new MySQLiteOpenHelper(mActivity);

        TimerTask task_alarm = new TimerTask() {
            @Override
            public void run() {
                try {
                    //비교해서 알람을 뛰우면 되겠죠잉?
//                    dialog();
                    compare();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer_alarm = new Timer();
        timer_alarm.schedule(task_alarm, 600 *1000);//600 *
    }

    BroadcastReceiver myReceiver_alarm = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sd_temp = intent.getIntExtra("SD_TEMP", 0);
            sd_hum = intent.getIntExtra("SD_HUM", 0);
            sd_uvi = intent.getIntExtra("SD_UVI", 0);
            sd_vitamind = intent.getIntExtra("SD_VITAMIND", 0);
            rss_temp = intent.getIntExtra("RSS_TEMP", 0);
            rss_hum = intent.getIntExtra("RSS_HUM", 0);
            rss_switch = intent.getBooleanExtra("RSS_SWITCH", false);
            compare();
        }
    };
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            temp = intent.getDoubleExtra("TEMP", 0.0);
            hum = intent.getDoubleExtra("HUM", 0.0);
            uvi = intent.getDoubleExtra("UVI", 0.0);
            vitamind = intent.getDoubleExtra("VITAMIND", 0.0);
        }
    };

    public void getRss() {
        String[] arr = dbHelper.getRssData();
//        rss_db_temp = Integer.parseInt(arr[0]);
//        rss_pty = arr[1];
//        rss_db_hum = Integer.parseInt(arr[2]);

        rss_db_temp = 21;
        rss_pty = "0";
        rss_db_hum = 0;
    }

    public void dialog(String s) {
        String sb = "";
        sb = s;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity); //큰 다이얼로그

        // dialogBuilder.setIcon(R.drawable.i) 아이콘 넣을때.....

        dialogBuilder.setTitle("경고!");
        dialogBuilder.setNegativeButton(R.string.str_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                    }
                });
        dialogBuilder.setMessage(sb);
        dialogBuilder.show();

    }

    private void compare() {
        getRss();
        if (temp > sd_temp) dialog("현재 온도가 " + temp + "(℃) 이상 입니다!");
        if (hum > sd_hum) dialog("현재 습도가 " + hum + "(%) 이상 입니다!");
        if (uvi > sd_uvi) dialog("현재 자외선 지수가 " + uvi + "이상 입니다!");
        if (vitamind > sd_vitamind) dialog("현재 비타민 D 달성량이 " + vitamind + "(%) 이상 입니다!");
        if (rss_db_temp > rss_temp) dialog("현재 기상청 온도가 " + rss_db_temp + "(℃) 이상 입니다!"); Log.d(TAG,rss_db_temp+","+rss_temp);
        if (rss_db_hum > rss_hum) dialog("현재 기상청 습도가 " + rss_db_hum + "(%) 이상 입니다!");
        if (rss_switch == true) {
            if (rss_pty.equals("눈") )
                dialog("현재 기상청 날씨가 " + rss_pty + "입니다!");
        }


//        //알림(Notification)을 관리하는 NotificationManager 얻어오기
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        //알림(Notification)을 만들어내는 Builder 객체 생성
//        //API 11 버전 이하도 지원하기 위해 NotificationCampat 클래스 사용
//        //만약 minimum SDK가 API 11 이상이면 Notification 클래스 사용 가능
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        //Notification.Builder에게 Notification 제목, 내용, 이미지 등을 설정//////////////////////////////////////
//
//        builder.setSmallIcon(android.R.drawable.ic_dialog_email);//상태표시줄에 보이는 아이콘 모양
//        builder.setTicker("Notification"); //알림이 발생될 때 잠시 보이는 글씨
//
//        //상태바를 드래그하여 아래로 내리면 보이는 알림창(확장 상태바)의 아이콘 모양 지정
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_input_add));
//
//        builder.setContentTitle("Contents Title");    //알림창에서의 제목
//        builder.setContentText("Contents TEXT");   //알림창에서의 글씨
//
//        ///////////////////////////////////////////////////////////////////////////////////////////////////////
//
//        Notification notification = builder.build();   //Notification 객체 생성
//
//        manager.notify(1, notification);             //NotificationManager가 알림(Notification)을 표시
    }
}
