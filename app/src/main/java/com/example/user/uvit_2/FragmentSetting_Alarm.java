package com.example.user.uvit_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.uvit_2.R;


public class FragmentSetting_Alarm extends Fragment {
    private View view = null;

    private String TAG = "alarm";

    private int BASE_SETTING_1 = 50;
    private int BASE_SETTING_2 = 8;
    private int BASE_SETTING_3 = 100;

    int sd_temp, sd_hum, sd_uvi, sd_vitamind, rss_temp, rss_hum;
    boolean rss_switch = true;

    SeekBar alarm_sd_temp, alarm_sd_hum, alarm_sd_uvi, alarm_sd_vitamind, alarm_rss_temp, alarm_rss_hum;

    boolean temp ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_setting_alarm, container, false);
        }

        final TextView sd_temp_values = (TextView) view.findViewById(R.id.sd_temp_values);
        final TextView sd_hum_values = (TextView) view.findViewById(R.id.sd_hum_values);
        final TextView sd_uvi_values = (TextView) view.findViewById(R.id.sd_uvi_values);
        final TextView sd_vitamind_values = (TextView) view.findViewById(R.id.sd_vitamind_values);
        final TextView rss_temp_values = (TextView) view.findViewById(R.id.rss_temp_values);
        final TextView rss_hum_values = (TextView) view.findViewById(R.id.rss_hum_values);

        SeekBar.OnSeekBarChangeListener seekbarAction = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case R.id.alarm_sd_temp:
                        sd_temp_values.setText(" " + progress);
                        break;
                    case R.id.alarm_sd_hum:
                        sd_hum_values.setText(" " + progress);
                        break;
                    case R.id.alarm_sd_uvi:
                        sd_uvi_values.setText(" " + progress);
                        break;
                    case R.id.alarm_sd_vitamind:
                        sd_vitamind_values.setText(" " + progress);
                        break;
                    case R.id.alarm_rss_temp:
                        rss_temp_values.setText(" " + progress);
                        break;
                    case R.id.alarm_rss_hum:
                        rss_hum_values.setText(" " + progress);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
        alarm_sd_temp = (SeekBar) view.findViewById(R.id.alarm_sd_temp);
        alarm_sd_hum = (SeekBar) view.findViewById(R.id.alarm_sd_hum);
        alarm_sd_uvi = (SeekBar) view.findViewById(R.id.alarm_sd_uvi);
        alarm_sd_vitamind = (SeekBar) view.findViewById(R.id.alarm_sd_vitamind);
        alarm_rss_temp = (SeekBar) view.findViewById(R.id.alarm_rss_temp);
        alarm_rss_hum = (SeekBar) view.findViewById(R.id.alarm_rss_hum);

        alarm_sd_temp.setProgress(BASE_SETTING_1);
        alarm_sd_hum.setProgress(BASE_SETTING_1);
        alarm_sd_uvi.setProgress(BASE_SETTING_2);
        alarm_sd_vitamind.setProgress(BASE_SETTING_3);
        alarm_rss_temp.setProgress(BASE_SETTING_1);
        alarm_rss_hum.setProgress(BASE_SETTING_1);

        alarm_sd_temp.setOnSeekBarChangeListener(seekbarAction);
        alarm_sd_hum.setOnSeekBarChangeListener(seekbarAction);
        alarm_sd_uvi.setOnSeekBarChangeListener(seekbarAction);
        alarm_sd_vitamind.setOnSeekBarChangeListener(seekbarAction);
        alarm_rss_temp.setOnSeekBarChangeListener(seekbarAction);
        alarm_rss_hum.setOnSeekBarChangeListener(seekbarAction);

        sd_temp = alarm_sd_temp.getProgress();
        sd_hum = alarm_sd_temp.getProgress();
        sd_uvi = alarm_sd_temp.getProgress();
        sd_vitamind = alarm_sd_temp.getProgress();
        rss_temp = alarm_sd_temp.getProgress();
        rss_hum = alarm_sd_temp.getProgress();

        ValueObject vo = new ValueObject(sd_temp, sd_hum, sd_uvi, sd_vitamind, rss_temp, rss_hum, rss_switch);

        Intent intent = new Intent("SEND_ALARM");
        intent.putExtra("SD_TEMP", vo.getSd_temp());
        intent.putExtra("SD_HUM", vo.getSd_hum());
        intent.putExtra("SD_UVI", vo.getSd_uvi());
        intent.putExtra("SD_VITAMIND", vo.getSd_vitamind());
        intent.putExtra("RSS_TEMP", vo.getRss_temp());
        intent.putExtra("RSS_HUM", vo.getRss_hum());
        intent.putExtra("RSS_SWITCH", vo.getRss_switch());
        view.getContext().sendBroadcast(intent);
        Log.i(TAG, sd_temp + ", " + sd_hum + ", " + sd_uvi + ", " + sd_vitamind + ", " + rss_temp + ", " + rss_hum + ", " + rss_switch);


        final Switch alarm_rss_switch = (Switch) view.findViewById(R.id.alarm_rss_switch);
        alarm_rss_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rss_switch = isChecked;
            }
        });

        Button alarm_save = (Button) view.findViewById(R.id.alarm_save);
        alarm_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sd_temp = alarm_sd_temp.getProgress();
                sd_hum = alarm_sd_hum.getProgress();
                sd_uvi = alarm_sd_uvi.getProgress();
                sd_vitamind = alarm_sd_vitamind.getProgress();
                rss_temp = alarm_rss_temp.getProgress();
                rss_hum = alarm_rss_hum.getProgress();

                ValueObject vo = new ValueObject(sd_temp, sd_hum, sd_uvi, sd_vitamind, rss_temp, rss_hum, rss_switch);

                Intent intent = new Intent("SEND_ALARM");
                intent.putExtra("SD_TEMP", vo.getSd_temp());
                intent.putExtra("SD_HUM", vo.getSd_hum());
                intent.putExtra("SD_UVI", vo.getSd_uvi());
                intent.putExtra("SD_VITAMIND", vo.getSd_vitamind());
                intent.putExtra("RSS_TEMP", vo.getRss_temp());
                intent.putExtra("RSS_HUM", vo.getRss_hum());
                intent.putExtra("RSS_SWITCH", vo.getRss_switch());
                view.getContext().sendBroadcast(intent);
                Log.i(TAG, sd_temp + ", " + sd_hum + ", " + sd_uvi + ", " + sd_vitamind + ", " + rss_temp + ", " + rss_hum + ", " + rss_switch);

                Toast.makeText(view.getContext(), "알람 설정이 완료되었습니다.",Toast.LENGTH_LONG).show();
            }

        });
        return view;
    }


    public void temp() {
        temp = true;
    }
}