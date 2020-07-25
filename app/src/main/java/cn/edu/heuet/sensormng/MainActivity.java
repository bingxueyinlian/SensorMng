package cn.edu.heuet.sensormng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.edu.heuet.sensormng.service.AccelerometerService;
import cn.edu.heuet.sensormng.service.AmbientTemperatureService;
import cn.edu.heuet.sensormng.service.BluetoothService;
import cn.edu.heuet.sensormng.service.GPSService;
import cn.edu.heuet.sensormng.service.GSMService;
import cn.edu.heuet.sensormng.service.GameRotationVectorService;
import cn.edu.heuet.sensormng.service.GeoMagneticRotationVectorService;
import cn.edu.heuet.sensormng.service.GravityService;
import cn.edu.heuet.sensormng.service.GyroscopeService;
import cn.edu.heuet.sensormng.service.GyroscopeUncalibratedService;
import cn.edu.heuet.sensormng.service.LightService;
import cn.edu.heuet.sensormng.service.LinearAccelerationService;
import cn.edu.heuet.sensormng.service.MagneticFieldService;
import cn.edu.heuet.sensormng.service.MagneticFieldUncalibratedService;
import cn.edu.heuet.sensormng.service.OrientationService;
import cn.edu.heuet.sensormng.service.PressureService;
import cn.edu.heuet.sensormng.service.ProximityService;
import cn.edu.heuet.sensormng.service.RelativeHumidityService;
import cn.edu.heuet.sensormng.service.RotationVectorService;
import cn.edu.heuet.sensormng.service.SignificantMotionService;
import cn.edu.heuet.sensormng.service.StepCounterService;
import cn.edu.heuet.sensormng.service.StepDetectorService;
import cn.edu.heuet.sensormng.service.TemperatureService;
import cn.edu.heuet.sensormng.service.WifiService;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {
    private static final int PERMISSION_REQUEST_CODE = 10001;
    private final String TAG = MainActivity.class.getSimpleName();
    private String appPackageName = null;
    private ListView listView = null;
    private MyAdapter myAdapter;
    private final int TYPE_GPS = 100;
    private final int TYPE_GSM = 200;
    private final int TYPE_BLUETOOTH = 300;
    private final int TYPE_WIFI = 400;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        appPackageName = getPackageName();
        Log.i(TAG, appPackageName);
        listView = (ListView) findViewById(R.id.list_view);
        this.setTitle(getString(R.string.app_name_title));

        //请求权限
        permissionTask();
    }

    private void initData() {
        FillListView();
        setServiceRunningStatus();
        listView.setAdapter(myAdapter);

        acquireWakeLock();
    }

    public void acquireWakeLock() {
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        releaseWakeLock();
        //Acquire new wake lock
        //mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mytag:PARTIAL_WAKE_LOCK");
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "mytag:SCREEN_DIM_WAKE_LOCK");//保持屏幕高亮,可变暗
        mWakeLock.acquire(24 * 60 * 60 * 1000L);
    }

    public void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setServiceRunningStatus();
    }

    private void FillListView() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> fullList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        ArrayList<Map<String, Object>> dataList = new ArrayList<>();
        // GPS
        Map<String, Object> map = new HashMap<>();
        map.put("SensorName", "GPS");
        map.put("SensorType", TYPE_GPS);
        map.put("IsRunning", false);
        dataList.add(map);
        // GSM
        map = new HashMap<>();
        map.put("SensorName", "GSM");
        map.put("SensorType", TYPE_GSM);
        map.put("IsRunning", false);
        dataList.add(map);
        // BLUETOOTH
        map = new HashMap<>();
        map.put("SensorName", "Bluetooth");
        map.put("SensorType", TYPE_BLUETOOTH);
        map.put("IsRunning", false);
        dataList.add(map);
        // TYPE_WIFI
        map = new HashMap<>();
        map.put("SensorName", "WIFI");
        map.put("SensorType", TYPE_WIFI);
        map.put("IsRunning", false);
        dataList.add(map);

        // Sensor
        for (Sensor sensor : fullList) {
            map = new HashMap<>();
            map.put("SensorName", sensor.getName());
            map.put("SensorType", sensor.getType());
            map.put("IsRunning", false);
            dataList.add(map);
        }
        myAdapter = new MyAdapter(this, dataList);
    }

    private void setServiceRunningStatus() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : list) {
            ComponentName service = runningServiceInfo.service;
            String packageName = service.getPackageName();
            if (appPackageName.equals(packageName)) {
                String className = service.getShortClassName().replace(".service.", "");
                updateRunningStatus(className);
            }
        }

    }

    private void updateRunningStatus(String serviceClassName) {
        if (myAdapter == null) return;
        serviceClassName = serviceClassName.replace("Service", "").toLowerCase(Locale.getDefault());
        for (int i = 0; i < myAdapter.getCount(); i++) {
            Map<String, Object> map = myAdapter.getItem(i);
            String sensorName = MapUtils.getString(map, "SensorName");
            assert sensorName != null;
            sensorName = sensorName.replace(" ", "").toLowerCase(Locale.getDefault());
            if (sensorName.contains(serviceClassName)) {
                myAdapter.updateRunningStatus(i, true);
                break;
            }
        }
    }

    private boolean setItemStatus(int position, boolean isStart) {
        if (myAdapter == null) return false;
        Map<String, Object> map = myAdapter.getItem(position);
        int sensorType = MapUtils.getIntValue(map, "SensorType");
        return changeService(sensorType, isStart);
    }

    private boolean changeService(int sensorType, boolean isStart) {
        switch (sensorType) {
            case TYPE_GPS:
                if (isStart) {
                    LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (locManager == null) {
                        Toast.makeText(this, "No GPS!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(this, "Please Open GPS！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, TYPE_GPS);
                        return false;
                    }
                }
                GPSService.enqueueWork(this, new Intent());
                break;
            case TYPE_GSM:
                GSMService.enqueueWork(this, new Intent());
                break;
            case TYPE_BLUETOOTH:
                if (isStart) {
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (btAdapter == null) {
                        Toast.makeText(this, "No Bluetooth!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (!btAdapter.isEnabled()) {
                        boolean success = btAdapter.enable();
                        if (!success) {
                            Toast.makeText(this, "enable Bluetooth fail", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
                BluetoothService.enqueueWork(this, new Intent());
                break;
            case TYPE_WIFI:
                if (isStart) {
                    WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
                    if (wifiManager == null) {
                        Toast.makeText(this, "No Wifi!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if (!isWiFiSettingOn()) {
                        boolean success = wifiManager.setWifiEnabled(true);
                        if (!success) {
                            Toast.makeText(this, "enable Wifi fail", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
                WifiService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_ACCELEROMETER:
                AccelerometerService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                AmbientTemperatureService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                GameRotationVectorService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                GeoMagneticRotationVectorService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_GRAVITY:
                GravityService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_GYROSCOPE:
                GyroscopeService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                GyroscopeUncalibratedService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_LIGHT:
                LightService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                LinearAccelerationService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                MagneticFieldService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                MagneticFieldUncalibratedService.enqueueWork(this, new Intent());
                break;
            //noinspection deprecation
            case Sensor.TYPE_ORIENTATION:
                OrientationService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_PRESSURE:
                PressureService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_PROXIMITY:
                ProximityService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                RelativeHumidityService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                RotationVectorService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                SignificantMotionService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_STEP_COUNTER:
                StepCounterService.enqueueWork(this, new Intent());
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                StepDetectorService.enqueueWork(this, new Intent());
                break;
            //noinspection deprecation
            case Sensor.TYPE_TEMPERATURE:
                TemperatureService.enqueueWork(this, new Intent());
                break;
            default:
                break;
        }
        return true;
    }

    public boolean isWiFiSettingOn() {
        boolean isOn = false;
        try {
            isOn = Settings.Global.getInt(this.getContentResolver(), Settings.Global.WIFI_ON) != 0;
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return isOn;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                    this,
                    hasStoragePermission() + "",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private boolean hasStoragePermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    public void permissionTask() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.CHANGE_WIFI_STATE
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            initData();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_request),
                    PERMISSION_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleDenied:" + requestCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyAdapter extends BaseAdapter {

        private ArrayList<Map<String, Object>> mData;
        private LayoutInflater mInflater;

        public MyAdapter(Context context, ArrayList<Map<String, Object>> data) {
            mData = data;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void updateRunningStatus(int position, boolean isRunning) {
            mData.get(position).put("IsRunning", isRunning);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = mInflater.inflate(R.layout.listitem, null);
            }
            bindView(position, v);
            return v;
        }

        private void bindView(int position, View v) {
            Map<String, Object> dataSet = mData.get(position);
            if (dataSet == null) {
                return;
            }
            String sName = MapUtils.getString(dataSet, "SensorName");
            TextView tvSensorName = (TextView) v
                    .findViewById(R.id.tvSensorName);
            Switch switchItem = (Switch) v.findViewById(R.id.switchItem);
            tvSensorName.setText(sName);

            boolean isRunning = MapUtils.getBooleanValue(dataSet, "IsRunning");
            switchItem.setOnCheckedChangeListener(null);
            switchItem.setChecked(isRunning);
            switchItem.setOnCheckedChangeListener(new SwitchCheckedChangeListener(position));
        }

        private class SwitchCheckedChangeListener implements
                CompoundButton.OnCheckedChangeListener {
            private int position;

            public SwitchCheckedChangeListener(int pos) {
                position = pos;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean res = setItemStatus(position, isChecked);
                if (res) {
                    updateRunningStatus(position, isChecked);
                }
            }
        }
    }
}