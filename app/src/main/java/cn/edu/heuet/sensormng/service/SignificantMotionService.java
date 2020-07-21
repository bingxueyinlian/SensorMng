package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * SignificantMotion
 */
public class SignificantMotionService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_SIGNIFICANT_MOTION;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SignificantMotionService.class, MyConstants.JOB_ID_SIGNIFICANTMOTION, work);
    }
}
