package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * Orientation
 */
public class OrientationService extends AbstractSensorService {

    @SuppressWarnings("deprecation")
    @Override
    int getSensorType() {
        return Sensor.TYPE_ORIENTATION;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, OrientationService.class, ConstantUtils.JOB_ID_ORIENTATION, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
