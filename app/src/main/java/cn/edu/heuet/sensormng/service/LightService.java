package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * Light
 */
public class LightService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_LIGHT;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LightService.class, MyConstants.JOB_ID_LIGHT, work);
    }
}
