package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * Gravity
 */
public class GravityService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_GRAVITY;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GravityService.class, MyConstants.JOB_ID_GRAVITY, work);
    }
}
