package cn.edu.heuet.sensormng.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.ConstantUtils;
import cn.edu.heuet.sensormng.StringUtils;

public class WifiService extends JobIntentService {
    private final String TAG = "WIFIService";
    private final String dirName = "Wifi";
    private final String fileName = "wifi";
    private FileUtils fileUtils = null;
    private WifiManager wifiManager = null;
    private Timer timer = null;
    private int cnt = 0;
    private String scanID = "";
    private List<String> scanedList = null;
    private IntentFilter filter;
    private String delay;
    private String period;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WifiService.class, ConstantUtils.JOB_ID_WIFI, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileUtils = new FileUtils(dirName, fileName);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        // delay:1,period:30
        String config = fileUtils.getConfigInfo("wifi", "delay,period");
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
        registerReceiver(mReceiver, filter);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    cnt++;
                    String cntStr = String.format(Locale.getDefault(), "%04d", cnt);
                    String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale
                            .getDefault()).format(new Date());
                    scanID = time + cntStr;
                    Log.i(TAG, scanID);
                    if (!wifiManager.isWifiEnabled())
                        wifiManager.setWifiEnabled(true);
                    scanedList = new ArrayList<String>();
                    boolean success = wifiManager.startScan();
                    if (success) {
                        Log.i(TAG, "扫描成功");
                    } else {
                        Log.i(TAG, "扫描失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Integer.parseInt(delay) * 1000, Integer.parseInt(period) * 1000);

        //防止服务退出
        long count = 0;
        while (count < Long.MAX_VALUE - 1) {
            try {
                Thread.sleep(10000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent
                    .getAction())) {
                List<ScanResult> resultList = wifiManager.getScanResults();

                if (resultList != null && resultList.size() > 0) {
                    String time = new SimpleDateFormat("yyyyMMddHHmmss,SSS",
                            Locale.getDefault()).format(new Date());
                    StringBuilder sbResult = new StringBuilder();
                    for (ScanResult sr : resultList) {
                        boolean isExist = checkExist(sr.BSSID);
                        if (isExist) {
                            continue;
                        }
                        String msg = convertToString(sr);
                        msg = time + "," + msg + "," + scanID + "\r\n";
                        sbResult.append(msg);
                    }
                    //
                    fileUtils.append(sbResult.toString());
                    Log.i(TAG, sbResult.toString());
                }
            }
        }

        private boolean checkExist(String bSSID) {
            if (scanedList == null) {
                scanedList = new ArrayList<String>();
            }
            if (scanedList.contains(bSSID)) {
                return true;
            } else {
                scanedList.add(bSSID);
                return false;
            }
        }

        private String convertToString(ScanResult scanResult) {

            String ssid = scanResult.SSID;
            if (ssid == null || ssid.trim().length() == 0) {
                ssid = "null";
            } else {
                ssid = ssid.trim().replace(" ", "").replace(",", "")
                        .replace("\"", "");
            }
            String bssid = scanResult.BSSID;
            if (bssid == null || bssid.trim().length() == 0) {
                bssid = "null";
            } else {
                bssid = bssid.trim().replace(" ", "").replace(",", "")
                        .replace("\"", "");
            }
            String capabilities = scanResult.capabilities;
            if (capabilities == null || capabilities.trim().length() == 0) {
                capabilities = "null";
            } else {
                capabilities = capabilities.trim().replace(" ", "")
                        .replace(",", "").replace("\"", "");
            }
            String level = scanResult.level + "";
            String frequency = scanResult.frequency + "";
            String timestamp = "";
            timestamp = scanResult.timestamp + "";
            ArrayList<String> dataList = new ArrayList<String>();
            dataList.add(ssid);
            dataList.add(bssid);
            dataList.add(capabilities);
            dataList.add(level);
            dataList.add(frequency);
            dataList.add(timestamp);

            return StringUtils.join(dataList, ",");
        }
    };
}
