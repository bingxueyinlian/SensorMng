package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * Proximity
 */
public class ProximityService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_PROXIMITY;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ProximityService.class, ConstantUtils.JOB_ID_PROXIMITY, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
