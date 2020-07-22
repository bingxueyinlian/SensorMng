package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * GyroscopeUncalibrated
 */
public class GyroscopeUncalibratedService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GyroscopeUncalibratedService.class, ConstantUtils.JOB_ID_GYROSCOPEUNCALIBRATED, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
