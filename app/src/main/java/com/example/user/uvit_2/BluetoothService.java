package com.example.user.uvit_2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
/**
 * Created by user on 2016-10-06.
 */

public class BluetoothService {
    MySQLiteOpenHelper dbHelper;
    // Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;


    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;
    Set<BluetoothDevice> mDevices;
    int mPariedDeviceCount;

    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    byte[] readBuffer;
    int readBufferPosition;
    Thread mWorkerThread = null;

    BluetoothDevice mRemoteDevie;

    String syncheck;
    int MED = 300, EXPOSURE = 50, TARTGETS_VITAMID = 400;
    double CORRECTION_FACTOR_3 = 0.916817688;
    double CORRECTION_FACTOR_5 = 0.324731475;

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Constructors
    public BluetoothService(Activity ac, Handler h) {
        mActivity = ac;
        mHandler = h;

        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        dbHelper = new MySQLiteOpenHelper(mActivity);
    }

    /**
     * Check the Bluetooth support
     *
     * @return boolean
     */
    public boolean getDeviceState() {
        Log.i(TAG, "Check the Bluetooth support");

        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth is not available");
            return false;

        } else {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    /**
     * Check the enabled Bluetooth
     */
    public void enableBluetooth() { //checkBlueTooth()
        Log.i(TAG, "Check the enabled Bluetooth");
        if (btAdapter.isEnabled()) {
            // 기기의 블루투스 상태가 On인 경우
            Log.d(TAG, "Bluetooth Enable Now");
        } else {
            // 기기의 블루투스 상태가 Off인 경우
            Log.d(TAG, "Bluetooth Enable Request");

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);

            selectDevice();
        }
    }

    void selectDevice() {
        mDevices = btAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
            Toast.makeText(mActivity, "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            mActivity.finish(); // App 종료.
        }
        // 페어링된 장치가 있는 경우.
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName()); // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
        }
        listItems.add("취소");  // 취소 항목 추가.

        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.d(TAG, String.valueOf(item));
                if (item==2){}else{
                    // TODO Auto-generated method stub
                    connectToSelectedDevice(items[item].toString());}
            }

        }).setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
        Log.d(TAG, "alert 실행, ");
    }




    //  connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
    //  실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.

            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(MY_UUID);

//            Toast.makeText(mActivity, "mSocket 상태" + mSocket.getRemoteDevice(), Toast.LENGTH_LONG).show();
            Log.d(TAG, String.valueOf(mSocket.getRemoteDevice()));
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.


            if (mSocket.isConnected()) {
                Toast.makeText(mActivity, "Connect Success", Toast.LENGTH_LONG).show();
            }

            // 데이터 송수신을 위한 스트림 얻기.  // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            mOutputStream = mSocket.getOutputStream();// 1. 데이터를 보내기 위한 OutputStrem
            mInputStream = mSocket.getInputStream();// 2. 데이터를 받기 위한 InputStream

            // 데이터 수신 준비.
            beginListenForData();
            mWorkerThread.start();

        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(mActivity, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            e.getMessage();
            mActivity.finish();  // App 종료
        }
    }

    private BluetoothDevice getDeviceFromBondedList(String name) {


        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice bDevice = null;
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        // getName() : 단말기의 Bluetooth Adapter 이름을 반환
        for (BluetoothDevice selectedDevice : mDevices) {
            if (name.equals(selectedDevice.getName())) {
                bDevice = selectedDevice;
                break;
            }
        }
        return bDevice;
    }

    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    void beginListenForData() {
        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.

        // 문자열 수신 쓰레드.
        mWorkerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
                // interrupt() 메소드는 하던 일을 멈추는 메소드이다.
                // isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
                while (!Thread.currentThread().isInterrupted()) {
                    final StringBuffer sb = new StringBuffer();
                    try {
                        while (true) {
                            char ch = (char) mInputStream.read();
                            sb.append(ch);

                            if (ch == '\n') {
                                break;
                            }
                        }
                        String buffer = sb.toString();
                        String[] arr = buffer.split(",");

                        double temp = Double.valueOf(arr[0]);
                        double hum = Double.valueOf(arr[1]);
                        double uvi = Double.valueOf(arr[2]);
                        double uvb = Double.valueOf(arr[3]);
                        String action = UVAction(uvi);
                        double vitamind = VitaminD(uvb-0.78);
//                        double vitamind = 31.2;
                        Log.d(TAG, String.valueOf(vitamind));

                        ValueObject vo = new ValueObject(temp, hum, uvi, uvb-0.78, action, vitamind);

                        Intent intent = new Intent("SEND_DATA");
                        intent.putExtra("TIME", vo.getTime());
                        intent.putExtra("TEMP", vo.getTemp()); //key, value
                        intent.putExtra("HUM", vo.getHum());
                        intent.putExtra("UVI", vo.getUvi());
                        intent.putExtra("UVB", vo.getUvb());
                        intent.putExtra("ACTION", vo.getAction());
                        intent.putExtra("VITAMIND", vo.getVitamind());
                        mActivity.sendBroadcast(intent);
//                        Log.i(TAG, sb.toString());
                        dbHelper.addData(vo);
                        dbHelper.addTrendData(vo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mActivity.registerReceiver(myReceiver, new IntentFilter("PUSH_SYN"));
                    mActivity.registerReceiver(myReceiver_profile, new IntentFilter("SEND_PROFILE"));

                }
            }
            BroadcastReceiver myReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    syncheck = intent.getStringExtra("SYNCHECK");
                    try {
                        // 출력 스트림에 데이터를 저장한다
                        byte[] buffer = syncheck.getBytes();
                        mOutputStream.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            BroadcastReceiver myReceiver_profile = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    MED = Integer.valueOf(dbHelper.getSkintypeData());
                    String[] arr = dbHelper.getProfileData();
                    int exposureSum = Integer.valueOf(arr[4]) + Integer.valueOf(arr[5]);
                    EXPOSURE = exposureSum;
                    TARTGETS_VITAMID = Integer.valueOf(arr[6]);
                }
            };
        });
    }
    public String UVAction(double uvi) {
        String action = "낮음";
        if (uvi >= 3 && uvi < 5.5) {
            action = "보통";
        } else if (uvi >= 5.5 && uvi < 7.5) {
            action = "높음";
        } else if (uvi >= 7.5 && uvi < 10.5) {
            action = "매우높음";
        } else if (uvi >= 10.5) {
            action = "위험";
        }
        return action;
    }

    public double VitaminD(double uvb) {
        String[] arr = dbHelper.getData();
        double lastVitamind = Double.valueOf(arr[6]);
        double sumVitamind = 0.0;
        Log.d(TAG, "lasttt : " +String.valueOf(lastVitamind));

        double cvitamind = 0.0;

        if (uvb <= 0) {
            cvitamind = 0.0;
            sumVitamind = 0;
            sumVitamind += lastVitamind;
        } else {
            cvitamind = ((((uvb * CORRECTION_FACTOR_5) * 10) / MED) * EXPOSURE * 40);
            Log.d(TAG, String.valueOf(cvitamind));
            sumVitamind = (cvitamind / TARTGETS_VITAMID) * 100;
            sumVitamind += lastVitamind;
        }
        Log.d(TAG, "return : " + String.valueOf(sumVitamind));
        if (sumVitamind >= 100) sumVitamind = 100;

        return sumVitamind;
    }

    public void cancel() {
    }

}