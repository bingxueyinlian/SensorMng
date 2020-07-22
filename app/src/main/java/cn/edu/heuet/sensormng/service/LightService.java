package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * Light
 */
public class LightService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_LIGHT;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LightService.class, ConstantUtils.JOB_ID_LIGHT, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
