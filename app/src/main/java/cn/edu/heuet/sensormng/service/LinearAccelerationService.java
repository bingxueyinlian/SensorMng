package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * LinearAcceleration
 */
public class LinearAccelerationService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_LINEAR_ACCELERATION;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LinearAccelerationService.class, ConstantUtils.JOB_ID_LINEARACCELERATION, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
