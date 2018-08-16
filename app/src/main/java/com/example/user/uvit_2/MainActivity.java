package com.example.user.uvit_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.uvit_2.FragmentSetting;
import com.example.user.uvit_2.FragmentSetting_Alarm;
import com.example.user.uvit_2.AlarmService;
import com.example.user.uvit_2.GpsInfo;
import com.example.user.uvit_2.Hue.PHHome;
import com.example.user.uvit_2.MySQLiteOpenHelper;
import com.example.user.uvit_2.RssService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private static final String TAG = "Main";
    private BluetoothService btService;
    private GpsInfo gpsService;
    private RssService rssService;
    private AlarmService alarmService;
    private FragmentSetting_Alarm alarm_fragment;
    private PHHome phHome;
    MySQLiteOpenHelper dbHelper;
    double lat, lon;
    TabLayout tabLayout;

    private static String PREF_NAME = "com.pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setWindowAnimations(0); //화면전화 효과 제거
        checkFirst();

        // 안드로이드 버전 3.0이상에서 인터넷 연결시 에러 안나게하는 법..
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View view = getLayoutInflater().inflate(R.layout.tab_main, null);
        View view1 = getLayoutInflater().inflate(R.layout.tab_main, null);
        View view2 = getLayoutInflater().inflate(R.layout.tab_main, null);
        View view3 = getLayoutInflater().inflate(R.layout.tab_main, null);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


        tabLayout.addTab(tabLayout.newTab());
        tabLayout.getTabAt(0).setCustomView(view);
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_monitoring);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_trends).setCustomView(view1));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_led).setCustomView(view2));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_setting).setCustomView(view3));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        Log.d("tabcount","tabcount : "+tabLayout.getTabCount());
        final PagerAdapter adapter = new com.example.user.uvit_2.PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//
        if (btService == null) {
            btService = new BluetoothService(this, mHandler);
        }
        if (btService.getDeviceState()) {// 블루투스가 지원 가능한 기기일 때
            btService.enableBluetooth();
            btService.selectDevice();

        } else {
            finish();
        }
        gpsService = new GpsInfo(this);
        rssService = new RssService(this);
        dbHelper = new MySQLiteOpenHelper(this);
        alarmService = new AlarmService(this);
        phHome = new PHHome(this); //hue
