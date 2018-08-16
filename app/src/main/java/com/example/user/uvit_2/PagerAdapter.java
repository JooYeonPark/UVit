package com.example.user.uvit_2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.user.uvit_2.Hue.FragmentLED;


/**
 * Created by user on 2016-10-06.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("main", "position : " +String.valueOf(position));
        switch (position) {
            case 0:
                FragmentMonitoring monitoringFragment = new FragmentMonitoring();
                return monitoringFragment;
            case 1:
                FragmentTrends trendsFragment = new FragmentTrends();
                return trendsFragment;
            case 2:
                FragmentLED ledFragment = new FragmentLED();
                return ledFragment;
            case 3:
                FragmentSetting settingFragment = new FragmentSetting();
                return settingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
