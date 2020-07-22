package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * Gyroscope
 */
public class GyroscopeService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_GYROSCOPE;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GyroscopeService.class, MyConstants.JOB_ID_GYROSCOPE, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