//        dbHelper.readFromFile(this);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    rssService.addRSSData(rssService.parseRss(rssService.getGrid(gpsService.getLatitude(), gpsService.getLongitude())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, rssService.nextTime(), 3 * 3600 * 1000);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rss_gps_location:
                Dialog_GPS(gpsService.getLatitude(), gpsService.getLongitude());
                break;
            case R.id.rss_kma_location:
                Dialog_KMA();
                break;
            case R.id.action_rss:
                break;
            case R.id.actio_gps:
                Toast.makeText(getApplicationContext(), "현재위치 업데이트 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("SEND_GPS");
                lat = gpsService.getLatitude();
                lon = gpsService.getLongitude();
                ValueObject vo = new ValueObject(lat, lon, gpsService.getAddress(lat, lon));
                intent.putExtra("LAT", vo.getLat());
                intent.putExtra("LON", vo.getLon());
                intent.putExtra("ADDRESS", vo.getAddress());
                this.sendBroadcast(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            finish();
            toast.cancel();
        }
    }

    private void Dialog_GPS(double lat, double lon) {
        final String address = gpsService.getAddress(lat, lon);
        final String xml = rssService.getGrid(lat, lon);
        final String[] selectedText = {null};
        final List<String> listItem = new ArrayList<String>();

        String[][] rssArr = new String[18][11];
        rssArr = rssService.parseRss(xml);

        for (int i = 0; i < rssArr.length; i++) {
            if ("모레".equals(rssArr[i][0])) break;
            listItem.add(rssArr[i][0] + " " + rssArr[i][1]);
        }
        final CharSequence[] rssItems = listItem.toArray(new CharSequence[listItem.size()]);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this); //큰 다이얼로그
        final AlertDialog.Builder inBuilder = new AlertDialog.Builder(this); // 작은 다이얼로그
        inBuilder.setCancelable(true);
        // dialogBuilder.setIcon(R.drawable.i) 아이콘 넣을때.....
        dialogBuilder.setTitle(address + "의 날씨");
        dialogBuilder.setNegativeButton(R.string.str_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                    }
                });
        dialogBuilder.setItems(rssItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int item) {
                selectedText[0] = rssItems[item].toString();
                String text = selectedText[0];
                Log.d(TAG, item + ", " + String.valueOf(text));

                inBuilder.setTitle(address + "\n" + text + "의 날씨");
                StringBuilder sb = rssService.getRssData(item, xml); // 리스트에서 선택한 날씨 갖고 오기~

                inBuilder.setMessage(sb);
                inBuilder.setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                                dialog.dismiss();
                            }
                        });
                inBuilder.setNegativeButton(R.string.str_back,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                                dialogBuilder.show();
                            }
                        });
                inBuilder.show();
            }
        });
        dialogBuilder.show();
    }

    private void Dialog_KMA() {
        final ListView listView;
        final String[] dialogTitle = {null};
        final List<String> listItem = new ArrayList<String>();
        final List<String> listItem2 = new ArrayList<String>();
        final List<String> listItem3 = new ArrayList<String>();
        final String[][] topArr = kmaArr(rssService.json("top"));

//        LayoutInflater inflater=getLayoutInflater();
//        final View dialogView= inflater.inflate(R.layout.dialog_address, null);
        final AlertDialog.Builder Builder = new AlertDialog.Builder(this); //큰 다이얼로그
        final AlertDialog.Builder Builder2 = new AlertDialog.Builder(this); // 작은 다이얼로그
        final AlertDialog.Builder Builder3 = new AlertDialog.Builder(this); // 작은 작은 다이얼로그
        final AlertDialog.Builder Builder4 = new AlertDialog.Builder(this); // 작은 작은 다이얼로그
//        Builder.setView(dialogView);  Builder2.setView(dialogView);  Builder3.setView(dialogView);  Builder4.setView(dialogView);
//        TextView title = (TextView) dialogView.findViewById(R.id.dialog_title);
//        ListView list = (ListView) dialogView.findViewById(R.id.dialog_list);
        Builder4.setCancelable(true);

        for (int i = 0; i < topArr.length; i++) {
            if (topArr[i][1] == null) break;
            listItem.add(topArr[i][1]);
        }
        final CharSequence[] rssItems = listItem.toArray(new CharSequence[listItem.size()]);

        // outBuilder.setIcon(R.drawable.i) 아이콘 넣을때.....
        Builder.setTitle("지역을 선택하세요.");
//        title.setText("지역을 선택하세요.");
        Builder.setNegativeButton(R.string.str_back,
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                    }
                });
        Builder.setItems(rssItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int item) {
                dialogTitle[0] = topArr[item][1] + " ";
                Builder2.setTitle(dialogTitle[0]);
                Log.d("dailog",dialogTitle[0]);

                final String[][] mdlArr = kmaArr(rssService.json(topArr[item][0]));
                listItem2.clear();
                for (int i = 0; i < mdlArr.length; i++) listItem2.add(mdlArr[i][1]);
                final CharSequence[] rssItems2 = listItem2.toArray(new CharSequence[listItem2.size()]);

                Builder2.setNegativeButton(R.string.str_back,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                                Builder.show();
                            }
                        });

                Builder2.setItems(rssItems2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item2) {
                        final String[][] leafArr = kmaArr_leaf(rssService.json_leaf(mdlArr[item2][0]));
                        dialogTitle[0] += mdlArr[item2][1] + " ";
                        Builder3.setTitle(dialogTitle[0]);

                        listItem3.clear();
                        for (int i = 0; i < leafArr.length; i++) listItem3.add(leafArr[i][1]);
                        final CharSequence[] rssItems3 = listItem3.toArray(new CharSequence[listItem3.size()]);

                        Builder3.setNegativeButton(R.string.str_back,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface, int i) {
                                        Builder2.show();
                                    }
                                });
                        Builder3.setItems(rssItems3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int item3) {
                                String xml = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=" + leafArr[item3][2] + "&gridy=" + leafArr[item3][3];
                                dialogTitle[0] += leafArr[item3][1] + " ";
                                Builder4.setTitle(dialogTitle[0]);

                                StringBuilder sb = rssService.getRssData(0, xml); // 리스트에서 선택한 날씨 갖고 오기~

                                Builder4.setMessage(sb);
                                Builder4.setPositiveButton(R.string.str_ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialoginterface, int i) {
                                                dialog.dismiss();
                                            }
                                        });
                                Builder4.setNegativeButton(R.string.str_back,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialoginterface, int i) {
                                                Builder3.show();
                                            }
                                        });
                                Builder4.show();
                            }
                        });
                        Builder3.show();
                    }
                });
                Builder2.show();
            }
        });
        Builder.show();
    }


    public String[][] kmaArr(StringBuffer sb) {
        String buffer = sb.toString();
        String[] arr = buffer.split(",");
        Log.d(TAG, String.valueOf(arr.length));

        String[][] Arr = new String[arr.length / 2][2];
        int count = 0;
        for (int i = 0; i < arr.length; i += 2) {
            Arr[count][0] = arr[i];
            Arr[count][1] = arr[i + 1];
//            Log.d(TAG, count + " : " + Arr[count][0] + ", " + Arr[count][1]);
            count++;
        }
        return Arr;
    }

    public String[][] kmaArr_leaf(StringBuffer sb) {
        String buffer = sb.toString();
        String[] arr = buffer.split(",");
        Log.d(TAG, String.valueOf(arr.length));

        String[][] Arr = new String[arr.length / 4][4];
        int count = 0;
        for (int i = 0; i < arr.length; i += 4) {
            Arr[count][0] = arr[i];
            Arr[count][1] = arr[i + 1];
            Arr[count][2] = arr[i + 2];
            Arr[count][3] = arr[i + 3];
            Log.d(TAG, count + " : " + Arr[count][0] + ", " + Arr[count][1] + ", " + Arr[count][2] + ", " + Arr[count][3]);
            count++;
        }
        return Arr;
    }



    private void checkFirst() {
        Context mContext = this;

        SharedPreferences pref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String start = pref.getString("start", "");
        if (start.equals("")) {
            Log.d(TAG, "처음처음");

            dbHelper = new MySQLiteOpenHelper(this);
            dbHelper.basicTable();
            dbHelper.readFromFile(this);

            alarm_fragment = new FragmentSetting_Alarm();
            alarm_fragment.temp();

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("start", "start");
            editor.commit();

        } else {
            Log.d(TAG, "첫실행ㄴㄴ");
        }
    }
}