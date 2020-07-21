package cn.edu.heuet.sensormng.service;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.Locale;

import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.MyConstants;

/**
 * AbstractSensorService
 */
public abstract class AbstractSensorService extends JobIntentService implements SensorEventListener {

    private String TAG = "";
    private int m_delay = MyConstants.SENSOR_DELAY;
    private String dirName = "";
    private String fileName = "";
    private FileUtils fileUtils = null;
    private SensorManager mSensorManager = null;
    private Sensor mSensor = null;

    abstract int getSensorType();

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        dirName = TAG.replace("Service", "");
        fileName = dirName.toLowerCase(Locale.getDefault());
        fileUtils = new FileUtils(dirName, fileName);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(getSensorType());
        mSensorManager.registerListener(this, mSensor, m_delay);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        StringBuilder sb = new StringBuilder();
        float[] values = event.values;
        for (int i = 0; i < values.length; i++) {
            float v = values[i];
            if (i > 0) {
                sb.append(",");
            }
            sb.append(v);
        }
        String msg = sb.toString().trim();
        fileUtils.appendLine(msg);

        Log.i(TAG, msg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
