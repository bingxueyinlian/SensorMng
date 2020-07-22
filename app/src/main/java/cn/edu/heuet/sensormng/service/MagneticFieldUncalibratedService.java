package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * MagneticFieldUncalibrated
 */
public class MagneticFieldUncalibratedService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MagneticFieldUncalibratedService.class, MyConstants.JOB_ID_MAGNETICFIELDUNCALIBRATED, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
