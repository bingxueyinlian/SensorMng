package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * StepDetector
 */
public class StepDetectorService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_STEP_DETECTOR;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, StepDetectorService.class, ConstantUtils.JOB_ID_STEPDETECTOR, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}