package cn.edu.heuet.sensormng.service;

import java.util.ArrayList;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.StringUtils;

/**
 * GPS
 */
public class GPSService extends Service implements LocationListener {
    private final String TAG = "GPSService";
    private final String dirName = "GPS";
    private String fileName = "gps";
    private FileUtils fileUtils = null;
    private LocationManager mLocationManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
