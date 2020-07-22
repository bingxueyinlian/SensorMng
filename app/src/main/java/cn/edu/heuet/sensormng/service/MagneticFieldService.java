package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * MagneticField
 */
public class MagneticFieldService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_MAGNETIC_FIELD;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MagneticFieldService.class, MyConstants.JOB_ID_MAGNETICFIELD, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
