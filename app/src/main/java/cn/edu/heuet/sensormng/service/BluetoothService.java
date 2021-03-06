package cn.edu.heuet.sensormng.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.ConstantUtils;
import cn.edu.heuet.sensormng.StringUtils;

/**
 * Bluetooth
 */
public class BluetoothService extends JobIntentService {
    private String TAG = "BluetoothService";
    private final String dirName = "Bluetooth";
    private String fileName = "bluetooth";
    private FileUtils fileUtils = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Timer timer = null;
    private int cnt = 0;
    private String scanID = "";
    private ArrayList<String> arrDevices = null;
    private String delay;
    private String period;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BluetoothService.class, ConstantUtils.JOB_ID_BLUETOOTH, work);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        fileUtils = new FileUtils(dirName, fileName);

        // delay:1,period:30
        String config = fileUtils.getConfigInfo("bluetooth", "delay,period");
        String[] tempArr = config.split(",");
        delay = tempArr[0];
        period = tempArr[1];
        if (delay == null || delay.length() == 0) {
            delay = "1";
        }
        if (period == null || period.length() == 0) {
            period = "30";
        }
    }

    @Override
    public void onDestroy() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter = null;
        }
        unregisterReceiver(mReceiver);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mBluetoothAdapter == null) return;
                    if (!mBluetoothAdapter.isDiscovering()) {
                        cnt++;
                        String cntStr = String.format(Locale.getDefault(), "%04d",
                                cnt);
                        String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale
                                .getDefault()).format(new Date());
                        scanID = time + cntStr;
                        Log.i(TAG, scanID);
                        mBluetoothAdapter.startDiscovery();
                    } else {
                        Log.i(TAG, "before scan is running");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Integer.parseInt(delay) * 1000, Integer.parseInt(period) * 1000);

        long count = 0;
        while (count < Long.MAX_VALUE - 1) {//防止服务退出
            try {
                Thread.sleep(10000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                arrDevices = new ArrayList<String>();
                Log.i(TAG, "ACTION_DISCOVERY_STARTED");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i(TAG, "ACTION_FOUND");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    ArrayList<String> dataList = new ArrayList<String>();

                    String time = new SimpleDateFormat("yyyyMMddHHmmss,SSS",
                            Locale.getDefault()).format(new Date());
                    String deviceName = device.getName();
                    if (deviceName == null || deviceName.trim().length() == 0) {
                        deviceName = "null";
                    } else {
                        deviceName = deviceName.trim().replace(" ", "")
                                .replace(",", "").replace("\"", "");
                    }
                    dataList.add(time);
                    dataList.add(deviceName);
                    dataList.add(device.getAddress());
                    dataList.add(device.getBondState() + "");

                    dataList.add(device.getType() + "");
                    BluetoothClass bc = device.getBluetoothClass();
                    if (bc != null) {
                        dataList.add(bc.toString());
                        dataList.add(bc.getDeviceClass() + "");
                        dataList.add(bc.getMajorDeviceClass() + "");
                    } else {
                        dataList.add("");
                        dataList.add("");
                        dataList.add("");
                    }
                    String msg = StringUtils.join(dataList, ",");
                    boolean isExists = false;
                    for (String item : arrDevices) {
                        String temp = msg.replace(time, "");// ignore time
                        if (item.endsWith(temp)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (!isExists) {
                        arrDevices.add(msg);
                    }
                    Log.i(TAG, "DATA:" + msg);

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                Log.i(TAG, "ACTION_DISCOVERY_FINISHED");
                if (arrDevices != null && arrDevices.size() > 0) {
                    StringBuilder sbDevices = new StringBuilder();
                    for (String item : arrDevices) {
                        sbDevices.append(item).append(",").append(scanID).append("\r\n");
                    }
                    String data = sbDevices.toString();
                    fileUtils.append(data);
                    Log.i(TAG, "DATA:" + data);
                }
            }
        }
    };
}
