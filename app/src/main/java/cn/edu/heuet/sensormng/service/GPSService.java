package cn.edu.heuet.sensormng.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import java.util.ArrayList;

import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.ConstantUtils;
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
    private String minTime;
    private String minDistance;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GPSService.class, ConstantUtils.JOB_ID_GPS, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileUtils = new FileUtils(dirName, fileName);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // minTime:30,minDistance:0
        String config = fileUtils.getConfigInfo("gps", "minTime,minDistance");
        String[] tempArr = config.split(",");
        minTime = tempArr[0];
        minDistance = tempArr[1];
        if (minTime == null || minTime.length() == 0) {
            minTime = "30";
        }
        if (minDistance == null || minDistance.length() == 0) {
            minDistance = "0";
        }
    }

    @Override
    public void onDestroy() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        UpdateLocation(lastKnownLocation);
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                Integer.parseInt(minTime) * 1000,
                Integer.parseInt(minDistance), this, Looper.getMainLooper()
        );

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
        try {
            UpdateLocation(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.i(TAG, "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.i(TAG, "onProviderEnabled");
    }
}
