package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

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
        enqueueWork(context, OrientationService.class, MyConstants.JOB_ID_ORIENTATION, work);
    }
}
