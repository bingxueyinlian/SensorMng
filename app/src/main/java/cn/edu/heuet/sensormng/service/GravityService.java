package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * Gravity
 */
public class GravityService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_GRAVITY;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GravityService.class, ConstantUtils.JOB_ID_GRAVITY, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
