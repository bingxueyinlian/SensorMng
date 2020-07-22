package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * StepCounter
 */
public class StepCounterService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_STEP_COUNTER;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, StepCounterService.class, MyConstants.JOB_ID_STEPCOUNTER, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
