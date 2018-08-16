package com.example.user.uvit_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016-10-06.
 */
public class Setting_AboutActivity extends AppCompatActivity {
    private static final String TAG = "about";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_about);
        getWindow().setWindowAnimations(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View view = getLayoutInflater().inflate(R.layout.setting_tab, null);
        View view1 = getLayoutInflater().inflate(R.layout.tab_main, null);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.about_tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_setting_alarm).setCustomView(view));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.img_uvit).setCustomView(view1));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setTabMode(TabLayout.MODE_FIXED);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.about_container);
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> list = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            list.add(new FragmentSetting_About());

        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).toString();
        }
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}