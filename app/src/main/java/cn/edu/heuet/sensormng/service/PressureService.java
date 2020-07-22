package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * Pressure
 */
public class PressureService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_PRESSURE;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, PressureService.class, MyConstants.JOB_ID_PRESSURE, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
