package com.example.user.uvit_2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public  class FragmentSetting extends Fragment {
    private View view = null;
    Button btn_profile, btn_led, btn_alarm, btn_about;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_setting, container, false);

        btn_profile = (Button) view.findViewById(R.id.setting_profile);
        btn_alarm = (Button) view.findViewById(R.id.setting_alarm);
        btn_about = (Button) view.findViewById(R.id.setting_about);

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "Setting_ProfileActivity";
                startActivity(new Intent(view.getContext(), Setting_ProfileActivity.class));
            }
        });

        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "Setting_AlarmActivity";
                startActivity(new Intent(view.getContext(), Setting_AlarmActivity.class));
            }
        });
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = "Setting_AboutActivity";
                startActivity(new Intent(view.getContext(), Setting_AboutActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
