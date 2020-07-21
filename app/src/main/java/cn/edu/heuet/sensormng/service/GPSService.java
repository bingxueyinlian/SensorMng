package cn.edu.heuet.sensormng.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import java.util.ArrayList;

import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.MyConstants;
import cn.edu.heuet.sensormng.StringUtils;

/**
 * GPS
 */
public class GPSService extends JobIntentService implements LocationListener {
    private final String TAG = "GPSService";
    private final String dirName = "GPS";
    private final String fileName = "gps";
    private FileUtils fileUtils = null;
    private LocationManager mLocationManager = null;
    private long count = 0;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GPSService.class, MyConstants.JOB_ID_GPS, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileUtils = new FileUtils(dirName, fileName);
        // minTime:30,minDistance:0
        String config = fileUtils.getConfigInfo("gps", "minTime,minDistance");
        String[] tempArr = config.split(",");
        String minTime = tempArr[0];
        String minDistance = tempArr[1];
        if (minTime == null || minTime.length() == 0) {
            minTime = "30";
        }
        if (minDistance == null || minDistance.length() == 0) {
            minDistance = "0";
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                Integer.parseInt(minTime) * 1000,
                Integer.parseInt(minDistance), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        while (count < Long.MAX_VALUE - 1) {//防止服务退出
            try {
                Thread.sleep(1000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void UpdateLocation(Location location) {
        if (location != null) {
            ArrayList<String> dataList = new ArrayList<String>();
            dataList.add(location.getLongitude() + "");
            dataList.add(location.getLatitude() + "");
            dataList.add(location.getAltitude() + "");
            dataList.add(location.getBearing() + "");
            dataList.add(location.getSpeed() + "");
            dataList.add(location.getAccuracy() + "");

            String msg = StringUtils.join(dataList, ",");
            fileUtils.appendLine(msg);

            assert msg != null;
            Log.i(TAG, msg);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        UpdateLocation(location);
    }
}
