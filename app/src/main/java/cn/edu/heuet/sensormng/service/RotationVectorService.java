package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * RotationVector
 */
public class RotationVectorService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_ROTATION_VECTOR;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RotationVectorService.class, MyConstants.JOB_ID_ROTATIONVECTOR, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
